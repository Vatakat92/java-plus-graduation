package ru.practicum.handlers;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.stream.Collectors;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practicum.exceptions.EmailAlreadyExistsException;
import ru.practicum.exceptions.NotFoundException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class UserExceptionHandler {

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({
            MethodArgumentNotValidException.class
    })
    public ApiErrors handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> String.format("Field: %s. Error: %s. Value: %s",
                        err.getField(), err.getDefaultMessage(), err.getRejectedValue()))
                .collect(Collectors.joining("; "));

        return ApiErrors.of(BAD_REQUEST,
                "Incorrectly made request.",
                msg
        );
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({
            ConstraintViolationException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class,
            IllegalArgumentException.class,
    })
    public ApiErrors handleBadRequest(Exception ex) {
        return ApiErrors.of(BAD_REQUEST,
                "Incorrectly made request.",
                ex.getMessage()
        );
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler({
            NotFoundException.class
    })
    public ApiErrors handleNotFound(Exception ex) {
        return ApiErrors.of(NOT_FOUND,
                "The required object was not found.",
                ex.getMessage()
        );
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({
            ValidationException.class
    })
    public ApiErrors handleCustomValidation(ValidationException ex) {
        return ApiErrors.badRequest(
                ex.getMessage()
        );
    }

    @ResponseStatus(CONFLICT)
    @ExceptionHandler({
            EmailAlreadyExistsException.class
    })
    public ApiErrors handleEmailAlreadyExists(EmailAlreadyExistsException e) {
        return ApiErrors.of(
                CONFLICT,
                "Integrity constraint has been violated.",
                e.getMessage()
        );
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler({
            Exception.class
    })
    public ApiErrors handleOther(Exception ex) {
        return ApiErrors.of(INTERNAL_SERVER_ERROR,
                "Unexpected error.",
                ex.getMessage()
        );
    }
}
