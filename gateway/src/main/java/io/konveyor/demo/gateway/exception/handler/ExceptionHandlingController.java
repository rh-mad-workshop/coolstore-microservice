package io.konveyor.demo.gateway.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.konveyor.demo.gateway.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlingController {
	
	@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Order not found")
	@ExceptionHandler(ResourceNotFoundException.class)
	public void resourceNotFound(ResourceNotFoundException e) {
		log.warn("Resource not found: " + e.getMessage());
	}

}
