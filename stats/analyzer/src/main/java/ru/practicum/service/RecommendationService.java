package ru.practicum.service;

import ru.practicum.grpc.proto.InteractionsCountRequestProto;
import ru.practicum.grpc.proto.RecommendedEventProto;
import ru.practicum.grpc.proto.SimilarEventsRequestProto;
import ru.practicum.grpc.proto.UserPredictionsRequestProto;
import java.util.stream.Stream;

public interface RecommendationService {
    Stream<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request);

    Stream<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request);

    Stream<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request);
}
