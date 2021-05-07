
package it.gov.pagopa.tkm.ms.consentmanager.constant;

import it.gov.pagopa.tkm.ms.consentmanager.model.entity.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;

import java.time.*;
import java.util.*;
import java.util.stream.*;

import static it.gov.pagopa.tkm.ms.consentmanager.constant.ConsentEnum.*;

public class TestBeans {

    public static final String TAX_CODE = "AAABBBCCCDDD1111";
    public static final String INVALID_TAX_CODE = TAX_CODE + "1";

    public static final ClientEnum CLIENT_ID = ClientEnum.EXAMPLE;
    public static final String HPAN = "92fc472e8709cf61aa2b6f8bb9cf61aa2b6f8bd8267f9c14f58f59cf61aa2b6f";
    public static final String HPAN_2 = "nw629p2e8709cf61aa2b6f8bb9cf61aa2b6f8bd8267f9c14f58f59cf61be80q1";
    public static final String INVALID_HPAN = HPAN + "a";

    public static final Set<ServiceEnum> ONE_SERVICE_SET = new HashSet<>(Collections.singletonList(ServiceEnum.EXAMPLE));
    public static final Set<ServiceEnum> ALL_SERVICES_SET = new HashSet<>(Arrays.asList(ServiceEnum.values()));

    private static final Instant INSTANT = Instant.parse("2018-08-19T16:45:42.00Z");

    public static final Consent GLOBAL_ALLOW_CONSENT_REQUEST = new Consent().setConsent(ALLOW);
    public static final Consent GLOBAL_DENY_CONSENT_REQUEST = new Consent().setConsent(DENY);
    public static final Consent ALLOW_CONSENT_ALL_SERVICES_REQUEST = new Consent().setConsent(ALLOW).setHpan(HPAN);
    public static final Consent DENY_CONSENT_ALL_SERVICES_REQUEST = new Consent().setConsent(DENY).setHpan(HPAN);
    public static final Consent ALLOW_CONSENT_ONE_SERVICE_REQUEST = new Consent().setConsent(ALLOW).setHpan(HPAN).setServices(ONE_SERVICE_SET);
    public static final Consent DENY_CONSENT_ONE_SERVICE_REQUEST = new Consent().setConsent(DENY).setHpan(HPAN).setServices(ONE_SERVICE_SET);
    public static final Consent ALLOW_CONSENT_MULTIPLE_SERVICES_REQUEST = new Consent().setConsent(ALLOW).setHpan(HPAN).setServices(ALL_SERVICES_SET);
    public static final Consent DENY_CONSENT_MULTIPLE_SERVICES_REQUEST = new Consent().setConsent(DENY).setHpan(HPAN).setServices(ALL_SERVICES_SET);

    public static final Consent MISSING_CONSENT_REQUEST = new Consent();
    public static final Consent PARTIAL_CONSENT_REQUEST = new Consent().setConsent(PARTIAL);
    public static final Consent ALLOW_CONSENT_INVALID_HPAN_REQUEST = new Consent().setConsent(ALLOW).setHpan(HPAN + "a");

    public static final List<Consent> VALID_CONSENT_REQUESTS = Arrays.asList(
            GLOBAL_ALLOW_CONSENT_REQUEST,
            GLOBAL_DENY_CONSENT_REQUEST,
            ALLOW_CONSENT_ALL_SERVICES_REQUEST,
            ALLOW_CONSENT_ONE_SERVICE_REQUEST,
            ALLOW_CONSENT_MULTIPLE_SERVICES_REQUEST,
            DENY_CONSENT_ALL_SERVICES_REQUEST,
            DENY_CONSENT_ONE_SERVICE_REQUEST,
            DENY_CONSENT_MULTIPLE_SERVICES_REQUEST);

    public static final List<Consent> INVALID_CONSENT_REQUESTS = Arrays.asList(
            MISSING_CONSENT_REQUEST,
            PARTIAL_CONSENT_REQUEST,
            ALLOW_CONSENT_INVALID_HPAN_REQUEST);

    public static final TkmUser USER_WITH_GLOBAL_ALLOW_CONSENT =
            new TkmUser()
                    .setTaxCode(TAX_CODE)
                    .setConsentType(ALLOW)
                    .setConsentDate(INSTANT)
                    .setConsentLastClient(CLIENT_ID);

    public static final TkmUser USER_WITH_PARTIAL_CONSENT =
            new TkmUser()
                    .setTaxCode(TAX_CODE)
                    .setConsentType(PARTIAL)
                    .setConsentDate(INSTANT)
                    .setConsentLastClient(CLIENT_ID);

    public static final TkmUser USER_WITH_GLOBAL_DENY_CONSENT =
            new TkmUser()
                    .setTaxCode(TAX_CODE)
                    .setConsentType(DENY)
                    .setConsentDate(INSTANT)
                    .setConsentLastClient(CLIENT_ID);


    public static final TkmCard CARD_FROM_USER_WITH_PARTIAL_CONSENT =
            new TkmCard()
                .setHpan(HPAN)
                .setUser(USER_WITH_PARTIAL_CONSENT);

    public static final List<TkmService> ALL_SERVICES_LIST = ALL_SERVICES_SET.stream().map(s ->
                    new TkmService().setName(s))
            .collect(Collectors.toList());

