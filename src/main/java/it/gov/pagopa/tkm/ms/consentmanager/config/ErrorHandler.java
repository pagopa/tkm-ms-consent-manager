package it.gov.pagopa.tkm.ms.consentmanager.config;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import it.gov.pagopa.tkm.ms.consentmanager.exception.*;
import lombok.extern.log4j.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
@Log4j2
public class ErrorHandler {

    @ExceptionHandler(ConsentException.class)
    public ResponseEntity<ErrorCodeEnum> handleConsentException(ConsentException ce) {
        log.error(ce.getMessage());
        return ResponseEntity.badRequest().body(ce.getErrorCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> handleException(Exception e) {
        log.error(e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
