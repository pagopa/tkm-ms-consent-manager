package it.gov.pagopa.tkm.ms.consentmanager.constant;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCodeEnum {

    CONSENT_TYPE_NOT_CONSISTENT("C1000", "Cannot give a partial consent after a global consent"),
    CONSENT_TYPE_NOT_PERMITTED("C1001", "Cannot request a PARTIAL consent, allowed values are ALLOW and DENY"),
    HPAN_NOT_FOUND("C1002", "No Card found with the requested Hpan "),
    USER_NOT_FOUND("C1003", "No User found with the requested tax code "),
    ILLEGAL_SERVICE_VALUE("C1004", "Not all services with specified names exist"),
    REQUEST_VALIDATION_FAILED("C1002", "Request validation failed, check for errors in the request body or headers"),
    MISSING_HEADERS("C1003", "Required header(s) missing");
    REQUEST_VALIDATION_FAILED("C1001", "Request validation failed, check for errors in the request body or headers"),
    MISSING_HEADERS("C1002", "Required header(s) missing");

    @Getter
    private final String errorCode;

    @Getter
    private final String description;

}
