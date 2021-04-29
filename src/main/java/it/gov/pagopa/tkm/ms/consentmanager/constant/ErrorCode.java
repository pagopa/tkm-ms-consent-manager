package it.gov.pagopa.tkm.ms.consentmanager.constant;

import lombok.*;

@AllArgsConstructor
public enum ErrorCode {

    EXAMPLE("01", "desc");

    private final String errorCode;
    private final String description;

}
