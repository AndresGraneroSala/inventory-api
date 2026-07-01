package es.andres.biblioteca.Biblioteca.exceptions;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {

    private String message;
    private int statusCode;
    private LocalDateTime timeStamp;
    private String errorDetails;

    public ErrorResponse(String message, int statusCode, String errorDetails) {
        this.message = message;
        this.statusCode = statusCode;
        this.errorDetails = errorDetails;
        timeStamp = LocalDateTime.now();
    }
}
