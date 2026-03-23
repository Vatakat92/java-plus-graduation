package ru.practicum.clients;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.practicum.grpc.proto.InteractionsCountRequestProto;
import ru.practicum.grpc.proto.RecommendationsControllerGrpc;
import ru.practicum.grpc.proto.RecommendedEventProto;
import ru.practicum.grpc.proto.UserPredictionsRequestProto;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AnalyzerClient {
    @GrpcClient("analyzer")
    private RecommendationsControllerGrpc.RecommendationsControllerBlockingStub client;

    public Map<Long, Double> getRecommendationsForUserId(long userId, int maxResults) {
        UserPredictionsRequestProto request = UserPredictionsRequestProto.newBuilder()
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();
        try {
            Iterator<RecommendedEventProto> iterator = client.getRecommendationsForUser(request);
            Map<Long, Double> result = new HashMap<>();
            iterator.forEachRemaining(proto -> result.put(proto.getEventId(), proto.getScore()));
            return result;
        } catch (Exception e) {
            log.error("Error getting recommendations for user: {}", e.getMessage());
        }
        return Map.of();
    }

    public Map<Long, Double> getInteractionsCount(List<Long> eventIds) {
        InteractionsCountRequestProto request = InteractionsCountRequestProto.newBuilder()
                .addAllEventId(eventIds)
                .build();
        try {
            Iterator<RecommendedEventProto> iterator = client.getInteractionsCount(request);
            Map<Long, Double> result = new HashMap<>();
            iterator.forEachRemaining(proto -> result.put(proto.getEventId(), proto.getScore()));
            return result;
        } catch (Exception e) {
            log.error("Error getting interactions count for events: {}", e.getMessage());
        }
        return Map.of();
    }
}