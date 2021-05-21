package it.gov.pagopa.tkm.ms.consentmanager.config;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import it.gov.pagopa.tkm.ms.consentmanager.exception.*;
import lombok.extern.log4j.*;
import org.springframework.http.*;
import org.springframework.http.converter.*;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.*;

import javax.validation.*;

@RestControllerAdvice
@Log4j2
public class ErrorHandler {

    @ExceptionHandler(ConsentException.class)
    public ResponseEntity<ErrorCodeEnum> handleConsentException(ConsentException ce) {
        log.error(ce.getMessage());
        return ResponseEntity.badRequest().body(ce.getErrorCode());
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
