package it.gov.pagopa.tkm.ms.consentmanager.exception;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
public class ConsentException extends RuntimeException {

    private ErrorCodeEnum errorCode;

    public ConsentException(ErrorCodeEnum ec) {
        super(ec.getErrorCode() + " - " + ec.getDescription());
        errorCode = ec;
    }

}
