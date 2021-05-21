package it.gov.pagopa.tkm.ms.consentmanager.constant;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCodeEnum {

    REQUEST_VALIDATION_FAILED("C1000", "Request validation failed, check for errors in the request body or headers"),
    MISSING_HEADERS("C1001", "Required header(s) missing"),
    CONSENT_TYPE_NOT_CONSISTENT("C1002", "Cannot give a partial consent after a global consent"),
    CONSENT_TYPE_ALREADY_SET("C1003", "User already holds this consent type"),
    HPAN_NOT_FOUND("C1005", "No Card found with the requested Hpan "),
    USER_NOT_FOUND("C1005", "No User found with the requested tax code "),
    ILLEGAL_SERVICE_VALUE("C1006", "Not all services with specified names exist");
    
    @Getter
    private final String errorCode;

    @Getter
    private final String description;

}
