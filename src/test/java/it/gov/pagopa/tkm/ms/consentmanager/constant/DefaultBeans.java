package it.gov.pagopa.tkm.ms.consentmanager.constant;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.TkmCard;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.TkmCardService;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.TkmCitizen;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.TkmService;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.Consent;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.CardServiceConsent;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.ConsentResponse;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.ServiceConsent;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static it.gov.pagopa.tkm.ms.consentmanager.constant.ConsentEntityEnum.Partial;
import static it.gov.pagopa.tkm.ms.consentmanager.constant.ConsentRequestEnum.Allow;
import static it.gov.pagopa.tkm.ms.consentmanager.constant.ConsentRequestEnum.Deny;

public class DefaultBeans {

    private static final String HPAN2 = "95fc472e8709cf61aa2b6f8bb9cf61aa2b6f8bd8267f9c14f58f59cf61aa2b6f";
    public final String TAX_CODE = "PCCRLE04M24L219D";
    public final String CLIENT_ID = "TEST_CLIENT";
    public final String HPAN = "92fc472e8709cf61aa2b6f8bb9cf61aa2b6f8bd8267f9c14f58f59cf61aa2b6f";
    public final Set<ServiceEnum> ONE_SERVICE_SET = Collections.singleton(ServiceEnum.BPD);

    public final Set<ServiceEnum> ALL_SERVICES_SET = new HashSet<>(Arrays.asList(ServiceEnum.values()));

    public final static Instant INSTANT = Instant.MAX;

    public final Consent GLOBAL_ALLOW_CONSENT_REQUEST = Consent.builder().consent(Allow).build();
    private final Consent GLOBAL_DENY_CONSENT_REQUEST = Consent.builder().consent(Deny).build();
    public final Consent ALLOW_CONSENT_ALL_SERVICES_REQUEST = Consent.builder().consent(Allow).hpan(HPAN).build();
    private final Consent DENY_CONSENT_ALL_SERVICES_REQUEST = Consent.builder().consent(Deny).hpan(HPAN).build();
    private final Consent ALLOW_CONSENT_ONE_SERVICE_REQUEST = Consent.builder().consent(Allow).hpan(HPAN).services(ONE_SERVICE_SET).build();
    private final Consent DENY_CONSENT_ONE_SERVICE_REQUEST = Consent.builder().consent(Deny).hpan(HPAN).services(ONE_SERVICE_SET).build();
    private final Consent ALLOW_CONSENT_MULTIPLE_SERVICES_REQUEST = Consent.builder().consent(Allow).hpan(HPAN).services(ALL_SERVICES_SET).build();
    private final Consent DENY_CONSENT_MULTIPLE_SERVICES_REQUEST = Consent.builder().consent(Deny).hpan(HPAN).services(ALL_SERVICES_SET).build();

    private final Consent MISSING_CONSENT_REQUEST = new Consent();
    private final Consent ALLOW_CONSENT_INVALID_HPAN_REQUEST = Consent.builder().consent(Allow).hpan(HPAN + "a").build();
    public final Consent ALLOW_CONSENT_INVALID_GLOBAL_SERVICE_REQUEST = Consent.builder().consent(Allow).services(Collections.singleton(ServiceEnum.BPD)).build();

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
            TkmCitizen.builder()
                    .taxCode(TAX_CODE)
                    .consentType(ConsentEntityEnum.Allow)
                    .consentDate(INSTANT)
                    .consentClient(CLIENT_ID)
                    .deleted(false)
            .build();
    public final TkmCitizen CITIZEN_WITH_GLOBAL_DENY_CONSENT =
            TkmCitizen.builder()
                    .taxCode(TAX_CODE)
                    .consentType(ConsentEntityEnum.Deny)
                    .consentDate(INSTANT)
                    .consentClient(CLIENT_ID)
                    .deleted(false)
            .build();

