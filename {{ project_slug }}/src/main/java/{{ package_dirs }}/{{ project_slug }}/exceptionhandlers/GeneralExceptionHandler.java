package com.imgarena.sherlock.exceptionhandlers;

import com.imgarena.sherlock.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GeneralExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(GeneralExceptionHandler.class);

  @ExceptionHandler({AccessDeniedException.class, AuthenticationException.class})
  public void handleSpringSecurityExceptions(RuntimeException ex) {
    throw ex;
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleErrors(Exception ex) {
    LOG.error("Unhandled exception:", ex);
    return ResponseEntity.internalServerError()
        .body(new ErrorResponse("Unhandled Exception Error. See logs."));
  }
}
