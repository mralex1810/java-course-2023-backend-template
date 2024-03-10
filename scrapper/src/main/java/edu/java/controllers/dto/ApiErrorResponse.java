package edu.java.controllers.dto;

import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpStatus;

public record ApiErrorResponse(String description, String code, String exceptionName, String exceptionMessage,
                               List<String> stacktrace) {

    public ApiErrorResponse(String description, HttpStatus code, Exception e) {
        this(
            description,
            code.name(),
            e.getClass().toString(),
            e.getMessage(),
            Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()
        );
    }

}
