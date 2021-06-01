package it.gov.pagopa.tkm.ms.consentmanager.constant;

public enum ConsentEntityEnum {
    Deny,
    Allow,
    Partial;

    public static ConsentEntityEnum toConsentEntityEnum(ConsentRequestEnum requestEnum) {
        return ConsentEntityEnum.valueOf(requestEnum.name());
    }

}
