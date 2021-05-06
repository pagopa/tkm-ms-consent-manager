package it.gov.pagopa.tkm.ms.consentmanager.constant;

import lombok.*;

@AllArgsConstructor
public enum ConsentEnum {

    DENY(0),
    ALLOW(1),
    PARTIAL(2);

    private final int dbCode;

    public int getDbCode() {
        return dbCode;
    }

}
