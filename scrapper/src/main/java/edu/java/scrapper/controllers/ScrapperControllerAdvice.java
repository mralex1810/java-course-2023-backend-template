package edu.java.scrapper.controllers;

import edu.java.scrapper.controllers.dto.ApiErrorResponse;
import edu.java.scrapper.exceptions.AlreadyRegisteredChatException;
import edu.java.scrapper.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ScrapperControllerAdvice {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        String error = e.getName() + " should be of type " + e.getRequiredType().getName();

        return new ApiErrorResponse(error, HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(AlreadyRegisteredChatException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ApiErrorResponse handleResourceConflict(AlreadyRegisteredChatException e) {
        return new ApiErrorResponse("Chat already registered", HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleResourceNotFound(ResourceNotFoundException e) {
        return new ApiErrorResponse("Resource not found", HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleRuntimeException(RuntimeException e) {
        return new ApiErrorResponse(
            "An unexpected error occurred. Please try again later.",
            HttpStatus.INTERNAL_SERVER_ERROR,
            e
        );
    }
}
