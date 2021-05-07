package it.gov.pagopa.tkm.ms.consentmanager.constant;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCodeEnum {

    CONSENT_TYPE_NOT_CONSISTENT("C1000", "Cannot give a partial consent after a global consent"),
    REQUEST_VALIDATION_FAILED("C1001", "Request validation failed, check for errors in the request body or headers"),
    MISSING_HEADERS("C1002", "Required header(s) missing");

    @Getter
    private final String errorCode;

    @Getter
    private final String description;

}
