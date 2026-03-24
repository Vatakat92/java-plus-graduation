package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.kafka.avro.EventSimilarityAvro;
import ru.practicum.kafka.avro.UserActionAvro;
import ru.practicum.config.KafkaClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregatorProcessor {

    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    private final KafkaClient kafkaClient;
    private final SimilarityCalculator calculator;

    public void start() {
        Consumer<Long, SpecificRecordBase> consumer = kafkaClient.getConsumer();
        Producer<Long, SpecificRecordBase> producer = kafkaClient.getProducer();

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            consumer.subscribe(List.of(kafkaClient.getTopicsProperties().getStatsUserActionV1()));

            while(true) {
                ConsumerRecords<Long, SpecificRecordBase> records = consumer.poll(kafkaClient.getPollTimeout());

                int count = 0;
                for (ConsumerRecord<Long, SpecificRecordBase> record : records) {
                    handleRecord(record, producer);
                    manageOffsets(record, count, consumer);
                    count++;
                }
                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("An error occurred while processing user actions.", e);
        } finally {
            try {
                producer.flush();
                consumer.commitSync(currentOffsets);
            } finally {
                log.info("Consumer closed.");
                consumer.close();
                log.info("Producer closed.");
                producer.close();
            }
        }
    }

    private void manageOffsets(ConsumerRecord<Long, SpecificRecordBase> record, int count, Consumer<Long, SpecificRecordBase> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Error while fixing offsets: {}", offsets, exception);
                }
            });
        }
    }


    private void handleRecord(ConsumerRecord<Long, SpecificRecordBase> record, Producer<Long, SpecificRecordBase> producer) {

        if (record.value() instanceof UserActionAvro action) {
            List<EventSimilarityAvro> similarities = calculator.calculateSimilarity(action);
            if (similarities.isEmpty()) return;

            for (EventSimilarityAvro similarity : similarities) {
                long timestamp = similarity.getTimestamp().toEpochMilli();
                ProducerRecord<Long, SpecificRecordBase> similarityRecord = new ProducerRecord<>(
                        kafkaClient.getTopicsProperties().getStatsEventsSimilarityV1(),
                        null,
                        timestamp,
                        similarity.getEventA(),
                        similarity
                );

                producer.send(similarityRecord, (metadata, ex) -> {
                    if (ex != null) {
                        log.error("Error sending event affinity for events eventA={} and eventB={}: {}",
                                similarity.getEventA(), similarity.getEventB(), ex.getMessage(), ex);
                    } else {
                        log.info("Similarity of events eventA={} and eventB={} successfully sent: partition={}, offset={}",
                                similarity.getEventA(), similarity.getEventB(), metadata.partition(), metadata.offset());
                    }
                });
            }
        } else {
            log.warn("Received unknown message: {}", record.value());
        }
    }
}