    public static final TkmService ONE_SERVICE = new TkmService().setName(ServiceEnum.EXAMPLE);

    public static final List<TkmService> ONE_SERVICE_LIST = Collections.singletonList(ONE_SERVICE);

    public static final List<TkmCardService> CARD_SERVICES_FOR_ONE_SERVICE_LIST = Collections.singletonList(
            new TkmCardService()
                    .setCard(CARD_FROM_USER_WITH_PARTIAL_CONSENT)
                    .setConsentType(ALLOW)
                    .setService(ONE_SERVICE));

    public static final List<TkmCardService> CARD_SERVICES_FOR_ALL_SERVICES_LIST = ALL_SERVICES_SET.stream().map(s ->
                    new TkmCardService()
                    .setCard(CARD_FROM_USER_WITH_PARTIAL_CONSENT)
                    .setConsentType(ALLOW)
                    .setService(new TkmService().setName(s)))
            .collect(Collectors.toList());

    public static final TkmService SERVICE_EXAMPLE = new TkmService().setName(ServiceEnum.EXAMPLE);
    public static final TkmService SERVICE_EXAMPLE_2 = new TkmService().setName(ServiceEnum.EXAMPLE_2);
    public static final TkmService SERVICE_EXAMPLE_3 = new TkmService().setName(ServiceEnum.EXAMPLE_3);


    public static final String[] MULTIPLE_SERVICE_STRING_ARRAY = {ServiceEnum.EXAMPLE.toString(), ServiceEnum.EXAMPLE_2.toString()};
    public static final String[] INVALID_MULTIPLE_SERVICE_STRING_ARRAY = {ServiceEnum.EXAMPLE.toString(), "INVALID_SERVICE"};
    public static final List<TkmService> MULTIPLE_TKM_SERVICES = Arrays.asList( SERVICE_EXAMPLE, SERVICE_EXAMPLE_2, SERVICE_EXAMPLE_3);
    public static final List<TkmService> MULTIPLE_TKM_SERVICES_SUB = Arrays.asList( SERVICE_EXAMPLE, SERVICE_EXAMPLE_3);

    public static final Set<ServiceEnum> MULTIPLE_SERVICE_SUB_SET = new HashSet<>(Arrays.asList(ServiceEnum.EXAMPLE, ServiceEnum.EXAMPLE_3));
    public static final Set<ServiceEnum> CARD_1_SERVICE_SET = new HashSet<>(Arrays.asList(ServiceEnum.EXAMPLE, ServiceEnum.EXAMPLE_2));
    public static final Set<ServiceEnum> CARD_2_SERVICE_SET = new HashSet<>(Arrays.asList(ServiceEnum.EXAMPLE, ServiceEnum.EXAMPLE_3));

    public static final TkmCard PARTIAL_USER_VALID_CARD = new TkmCard().setId(1L).setHpan(HPAN).setUser(USER_WITH_PARTIAL_CONSENT).setDeleted(false);
    public static final TkmCard PARTIAL_USER_VALID_CARD_2 = new TkmCard().setId(2L).setHpan(HPAN_2).setUser(USER_WITH_PARTIAL_CONSENT).setDeleted(false);

    public static final List<TkmCard> PARTIAL_USER_CARDS_LIST = Arrays.asList(PARTIAL_USER_VALID_CARD, PARTIAL_USER_VALID_CARD_2);

    public static final TkmCardService CARD_SERVICE_1 = new TkmCardService().setId(1L).setService(SERVICE_EXAMPLE).setCard(PARTIAL_USER_VALID_CARD).setConsentType(ALLOW);
    public static final TkmCardService CARD_SERVICE_2 = new TkmCardService().setId(2L).setService(SERVICE_EXAMPLE_2).setCard(PARTIAL_USER_VALID_CARD).setConsentType(ALLOW);
    public static final TkmCardService CARD_SERVICE_3 = new TkmCardService().setId(3L).setService(SERVICE_EXAMPLE_3).setCard(PARTIAL_USER_VALID_CARD).setConsentType(DENY);
    public static final TkmCardService CARD_SERVICE_4 = new TkmCardService().setId(4L).setService(SERVICE_EXAMPLE).setCard(PARTIAL_USER_VALID_CARD_2).setConsentType(ALLOW);
    public static final TkmCardService CARD_SERVICE_5 = new TkmCardService().setId(5L).setService(SERVICE_EXAMPLE_3).setCard(PARTIAL_USER_VALID_CARD_2).setConsentType(ALLOW);

    public static final List<TkmCardService> CARD_1_SERVICES = Arrays.asList(CARD_SERVICE_1, CARD_SERVICE_2, CARD_SERVICE_3);
    public static final List<TkmCardService> CARD_1_SERVICES_SUB = Arrays.asList(CARD_SERVICE_1);
    public static final List<TkmCardService> CARD_2_SERVICES = Arrays.asList(CARD_SERVICE_4, CARD_SERVICE_5);

    public static final String[] SERVICES_SUB_ARRAY = {ServiceEnum.EXAMPLE.toString(), ServiceEnum.EXAMPLE_3.toString()};
    public static final String[] SERVICES_INVALID_SINGLE_ARRAY = {"INVALID"};

}