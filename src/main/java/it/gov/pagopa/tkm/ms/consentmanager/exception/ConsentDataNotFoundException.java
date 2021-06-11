package it.gov.pagopa.tkm.ms.consentmanager.exception;

import it.gov.pagopa.tkm.ms.consentmanager.constant.ErrorCodeEnum;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ConsentDataNotFoundException extends ConsentException{

    public ConsentDataNotFoundException(ErrorCodeEnum errorCode) {
        super(errorCode);
    }

}
