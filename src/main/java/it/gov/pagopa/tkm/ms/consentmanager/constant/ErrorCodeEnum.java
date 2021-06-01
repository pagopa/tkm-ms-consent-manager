package it.gov.pagopa.tkm.ms.consentmanager.constant;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCodeEnum {

    REQUEST_VALIDATION_FAILED("C1000", "Request validation failed, check for errors in the request body or headers"),
    MISSING_HEADERS("C1001", "Required header(s) missing"),
    CONSENT_TYPE_NOT_CONSISTENT("C1002", "Cannot give a partial consent after a global consent"),
    HPAN_NOT_FOUND("C1003", "No Card found with the requested Hpan "),
    USER_NOT_FOUND("C1004", "No User found with the requested tax code "),
    CONSENT_TYPE_ALREADY_SET("C1005", "Citizen already holds this consent type"),
    HPAN_AND_SERVICES_PARAMS_NOT_COHERENT ("C1006", "services params allowed only with hpan param"),
    EMPTY_CONSENT_SERVICE("C1008", "services params cannot be empty. Please remove or fill it");

    @Getter
    private final String errorCode;

    @Getter
    private final String description;

}
