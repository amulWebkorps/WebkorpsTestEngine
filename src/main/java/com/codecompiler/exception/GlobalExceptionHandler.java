package com.codecompiler.exception;

import com.codecompiler.response.ResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex) {
		logError("UNAUTHORIZED", ex);
		return ResponseHandler.generateResponse("UNAUTHORIZED", HttpStatus.UNAUTHORIZED, "Email and password do not match");
	}

	@ExceptionHandler(InsufficientDataException.class)
	public ResponseEntity<Object> handleInsufficientDataException(InsufficientDataException ex) {
		logError("Insufficient Data", ex);
		return ResponseHandler.generateResponse("Insufficient Data", HttpStatus.UNAUTHORIZED, ex.getMessage());
	}

	@ExceptionHandler(UserAlreadyExistException.class)
	public ResponseEntity<Object> handleUserAlreadyExistException(UserAlreadyExistException ex) {
		logError("CONFLICT", ex);
		return ResponseHandler.generateResponse("CONFLICT", HttpStatus.CONFLICT, ex.getMessage());
	}

	@ExceptionHandler(UnSupportedFormatException.class)
	public ResponseEntity<Object> handleUnSupportedFormatException(UnSupportedFormatException ex) {
		logError("UNSUPPORTED_MEDIA_TYPE", ex);
		return ResponseHandler.generateResponse("UNSUPPORTED_MEDIA_TYPE", HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.getMessage());
	}

	@ExceptionHandler(RecordNotFoundException.class)
	public ResponseEntity<Object> handleRecordNotFoundException(RecordNotFoundException ex) {
		logError("NOT_FOUND", ex);
		return ResponseHandler.generateResponse("NOT_FOUND", HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleCommonException(Exception ex) {
		logError("INTERNAL_SERVER_ERROR", ex);
		return ResponseHandler.generateResponse("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
	}

	private void logError(String status, Exception exception) {
		log.error("Exception occurs [{}] : {}", status, exception.getMessage());
	}
}
