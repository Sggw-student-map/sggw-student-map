package com.gwozdz1uuu.sggwstudentmap.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Globalny exception handler dla brakujacych zasobow / mapping-ow HTTP.
 *
 * Domyslnie Spring Boot 3.2+ rzuca {@link NoResourceFoundException} dla
 * niezarejestrowanych sciezek i zamienia ja przez DefaultHandlerExceptionResolver
 * na 500 z pelnotreciowym JSON-em w stylu:
 *   {"status":500,"error":"Internal Server Error",
 *    "message":"No static resource actuator/env for request '/actuator/env'."}
 *
 * To wycieka: framework (Spring Boot), pelna sciezke zadania oraz informacje
 * o mechanizmie obslugi statycznych zasobow - co jest informacyjnie uzyteczne
 * dla atakujacego skanujacego endpointy.
 *
 * Zastepujemy to neutralnym 404 z pustym body. Atakujacy nie odroznia
 * "endpoint nie istnieje" od "endpoint istnieje, ale jest schowany".
 */
@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResourceFound(NoResourceFoundException ex) {
        return ResponseEntity.notFound().build();
    }
}
