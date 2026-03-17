package ru.practicum.handlers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiErrors {
    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private List<String> errors;
    private String message;
    private String reason;
    private String status;
    private String timestamp;

    public static ApiErrors of(HttpStatus status, String reason, String message, List<String> errors) {
        return ApiErrors.builder()
                .errors(errors == null ? List.of() : errors)
                .message(message)
                .reason(reason)
                .status(status.name())
                .timestamp(now())
                .build();
    }

    public static ApiErrors of(HttpStatus status, String reason, String message) {
        return of(status, reason, message, List.of());
    }

    public static ApiErrors badRequest(String message) {
        return of(HttpStatus.BAD_REQUEST, "Incorrectly made request.", message);
    }

    private static String now() {
        return LocalDateTime.now().format(F);
    }

}