    public final TkmCitizen CITIZEN_WITH_GLOBAL_ALLOW_CONSENT_UPDATED =
            TkmCitizen.builder()
                    .taxCode(TAX_CODE)
                    .consentType(ConsentEntityEnum.Allow)
                    .consentDate(INSTANT)
                    .consentClient(CLIENT_ID)
                    .consentUpdateClient(CLIENT_ID)
                    .consentUpdateDate(INSTANT)
                    .deleted(false)
            .build();

    public final TkmCitizen CITIZEN_WITH_PARTIAL_CONSENT =
            TkmCitizen.builder()
                    .taxCode(TAX_CODE)
                    .consentType(Partial)
                    .consentDate(INSTANT)
                    .consentClient(CLIENT_ID)
                    .deleted(false)
            .build();

    public final TkmCard CARD_FROM_CITIZEN_WITH_PARTIAL_CONSENT =
            TkmCard.builder()
                    .hpan(HPAN)
                    .citizen(CITIZEN_WITH_PARTIAL_CONSENT)
                    .deleted(false)
            .build();

    public final List<TkmService> ALL_TKM_SERVICES_LIST = ALL_SERVICES_SET.stream().map(s -> TkmService.builder().name(s).build()).collect(Collectors.toList());

    private final TkmService ONE_SERVICE = TkmService.builder().name(ServiceEnum.BPD).build();

    public final List<TkmService> ONE_SERVICE_LIST = Collections.singletonList(ONE_SERVICE);

    public final Set<TkmCardService> CARD_SERVICES_FOR_ALL_SERVICES_SET = ALL_SERVICES_SET.stream().map(s ->
            TkmCardService.builder()
                    .card(CARD_FROM_CITIZEN_WITH_PARTIAL_CONSENT)
                    .consentType(ConsentRequestEnum.Allow)
                    .service(TkmService.builder().name(s).build())
            .build())
            .collect(Collectors.toSet());

    private final TkmService SERVICE_EXAMPLE = TkmService.builder().name(ServiceEnum.BPD).build();
    private final TkmService SERVICE_EXAMPLE_2 = TkmService.builder().name(ServiceEnum.FA).build();

    public final String[] MULTIPLE_SERVICE_STRING_ARRAY = {ServiceEnum.BPD.toString(), ServiceEnum.FA.toString()};

    private final TkmCard PARTIAL_USER_VALID_CARD = TkmCard.builder().id(1L).hpan(HPAN).citizen(CITIZEN_WITH_PARTIAL_CONSENT).deleted(false).build();

    private final TkmCardService CARD_SERVICE_1 = TkmCardService.builder().service(SERVICE_EXAMPLE).card(PARTIAL_USER_VALID_CARD).consentType(ConsentRequestEnum.Allow).build();
    private final TkmCardService CARD_SERVICE_2 = TkmCardService.builder().service(SERVICE_EXAMPLE_2).card(PARTIAL_USER_VALID_CARD).consentType(ConsentRequestEnum.Allow).build();

    public final List<TkmCardService> CARD_1_SERVICES = Arrays.asList(CARD_SERVICE_1, CARD_SERVICE_2);

    public final Set<ServiceEnum> SERVICES_SUB_ARRAY = Collections.singleton(ServiceEnum.BPD);

    public ConsentResponse getConsentResponseGlobal(ConsentEntityEnum consentEntityEnum) {
        return ConsentResponse.builder()
                .consent(consentEntityEnum)
                .lastUpdateDate(INSTANT)
                .build();
    }

    public ConsentResponse getConsentResponsePartial() {
        return ConsentResponse.builder()
                .consent(Partial)
                .lastUpdateDate(INSTANT)
                .details(getCardServiceConsentSet())
                .build();
    }

    private Set<CardServiceConsent> getCardServiceConsentSet() {
        Set<CardServiceConsent> cardServiceConsentSet = Sets.newHashSet();
        cardServiceConsentSet.add(createCardServiceConsent());
        cardServiceConsentSet.add(createCardServiceConsentOnlyBpd());
        return cardServiceConsentSet;
    }

