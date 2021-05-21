package it.gov.pagopa.tkm.ms.consentmanager.constant;

import it.gov.pagopa.tkm.ms.consentmanager.model.entity.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;

import java.time.*;
import java.util.*;
import java.util.stream.*;

import static it.gov.pagopa.tkm.ms.consentmanager.constant.ConsentRequestEnum.*;
import static it.gov.pagopa.tkm.ms.consentmanager.constant.ConsentEntityEnum.Partial;

public class DefaultBeans {

    public final String TAX_CODE = "AAABBBCCCDDD1111";
    public final String CLIENT_ID = "TEST_CLIENT";
    public final String HPAN = "92fc472e8709cf61aa2b6f8bb9cf61aa2b6f8bd8267f9c14f58f59cf61aa2b6f";
    public final Set<ServiceEnum> ONE_SERVICE_SET = new HashSet<>(Collections.singletonList(ServiceEnum.BPD));
    public final Set<ServiceEnum> ALL_SERVICES_SET = new HashSet<>(Arrays.asList(ServiceEnum.values()));

    public  final Instant INSTANT = Instant.parse("2018-08-19T16:45:42.00Z");

    public final Consent GLOBAL_ALLOW_CONSENT_REQUEST = new Consent().setConsent(Allow);
    public final Consent GLOBAL_DENY_CONSENT_REQUEST = new Consent().setConsent(Deny);
    public final Consent ALLOW_CONSENT_ALL_SERVICES_REQUEST = new Consent().setConsent(Allow).setHpan(HPAN);
    public final Consent DENY_CONSENT_ALL_SERVICES_REQUEST = new Consent().setConsent(Deny).setHpan(HPAN);
    public final Consent ALLOW_CONSENT_ONE_SERVICE_REQUEST = new Consent().setConsent(Allow).setHpan(HPAN).setServices(ONE_SERVICE_SET);
    public final Consent DENY_CONSENT_ONE_SERVICE_REQUEST = new Consent().setConsent(Deny).setHpan(HPAN).setServices(ONE_SERVICE_SET);
    public final Consent ALLOW_CONSENT_MULTIPLE_SERVICES_REQUEST = new Consent().setConsent(Allow).setHpan(HPAN).setServices(ALL_SERVICES_SET);
    public final Consent DENY_CONSENT_MULTIPLE_SERVICES_REQUEST = new Consent().setConsent(Deny).setHpan(HPAN).setServices(ALL_SERVICES_SET);

    public final Consent MISSING_CONSENT_REQUEST = new Consent();
    public final Consent ALLOW_CONSENT_INVALID_HPAN_REQUEST = new Consent().setConsent(Allow).setHpan(HPAN + "a");

    public final List<Consent> VALID_CONSENT_REQUESTS = Arrays.asList(
            GLOBAL_ALLOW_CONSENT_REQUEST,
            GLOBAL_DENY_CONSENT_REQUEST,
            ALLOW_CONSENT_ALL_SERVICES_REQUEST,
            ALLOW_CONSENT_ONE_SERVICE_REQUEST,
            ALLOW_CONSENT_MULTIPLE_SERVICES_REQUEST,
            DENY_CONSENT_ALL_SERVICES_REQUEST,
            DENY_CONSENT_ONE_SERVICE_REQUEST,
            DENY_CONSENT_MULTIPLE_SERVICES_REQUEST);

    public final List<Consent> INVALID_CONSENT_REQUESTS = Arrays.asList(
            MISSING_CONSENT_REQUEST,
            ALLOW_CONSENT_INVALID_HPAN_REQUEST);

    public final TkmCitizen CITIZEN_WITH_GLOBAL_ALLOW_CONSENT =
            new TkmCitizen()
                    .setTaxCode(TAX_CODE)
                    .setConsentType(ConsentEntityEnum.Allow)
                    .setConsentDate(INSTANT)
                    .setConsentClient(CLIENT_ID)
                    .setDeleted(false);

    public final TkmCitizen CITIZEN_WITH_GLOBAL_ALLOW_CONSENT_UPDATED =
            new TkmCitizen()
            .setTaxCode(TAX_CODE)
            .setConsentType(ConsentEntityEnum.Allow)
            .setConsentDate(INSTANT)
            .setConsentClient(CLIENT_ID)
            .setConsentUpdateClient(CLIENT_ID)
            .setConsentUpdateDate(INSTANT)
            .setDeleted(false);

    public final TkmCitizen CITIZEN_WITH_PARTIAL_CONSENT =
            new TkmCitizen()
                    .setTaxCode(TAX_CODE)
                    .setConsentType(Partial)
                    .setConsentDate(INSTANT)
                    .setConsentClient(CLIENT_ID)
                    .setDeleted(false);

    public final TkmCard CARD_FROM_CITIZEN_WITH_PARTIAL_CONSENT =
            new TkmCard()
                .setHpan(HPAN)
                .setCitizen(CITIZEN_WITH_PARTIAL_CONSENT)
                .setDeleted(false);

    public final List<TkmService> ALL_SERVICES_LIST = ALL_SERVICES_SET.stream().map(s -> new TkmService().setName(s)).collect(Collectors.toList());

    public final TkmService ONE_SERVICE = new TkmService().setName(ServiceEnum.BPD);

    public final List<TkmService> ONE_SERVICE_LIST = Collections.singletonList(ONE_SERVICE);

    public final List<TkmCardService> CARD_SERVICES_FOR_ONE_SERVICE_LIST = Collections.singletonList(
            new TkmCardService()
                    .setCard(CARD_FROM_CITIZEN_WITH_PARTIAL_CONSENT)
                    .setConsentType(ConsentEntityEnum.Allow)
                    .setService(ONE_SERVICE));

    public final List<TkmCardService> CARD_SERVICES_FOR_ALL_SERVICES_LIST = ALL_SERVICES_SET.stream().map(s ->
                    new TkmCardService()
                    .setCard(CARD_FROM_CITIZEN_WITH_PARTIAL_CONSENT)
                    .setConsentType(ConsentEntityEnum.Allow)
                    .setService(new TkmService().setName(s)))
            .collect(Collectors.toList());

}
