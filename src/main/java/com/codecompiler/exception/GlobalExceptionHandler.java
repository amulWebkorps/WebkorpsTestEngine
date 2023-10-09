package com.codecompiler.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.codecompiler.reponse.ResponseHandler;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<Object> emailPasswordNotMatch(BadCredentialsException badCredentialsException){
		log.info("Exception occurs : "+badCredentialsException.getMessage());
		return ResponseHandler.generateResponse("UNAUTHORIZED", HttpStatus.UNAUTHORIZED,"email and password does not match");
	}
	
	@ExceptionHandler(InsufficientDataException.class)
	public ResponseEntity<Object> insufficientData(InsufficientDataException insufficientDataException){
		log.info("Exception occurs : "+insufficientDataException.getMessage());
		return ResponseHandler.generateResponse("Insufficient Data", HttpStatus.UNAUTHORIZED,insufficientDataException.getMessage());
	}
	
	@ExceptionHandler(UserAlreadyExistException.class)
	public ResponseEntity<Object> userAlreadyExist(UserAlreadyExistException existException){
		log.info("Exception occurs : "+existException.getMessage());
		return ResponseHandler.generateResponse("CONFLICT", HttpStatus.CONFLICT,existException.getMessage());
	}
	
	@ExceptionHandler(UnSupportedFormatException.class)
	public ResponseEntity<Object> unSupportedFormat(UnSupportedFormatException formatException){
		log.info("Exception occurs : "+formatException.getMessage());
		return ResponseHandler.generateResponse("UNSUPPORTED_MEDIA_TYPE", HttpStatus.UNSUPPORTED_MEDIA_TYPE,formatException.getMessage());
	}
	
	@ExceptionHandler(RecordNotFoundException.class)
	public ResponseEntity<Object> recordNotFound(RecordNotFoundException notFoundException){
		log.info("Exception occurs : "+notFoundException.getMessage());
		return ResponseHandler.generateResponse("NOT_FOUND", HttpStatus.NOT_FOUND,notFoundException.getMessage());
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> commonExceptionResponse(Exception exception){
		log.info("Exception occurs : "+exception.getMessage());
		return ResponseHandler.generateResponse("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR,exception.getMessage());
	}
}
