package lk.ijse.gdse72.back_end.exceptions;

import lk.ijse.gdse72.back_end.utility.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobuleExceptionHandler {


    // You can also handle generic exceptions like this:
    @ExceptionHandler(Exception.class)// This will catch all exceptions that are not handled by more specific handlers
    public ResponseEntity<APIResponse<String>> handleGenaricExeption(Exception exception) {
        return new ResponseEntity<>(new APIResponse<>(
                500,
                //  HttpStatus.INTERNAL_SERVER_ERROR.value()--> 500 wenuwata danna puluwan
                exception.getMessage(),
                null),HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<APIResponse<String>> handleResourceNotFound(ResourceNotFound exception) {
        return new ResponseEntity<>(new APIResponse<>(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                null), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<Map<String, String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        Map<String,String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return new ResponseEntity<>(new APIResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                errors), HttpStatus.BAD_REQUEST);

    }
}