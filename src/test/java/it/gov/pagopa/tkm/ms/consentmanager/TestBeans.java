package it.gov.pagopa.tkm.ms.consentmanager;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;

import java.util.*;

import static it.gov.pagopa.tkm.ms.consentmanager.constant.ConsentEnum.*;

public class TestBeans {

    public static final String TAX_CODE = "AAABBBCCCDDD1111";
    public static final ClientEnum CLIENT_ID = ClientEnum.EXAMPLE;
    public static final String HPAN = "92fc472e8709cf61aa2b6f8bb9cf61aa2b6f8bd8267f9c14f58f59cf61aa2b6f";
    public static final Set<ServiceEnum> ONE_SERVICE_SET = new HashSet<>(Collections.singletonList(ServiceEnum.EXAMPLE));
    public static final Set<ServiceEnum> MULTIPLE_SERVICE_SET = new HashSet<>(Arrays.asList(ServiceEnum.EXAMPLE, ServiceEnum.EXAMPLE_2));

    public static final Consent GLOBAL_ALLOW_CONSENT_REQUEST = new Consent().setConsent(ALLOW);
    public static final Consent GLOBAL_DENY_CONSENT_REQUEST = new Consent().setConsent(DENY);
    public static final Consent ALLOW_CONSENT_ALL_SERVICES_REQUEST = new Consent().setConsent(ALLOW).setHpan(HPAN);
    public static final Consent DENY_CONSENT_ALL_SERVICES_REQUEST = new Consent().setConsent(DENY).setHpan(HPAN);
    public static final Consent ALLOW_CONSENT_ONE_SERVICE_REQUEST = new Consent().setConsent(ALLOW).setHpan(HPAN).setServices(ONE_SERVICE_SET);
    public static final Consent DENY_CONSENT_ONE_SERVICE_REQUEST = new Consent().setConsent(DENY).setHpan(HPAN).setServices(ONE_SERVICE_SET);
    public static final Consent ALLOW_CONSENT_MULTIPLE_SERVICES_REQUEST = new Consent().setConsent(ALLOW).setHpan(HPAN).setServices(MULTIPLE_SERVICE_SET);
    public static final Consent DENY_CONSENT_MULTIPLE_SERVICES_REQUEST = new Consent().setConsent(DENY).setHpan(HPAN).setServices(MULTIPLE_SERVICE_SET);

    public static final Consent MISSING_CONSENT_REQUEST = new Consent();
    public static final Consent PARTIAL_CONSENT_REQUEST = new Consent().setConsent(PARTIAL);
    public static final Consent ALLOW_CONSENT_INVALID_HPAN_REQUEST = new Consent().setConsent(ALLOW).setHpan(HPAN + "a");

    public static final List<Consent> VALID_CONSENT_REQUESTS = Arrays.asList(GLOBAL_ALLOW_CONSENT_REQUEST, GLOBAL_DENY_CONSENT_REQUEST, ALLOW_CONSENT_ALL_SERVICES_REQUEST, ALLOW_CONSENT_ONE_SERVICE_REQUEST, ALLOW_CONSENT_MULTIPLE_SERVICES_REQUEST, DENY_CONSENT_ALL_SERVICES_REQUEST, DENY_CONSENT_ONE_SERVICE_REQUEST, DENY_CONSENT_MULTIPLE_SERVICES_REQUEST);
    public static final List<Consent> INVALID_CONSENT_REQUESTS = Arrays.asList(MISSING_CONSENT_REQUEST, PARTIAL_CONSENT_REQUEST, ALLOW_CONSENT_INVALID_HPAN_REQUEST);

    public static final TkmUser USER_WITH_GLOBAL_ALLOW = new TkmUser().setId(1L).setTaxCode(TAX_CODE).setConsentType(ALLOW).setConsentDate(new Date()).setConsentLastClient(CLIENT_ID);

}