    private CardServiceConsent createCardServiceConsentOnlyBpd() {
        CardServiceConsent cardServiceConsent = new CardServiceConsent();
        cardServiceConsent.setHpan(HPAN2);
        cardServiceConsent.setServiceConsents(createServiceConsentOnlyBpd());
        return cardServiceConsent;
    }

    private CardServiceConsent createCardServiceConsent() {
        CardServiceConsent cardServiceConsent = new CardServiceConsent();
        cardServiceConsent.setHpan(HPAN);
        cardServiceConsent.setServiceConsents(createServiceConsent());
        return cardServiceConsent;
    }

    private Set<ServiceConsent> createServiceConsentOnlyBpd() {
        Set<ServiceConsent> serviceConsentSet = Sets.newHashSet();
        serviceConsentSet.add(new ServiceConsent(Allow, ServiceEnum.BPD));
        return serviceConsentSet;
    }

    private Set<ServiceConsent> createServiceConsent() {
        Set<ServiceConsent> serviceConsentSet = Sets.newHashSet();
        serviceConsentSet.add(new ServiceConsent(Allow, ServiceEnum.BPD));
        serviceConsentSet.add(new ServiceConsent(Deny, ServiceEnum.FA));
        return serviceConsentSet;
    }


    public TkmCitizen getCitizenTableWithGlobal(ConsentEntityEnum consentEntityEnum) {
        return TkmCitizen.builder()
                .taxCode(TAX_CODE)
                .consentType(consentEntityEnum)
                .consentDate(INSTANT)
                .consentClient(CLIENT_ID)
                .deleted(false)
                .build();
    }

    public TkmCitizen getCitizenTableWithPartial() {
        return TkmCitizen.builder()
                .taxCode(TAX_CODE)
                .consentType(Partial)
                .consentDate(INSTANT)
                .consentClient(CLIENT_ID)
                .deleted(false)
                .cards(createCards())
                .build();
    }

    private Set<TkmCard> createCards() {
        Set<TkmCard> tkmCardSet = Sets.newHashSet();
        tkmCardSet.add(createCard());
        tkmCardSet.add(createCardOnlyBpd());
        return tkmCardSet;
    }

    private TkmCard createCardOnlyBpd() {
        TkmCard tkmCard = new TkmCard();
        tkmCard.setId(0L);
        tkmCard.setHpan(HPAN2);
        tkmCard.setDeleted(false);
        tkmCard.setTkmCardServices(createCardServiceListOnlyBpd());
        return tkmCard;
    }

    private List<TkmCardService> createCardServiceListOnlyBpd() {
        List<TkmCardService> tkmCardServicelist = Lists.newArrayList();
        tkmCardServicelist.add(createCardService(ServiceEnum.BPD, Allow));
        return tkmCardServicelist;
    }

    private TkmCard createCard() {
        TkmCard tkmCard = new TkmCard();
        tkmCard.setId(0L);
        tkmCard.setHpan(HPAN);
        tkmCard.setDeleted(false);
        tkmCard.setTkmCardServices(createCardServiceList());
        return tkmCard;
    }

    private List<TkmCardService> createCardServiceList() {
        List<TkmCardService> tkmCardServicelist = Lists.newArrayList();
        tkmCardServicelist.add(createCardService(ServiceEnum.BPD, Allow));
        tkmCardServicelist.add(createCardService(ServiceEnum.FA, Deny));
        return tkmCardServicelist;
    }

    private TkmCardService createCardService(ServiceEnum serviceEnum, ConsentRequestEnum consentRequestEnum) {
        TkmService tkmService = TkmService.builder().name(serviceEnum).build();
        TkmCardService tkmCardService = new TkmCardService();
        tkmCardService.setService(tkmService);
        tkmCardService.setConsentType(consentRequestEnum);
        return tkmCardService;
    }

}
