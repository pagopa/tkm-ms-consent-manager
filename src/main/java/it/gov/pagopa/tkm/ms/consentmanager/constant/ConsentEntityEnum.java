package it.gov.pagopa.tkm.ms.consentmanager.constant;

public enum ConsentEntityEnum {

    DENY,
    ALLOW,
    PARTIAL;

    public static ConsentEntityEnum toConsentEntityEnum(ConsentRequestEnum requestEnum) {
        return ConsentEntityEnum.valueOf(requestEnum.name());
    }

}
