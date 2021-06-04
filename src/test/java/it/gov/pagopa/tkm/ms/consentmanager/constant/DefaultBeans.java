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

    public final Instant INSTANT = Instant.parse("2018-08-19T16:45:42.00Z");

    public final Consent GLOBAL_ALLOW_CONSENT_REQUEST = new Consent().setConsent(Allow);
    private final Consent GLOBAL_DENY_CONSENT_REQUEST = new Consent().setConsent(Deny);
    public final Consent ALLOW_CONSENT_ALL_SERVICES_REQUEST = new Consent().setConsent(Allow).setHpan(HPAN);
    private final Consent DENY_CONSENT_ALL_SERVICES_REQUEST = new Consent().setConsent(Deny).setHpan(HPAN);
    private final Consent ALLOW_CONSENT_ONE_SERVICE_REQUEST = new Consent().setConsent(Allow).setHpan(HPAN).setServices(ONE_SERVICE_SET);
    private final Consent DENY_CONSENT_ONE_SERVICE_REQUEST = new Consent().setConsent(Deny).setHpan(HPAN).setServices(ONE_SERVICE_SET);
    private final Consent ALLOW_CONSENT_MULTIPLE_SERVICES_REQUEST = new Consent().setConsent(Allow).setHpan(HPAN).setServices(ALL_SERVICES_SET);
    private final Consent DENY_CONSENT_MULTIPLE_SERVICES_REQUEST = new Consent().setConsent(Deny).setHpan(HPAN).setServices(ALL_SERVICES_SET);

    private final Consent MISSING_CONSENT_REQUEST = new Consent();
    private final Consent ALLOW_CONSENT_INVALID_HPAN_REQUEST = new Consent().setConsent(Allow).setHpan(HPAN + "a");
    public final Consent ALLOW_CONSENT_INVALID_GLOBAL_SERVICE_REQUEST = new Consent().setConsent(Allow).setServices(Collections.singleton(ServiceEnum.BPD));

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
    public final TkmCitizen CITIZEN_WITH_GLOBAL_DENY_CONSENT =
            new TkmCitizen()
                    .setTaxCode(TAX_CODE)
                    .setConsentType(ConsentEntityEnum.Deny)
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

    public final List<TkmService> ALL_TKM_SERVICES_LIST = ALL_SERVICES_SET.stream().map(s -> new TkmService().setName(s)).collect(Collectors.toList());

    private final TkmService ONE_SERVICE = new TkmService().setName(ServiceEnum.BPD);

    public final List<TkmService> ONE_SERVICE_LIST = Collections.singletonList(ONE_SERVICE);

    public final Set<TkmCardService> CARD_SERVICES_FOR_ALL_SERVICES_SET = ALL_SERVICES_SET.stream().map(s ->
            new TkmCardService()
                    .setCard(CARD_FROM_CITIZEN_WITH_PARTIAL_CONSENT)
                    .setConsentType(ConsentRequestEnum.Allow)
                    .setService(new TkmService().setName(s)))
            .collect(Collectors.toSet());

    private final TkmService SERVICE_EXAMPLE = new TkmService().setName(ServiceEnum.BPD);
    private final TkmService SERVICE_EXAMPLE_2 = new TkmService().setName(ServiceEnum.FA);

    public final String[] MULTIPLE_SERVICE_STRING_ARRAY = {ServiceEnum.BPD.toString(), ServiceEnum.FA.toString()};

    private final TkmCard PARTIAL_USER_VALID_CARD = new TkmCard().setId(1L).setHpan(HPAN).setCitizen(CITIZEN_WITH_PARTIAL_CONSENT).setDeleted(false);

    private final TkmCardService CARD_SERVICE_1 = new TkmCardService().setService(SERVICE_EXAMPLE).setCard(PARTIAL_USER_VALID_CARD).setConsentType(ConsentRequestEnum.Allow);
    private final TkmCardService CARD_SERVICE_2 = new TkmCardService().setService(SERVICE_EXAMPLE_2).setCard(PARTIAL_USER_VALID_CARD).setConsentType(ConsentRequestEnum.Allow);

    public final List<TkmCardService> CARD_1_SERVICES = Arrays.asList(CARD_SERVICE_1, CARD_SERVICE_2);

    public final Set<ServiceEnum> SERVICES_SUB_ARRAY = Collections.singleton(ServiceEnum.BPD);

    public ConsentResponse getConsentResponseGlobal(ConsentEntityEnum consentEntityEnum) {
        return new ConsentResponse()
                .setConsent(consentEntityEnum)
                .setLastUpdateDate(INSTANT);
    }

    public ConsentResponse getConsentResponsePartial() {
        return new ConsentResponse()
                .setConsent(Partial)
                .setLastUpdateDate(INSTANT)
                .setDetails(getCardServiceContentSet());
    }

    private Set<CardServiceConsent> getCardServiceContentSet() {
        Set<CardServiceConsent> cardServiceConsentSet = Sets.newHashSet();
        cardServiceConsentSet.add(createCardServiceConsent());
        cardServiceConsentSet.add(createCardServiceConsentOnlyBpd());
        return cardServiceConsentSet;
    }

    private CardServiceConsent createCardServiceConsentOnlyBpd() {
        CardServiceConsent cardServiceConsent = new CardServiceConsent();
        cardServiceConsent.setHpan(HPAN2);
        cardServiceConsent.setServiceConsents(createServiceContentOnlyBpd());
        return cardServiceConsent;
    }

    private CardServiceConsent createCardServiceConsent() {
        CardServiceConsent cardServiceConsent = new CardServiceConsent();
        cardServiceConsent.setHpan(HPAN);
        cardServiceConsent.setServiceConsents(createServiceContent());
        return cardServiceConsent;
    }

    private Set<ServiceConsent> createServiceContentOnlyBpd() {
        Set<ServiceConsent> serviceConsentSet = Sets.newHashSet();
        serviceConsentSet.add(new ServiceConsent(Allow, ServiceEnum.BPD));
        return serviceConsentSet;
    }

    private Set<ServiceConsent> createServiceContent() {
        Set<ServiceConsent> serviceConsentSet = Sets.newHashSet();
        serviceConsentSet.add(new ServiceConsent(Allow, ServiceEnum.BPD));
        serviceConsentSet.add(new ServiceConsent(Deny, ServiceEnum.FA));
        return serviceConsentSet;
    }


    public TkmCitizen getCitizenTableWithGlobal(ConsentEntityEnum consentEntityEnum) {
        return new TkmCitizen()
                .setTaxCode(TAX_CODE)
                .setConsentType(consentEntityEnum)
                .setConsentDate(INSTANT)
                .setConsentClient(CLIENT_ID)
                .setDeleted(false);
    }

    public TkmCitizen getCitizenTableWithPartial() {
        return new TkmCitizen()
                .setTaxCode(TAX_CODE)
                .setConsentType(Partial)
                .setConsentDate(INSTANT)
                .setConsentClient(CLIENT_ID)
                .setDeleted(false)
                .setCards(createCards());
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
        TkmService tkmService = new TkmService().setName(serviceEnum);

        TkmCardService tkmCardService = new TkmCardService();
        tkmCardService.setService(tkmService);
        tkmCardService.setConsentType(consentRequestEnum);
        return tkmCardService;
    }

//    public <T> T getJson(String fileName, Class<T> aClass) {
//        try (Reader reader = new InputStreamReader(new ClassPathResource(fileName).getInputStream())) {
//            return new Gson().fromJson(reader, aClass);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
