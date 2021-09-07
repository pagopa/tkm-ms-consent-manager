package it.gov.pagopa.tkm.ms.consentmanager.constant;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCodeEnum {

    REQUEST_VALIDATION_FAILED(1000, "Request validation failed, check for errors in the request body or headers"),
    MISSING_HEADERS(1001, "Required header(s) missing"),
    CONSENT_TYPE_NOT_CONSISTENT(1002, "Cannot give a partial consent after a global consent"),
    HPAN_NOT_FOUND(1003, "No card found with the requested hpan"),
    CITIZEN_NOT_FOUND(1004, "No citizen found with the requested tax code"),
    CONSENT_TYPE_ALREADY_SET(1005, "Citizen already holds this consent type"),
    HPAN_AND_SERVICES_PARAMS_NOT_COHERENT (1006, "Services param is only allowed in pair with hpan param"),
    EMPTY_CONSENT_SERVICE(1007, "Services param cannot be empty. Please remove it or populate it"),
    CALL_TO_CARD_MANAGER_FAILED(1008, "Could not execute call to Card Manager");

    @Getter
    private final Integer statusCode;

    @Getter
    private final String message;

}
