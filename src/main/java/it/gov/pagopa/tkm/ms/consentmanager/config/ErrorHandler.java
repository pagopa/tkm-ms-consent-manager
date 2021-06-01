package it.gov.pagopa.tkm.ms.consentmanager.config;

import it.gov.pagopa.tkm.ms.consentmanager.constant.ErrorCodeEnum;
import it.gov.pagopa.tkm.ms.consentmanager.exception.ConsentDataNotFoundException;
import it.gov.pagopa.tkm.ms.consentmanager.exception.ConsentException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ValidationException;

@RestControllerAdvice
@Log4j2
public class ErrorHandler {

    @ExceptionHandler(ConsentException.class)
    public ResponseEntity<ErrorCodeEnum> handleConsentException(ConsentException ce) {
        log.error(ce.getMessage());
        return ResponseEntity.badRequest().body(ce.getErrorCode());
    }

    @ExceptionHandler(ConsentDataNotFoundException.class)
    public ResponseEntity<ErrorCodeEnum> handleConsentDataNotFoundException(ConsentDataNotFoundException ce) {
        log.error(ce.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ce.getErrorCode());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, MethodArgumentTypeMismatchException.class, ValidationException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorCodeEnum> handleValidationException(Exception ve) {
        log.error(ve.getMessage());
        return ResponseEntity.badRequest().body(ErrorCodeEnum.REQUEST_VALIDATION_FAILED);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorCodeEnum> handleMissingHeadersException(MissingRequestHeaderException he) {
        log.error(he.getMessage());
        return ResponseEntity.badRequest().body(ErrorCodeEnum.MISSING_HEADERS);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> handleException(Exception e) {
        log.error(e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
