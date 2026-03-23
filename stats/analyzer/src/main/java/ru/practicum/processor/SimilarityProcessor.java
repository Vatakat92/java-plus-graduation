package ru.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.kafka.avro.EventSimilarityAvro;
import ru.practicum.config.KafkaClient;
import ru.practicum.handler.SimilarityHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimilarityProcessor {
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    private final KafkaClient kafkaClient;
    private final SimilarityHandler similarityHandler;

    public void start() {
        Consumer<Long, SpecificRecordBase> consumer = kafkaClient.getConsumerSimilarity();

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            consumer.subscribe(List.of(kafkaClient.getTopicsProperties().getStatsEventsSimilarityV1()));

            while(true) {
                ConsumerRecords<Long, SpecificRecordBase> records = consumer.poll(kafkaClient.getPollTimeout());

                int count = 0;
                for (ConsumerRecord<Long, SpecificRecordBase> record : records) {
                    handleRecord(record);
                    manageOffsets(record, count, consumer);
                    count++;
                }
                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Error while processing event similarities: ", e);
        } finally {
            try {
                consumer.commitSync(currentOffsets);
            } finally {
                log.info("Consumer closing.");
                consumer.close();
            }
        }
    }

    private void manageOffsets(ConsumerRecord<Long, SpecificRecordBase> record, int count,
                               Consumer<Long, SpecificRecordBase> consumer) {
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

    private void handleRecord(ConsumerRecord<Long, SpecificRecordBase> record) {
        if (record.value() instanceof EventSimilarityAvro eventSimilarityAvro) {
            log.info("Similarity of events received: eventA = {}, eventB = {}, score = {}",
                    eventSimilarityAvro.getEventA(), eventSimilarityAvro.getEventB(), eventSimilarityAvro.getScore());
            similarityHandler.handle(eventSimilarityAvro);
        }
    }
}
