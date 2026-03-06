package com.enterprise.talent_hub.api.error;

import java.net.URI;
import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.enterprise.talent_hub.service.exception.InvalidCredentialsException;
import com.enterprise.talent_hub.service.exception.ResourceNotFoundException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
		return buildProblem(HttpStatus.NOT_FOUND, "Resource not found", ex.getMessage());
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	public ProblemDetail handleInvalidCredentials(InvalidCredentialsException ex) {
		return buildProblem(HttpStatus.UNAUTHORIZED, "Authentication failed", ex.getMessage());
	}

	@ExceptionHandler({
		ConstraintViolationException.class,
		MethodArgumentNotValidException.class,
		BindException.class,
		MethodArgumentTypeMismatchException.class,
		IllegalArgumentException.class
	})
	public ProblemDetail handleBadRequest(Exception ex) {
		return buildProblem(HttpStatus.BAD_REQUEST, "Invalid request", ex.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ProblemDetail handleUnexpected(Exception ex) {
		return buildProblem(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", ex.getMessage());
	}

	private ProblemDetail buildProblem(HttpStatusCode status, String title, String detail) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail == null ? "No detail provided" : detail);
		problem.setTitle(title);
		problem.setType(URI.create("https://datatracker.ietf.org/doc/html/rfc7807"));
		problem.setProperty("timestamp", OffsetDateTime.now());
		return problem;
	}
}
