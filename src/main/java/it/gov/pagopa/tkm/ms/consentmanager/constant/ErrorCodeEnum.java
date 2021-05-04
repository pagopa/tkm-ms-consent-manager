package it.gov.pagopa.tkm.ms.consentmanager.constant;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCodeEnum {

    CONSENT_TYPE_NOT_CONSISTENT("C1000", "Cannot give a partial consent after a global consent"),
    CONSENT_TYPE_NOT_PERMITTED("C1001", "Cannot request a PARTIAL consent, allowed values are ALLOW and DENY"),
    REQUEST_VALIDATION_FAILED("C1002", "Request validation failed, check for errors in the request body or headers"),
    MISSING_HEADERS("C1003", "Required header(s) missing");

    @Getter
    private final String errorCode;

    @Getter
    private final String description;

}
