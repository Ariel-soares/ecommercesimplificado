package com.arielsoares.ecommercesimplificado.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.auth0.jwt.exceptions.JWTCreationException;

import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(HttpStatus.NOT_FOUND, ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InvalidArgumentException.class)
	public ResponseEntity<?> handleInvalidArgumentException(InvalidArgumentException ex, WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST, ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ValidationErrorDetails> handleValidationExceptions(MethodArgumentNotValidException ex) {
		ValidationErrorDetails errorDetails = new ValidationErrorDetails("Validation failed for object.");

		ex.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errorDetails.addError(fieldName, errorMessage);
		});

		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ValidationErrorDetails> handleConstraintViolation(ConstraintViolationException ex) {
		ValidationErrorDetails errorDetails = new ValidationErrorDetails("Validation failed.");

		ex.getConstraintViolations().forEach(violation -> {
			String fieldName = violation.getPropertyPath().toString();
			String errorMessage = violation.getMessage();
			errorDetails.addError(fieldName, errorMessage);
		});

		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<ErrorDetails> handleUsernameNotFoundException(UsernameNotFoundException ex) {
		ErrorDetails errorDetails = new ErrorDetails(HttpStatus.NOT_FOUND, "User not found", ex.getMessage());
		return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<ErrorDetails> handleInvalidTokenException(InvalidTokenException ex) {
		ErrorDetails errorDetails = new ErrorDetails(HttpStatus.UNAUTHORIZED, "Invalid token", ex.getMessage());
		return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(DisabledException.class)
	public ResponseEntity<Object> handleDisabledException(DisabledException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(JWTCreationException.class)
	public ResponseEntity<ErrorDetails> handleJWTCreationException(JWTCreationException ex) {
		ErrorDetails errorDetails = new ErrorDetails(HttpStatus.INTERNAL_SERVER_ERROR, "Error in creating token",
				ex.getMessage());
		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorDetails> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex,
			WebRequest request) {
		String errorMessage = "The request body is not readable. Please check the format of your request.";
		ErrorDetails errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST, errorMessage, ex.getMessage());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorDetails> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        String errorMessage = "O método " + ex.getMethod() + " não é suportado para esta rota. Os métodos suportados são: " 
                + ex.getSupportedHttpMethods();
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.METHOD_NOT_ALLOWED, errorMessage, ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.METHOD_NOT_ALLOWED);
    }

}
