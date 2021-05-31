package it.gov.pagopa.tkm.ms.consentmanager.service;

import it.gov.pagopa.tkm.ms.consentmanager.constant.ConsentEntityEnum;
import it.gov.pagopa.tkm.ms.consentmanager.constant.DefaultBeans;
import it.gov.pagopa.tkm.ms.consentmanager.constant.ServiceEnum;
import it.gov.pagopa.tkm.ms.consentmanager.exception.ConsentDataNotFoundException;
import it.gov.pagopa.tkm.ms.consentmanager.exception.ConsentException;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.TkmCard;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.TkmCardService;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.TkmCitizen;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.Consent;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.CardServiceConsent;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.ConsentResponse;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.ServiceConsent;
import it.gov.pagopa.tkm.ms.consentmanager.repository.CardRepository;
import it.gov.pagopa.tkm.ms.consentmanager.repository.CardServiceRepository;
import it.gov.pagopa.tkm.ms.consentmanager.repository.CitizenRepository;
import it.gov.pagopa.tkm.ms.consentmanager.repository.ServiceRepository;
import it.gov.pagopa.tkm.ms.consentmanager.service.impl.ConsentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SuppressWarnings("WeakerAccess")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class TestConsentService {

    @InjectMocks
    private ConsentServiceImpl consentService;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardServiceRepository cardServiceRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private CitizenRepository citizenRepository;

    @Captor
    private ArgumentCaptor<List<TkmCardService>> cardServiceListCaptor;

    private DefaultBeans testBeans;
    private final MockedStatic<Instant> instantMockedStatic = mockStatic(Instant.class);

    @BeforeEach
    public void init() {
        testBeans = new DefaultBeans();
        instantMockedStatic.when(Instant::now).thenReturn(testBeans.INSTANT);
    }

    @Test
    public void givenValidConsentRequest_returnValidConsentResponse() {
        when(serviceRepository.findAll()).thenReturn(testBeans.ALL_TKM_SERVICES_LIST);
        when(serviceRepository.findByNameIn(testBeans.ONE_SERVICE_SET)).thenReturn(testBeans.ONE_SERVICE_LIST);
        when(serviceRepository.findByNameIn(testBeans.ALL_SERVICES_SET)).thenReturn(testBeans.ALL_TKM_SERVICES_LIST);
        when(cardServiceRepository.findByCard(testBeans.CARD_FROM_CITIZEN_WITH_PARTIAL_CONSENT)).thenReturn(testBeans.CARD_SERVICES_FOR_ALL_SERVICES_SET);
        for (Consent consent : testBeans.VALID_CONSENT_REQUESTS) {
            ConsentResponse expectedConsentResponse = new ConsentResponse();
            if (consent.isPartial()) {
                Set<ServiceConsent> serviceConsents = (CollectionUtils.isEmpty(consent.getServices()) ?
                        testBeans.CARD_SERVICES_FOR_ALL_SERVICES_SET
                        : testBeans.CARD_1_SERVICES
                ).stream().map(ServiceConsent::new).collect(Collectors.toSet());
                Set<CardServiceConsent> cardServiceConsents = new HashSet<>(Collections.singletonList(new CardServiceConsent(consent.getHpan(), serviceConsents)));
                expectedConsentResponse.setConsent(ConsentEntityEnum.Partial);
                expectedConsentResponse.setLastUpdateDate(null);
                expectedConsentResponse.setDetails(cardServiceConsents);
            } else {
                expectedConsentResponse.setConsent(ConsentEntityEnum.toConsentEntityEnum(consent.getConsent()));
                expectedConsentResponse.setLastUpdateDate(null);
                expectedConsentResponse.setDetails(null);
            }
            ConsentResponse consentResponse = consentService.postConsent(testBeans.TAX_CODE, testBeans.CLIENT_ID, consent);
            assertEquals(expectedConsentResponse, consentResponse);
        }
    }

    @Test
    public void givenPartialConsentRequestFromCitizenWithGlobalConsent_expectException() {
        when(citizenRepository.findByTaxCodeAndDeletedFalse(testBeans.TAX_CODE)).thenReturn(testBeans.CITIZEN_WITH_GLOBAL_ALLOW_CONSENT);
        assertThrows(ConsentException.class, () -> consentService.postConsent(testBeans.TAX_CODE, testBeans.CLIENT_ID, testBeans.ALLOW_CONSENT_ALL_SERVICES_REQUEST));
    }

    @Test
    public void givenRequestOfSameConsentAsCitizen_expectException() {
        when(citizenRepository.findByTaxCodeAndDeletedFalse(testBeans.TAX_CODE)).thenReturn(testBeans.CITIZEN_WITH_GLOBAL_ALLOW_CONSENT);
        assertThrows(ConsentException.class, () -> consentService.postConsent(testBeans.TAX_CODE, testBeans.CLIENT_ID, testBeans.GLOBAL_ALLOW_CONSENT_REQUEST));
    }

    @Test
    public void givenNewTaxCode_createNewCitizen() {
        when(citizenRepository.findByTaxCodeAndDeletedFalse(testBeans.TAX_CODE)).thenReturn(null);
        consentService.postConsent(testBeans.TAX_CODE, testBeans.CLIENT_ID, testBeans.GLOBAL_ALLOW_CONSENT_REQUEST);
        verify(citizenRepository).save(testBeans.CITIZEN_WITH_GLOBAL_ALLOW_CONSENT);
    }

    @Test
    public void givenExistingTaxCode_updateCitizen() {
        when(citizenRepository.findByTaxCodeAndDeletedFalse(testBeans.TAX_CODE)).thenReturn(testBeans.CITIZEN_WITH_PARTIAL_CONSENT);
        consentService.postConsent(testBeans.TAX_CODE, testBeans.CLIENT_ID, testBeans.GLOBAL_ALLOW_CONSENT_REQUEST);
        verify(citizenRepository).save(testBeans.CITIZEN_WITH_GLOBAL_ALLOW_CONSENT_UPDATED);
    }

    @Test
    public void givenNewHpan_createNewCard() {
        when(citizenRepository.findByTaxCodeAndDeletedFalse(testBeans.TAX_CODE)).thenReturn(testBeans.CITIZEN_WITH_PARTIAL_CONSENT);
        when(cardRepository.findByHpanAndCitizenAndDeletedFalse(testBeans.HPAN, testBeans.CITIZEN_WITH_PARTIAL_CONSENT)).thenReturn(null);
        consentService.postConsent(testBeans.TAX_CODE, testBeans.CLIENT_ID, testBeans.ALLOW_CONSENT_ALL_SERVICES_REQUEST);
        verify(cardRepository).save(testBeans.CARD_FROM_CITIZEN_WITH_PARTIAL_CONSENT);
    }

    //GET
    @Test
    public void get_givenTaxCode_returnValidConsent() {
        when(citizenRepository.findByTaxCodeAndDeletedFalse(testBeans.TAX_CODE)).thenReturn(testBeans.getCitizenTableWithPartial());
        ConsentResponse response = consentService.getConsent(testBeans.TAX_CODE, null, null);
        assertEquals(testBeans.getConsentResponsePartial(), response);
    }

    @Test
    public void get_givenTaxCodeAndHpan_returnValidConsent() {
        TkmCitizen citizenTableWithPartial = testBeans.getCitizenTableWithPartial();
        TkmCard next = citizenTableWithPartial.getCards().stream().filter(c -> c.getHpan().equals(testBeans.HPAN)).findAny().get();
        citizenTableWithPartial.setCards(new HashSet<>(Collections.singleton(next)));

        ConsentResponse expectedResponse = testBeans.getConsentResponsePartial();
        CardServiceConsent nextExpected = expectedResponse.getDetails().stream().filter(c -> c.getHpan().equals(testBeans.HPAN)).findAny().get();
        expectedResponse.setDetails(new HashSet<>(Collections.singleton(nextExpected)));

        when(citizenRepository.findByTaxCodeAndDeletedFalse(testBeans.TAX_CODE)).thenReturn(citizenTableWithPartial);
        when(cardRepository.findByHpanAndCitizenAndDeletedFalse(any(), any())).thenReturn(next);

        ConsentResponse response = consentService.getConsent(testBeans.TAX_CODE, testBeans.HPAN, null);
        assertEquals(expectedResponse, response);
    }

    @Test
    public void get_givenTaxCodeAndHpanAndServices_returnValidConsent() {
        TkmCitizen citizenTableWithPartial = testBeans.getCitizenTableWithPartial();
        TkmCard next = citizenTableWithPartial.getCards().stream().filter(c -> c.getHpan().equals(testBeans.HPAN)).findAny().get();
        citizenTableWithPartial.setCards(new HashSet<>(Collections.singleton(next)));

        ConsentResponse expectedResponse = testBeans.getConsentResponsePartial();
        CardServiceConsent nextExpected = expectedResponse.getDetails().stream().filter(c -> c.getHpan().equals(testBeans.HPAN)).findAny().get();
        ServiceConsent serviceConsentExpected = nextExpected.getServiceConsents().stream().filter(s -> s.getService() == ServiceEnum.BPD).findAny().get();
        nextExpected.setServiceConsents(new HashSet<>(Collections.singleton(serviceConsentExpected)));
        expectedResponse.setDetails(new HashSet<>(Collections.singleton(nextExpected)));

        when(citizenRepository.findByTaxCodeAndDeletedFalse(testBeans.TAX_CODE)).thenReturn(citizenTableWithPartial);
        when(cardRepository.findByHpanAndCitizenAndDeletedFalse(any(), any())).thenReturn(next);

        ConsentResponse response = consentService.getConsent(testBeans.TAX_CODE, testBeans.HPAN, testBeans.SERVICES_SUB_ARRAY);
        assertEquals(response, expectedResponse);
    }

    @Test
    public void get_givenNotExistentTaxCode_expectNotFound() {
        when(citizenRepository.findByTaxCodeAndDeletedFalse(testBeans.TAX_CODE)).thenReturn(null);
        assertThrows(ConsentDataNotFoundException.class, () -> consentService.getConsent(testBeans.TAX_CODE, null, null));
    }

    @Test
    public void get_givenNotExistentHpan_expectNotFound() {
        when(citizenRepository.findByTaxCodeAndDeletedFalse(testBeans.TAX_CODE)).thenReturn(testBeans.CITIZEN_WITH_PARTIAL_CONSENT);
        when(cardRepository.findByHpanAndCitizenAndDeletedFalse(any(), any())).thenReturn(null);
        assertThrows(ConsentDataNotFoundException.class, () -> consentService.getConsent(testBeans.TAX_CODE, testBeans.HPAN, null));
    }

    @Test
    public void get_givenServiceWithouttHpan_InvalidRequestService() {
        assertThrows(ConsentException.class, () -> consentService.getConsent(testBeans.TAX_CODE, null, testBeans.SERVICES_SUB_ARRAY));
    }

    @Test
    public void get_givenEmptyServiceWithHpan_InvalidRequestService() {
        assertThrows(ConsentException.class, () -> consentService.getConsent(testBeans.TAX_CODE, testBeans.HPAN, new HashSet<>()));
    }
}
