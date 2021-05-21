package it.gov.pagopa.tkm.ms.consentmanager.exception;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConsentException extends RuntimeException {

    private ErrorCodeEnum errorCode;

    public ConsentException(ErrorCodeEnum errorCode) {
        super(errorCode.getErrorCode() + " - " + errorCode.getDescription());
        this.setErrorCode(errorCode);
    }

}
