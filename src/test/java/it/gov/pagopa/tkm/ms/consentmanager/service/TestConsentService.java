package it.gov.pagopa.tkm.ms.consentmanager.service;

import it.gov.pagopa.tkm.ms.consentmanager.constant.DefaultBeans;
import it.gov.pagopa.tkm.ms.consentmanager.exception.ConsentException;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.TkmCardService;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.Consent;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.ConsentResponse;
import it.gov.pagopa.tkm.ms.consentmanager.repository.CardRepository;
import it.gov.pagopa.tkm.ms.consentmanager.repository.CardServiceRepository;
import it.gov.pagopa.tkm.ms.consentmanager.repository.ServiceRepository;
import it.gov.pagopa.tkm.ms.consentmanager.repository.CitizenRepository;
import it.gov.pagopa.tkm.ms.consentmanager.service.impl.ConsentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
        for (Consent consent : testBeans.VALID_CONSENT_REQUESTS) {
            ConsentResponse consentResponse = consentService.postConsent(testBeans.TAX_CODE, testBeans.CLIENT_ID, consent);
            assertEquals(consentResponse, new ConsentResponse(consent));
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
        when(cardRepository.findByHpanAndCitizen(testBeans.HPAN, testBeans.CITIZEN_WITH_PARTIAL_CONSENT)).thenReturn(null);
        consentService.postConsent(testBeans.TAX_CODE, testBeans.CLIENT_ID, testBeans.ALLOW_CONSENT_ALL_SERVICES_REQUEST);
        verify(cardRepository).save(testBeans.CARD_FROM_CITIZEN_WITH_PARTIAL_CONSENT);
    }

    @Test
    public void givenPartialConsentRequestWithoutServices_applyConsentToAllServices() {
        when(citizenRepository.findByTaxCodeAndDeletedFalse(testBeans.TAX_CODE)).thenReturn(testBeans.CITIZEN_WITH_PARTIAL_CONSENT);
        when(serviceRepository.findAll()).thenReturn(testBeans.ALL_SERVICES_LIST);
        when(cardRepository.findByHpanAndCitizen(testBeans.HPAN, testBeans.CITIZEN_WITH_PARTIAL_CONSENT)).thenReturn(testBeans.CARD_FROM_CITIZEN_WITH_PARTIAL_CONSENT);
        consentService.postConsent(testBeans.TAX_CODE, testBeans.CLIENT_ID, testBeans.ALLOW_CONSENT_ALL_SERVICES_REQUEST);
        verify(serviceRepository).findAll();
        verify(cardServiceRepository).saveAll(cardServiceListCaptor.capture());
        assertThat(cardServiceListCaptor.getValue()).containsExactlyInAnyOrderElementsOf(testBeans.CARD_SERVICES_FOR_ALL_SERVICES_LIST);
    }

    @Test
    public void givenPartialConsentRequestWithServices_applyConsentToGivenServices() {
        when(citizenRepository.findByTaxCodeAndDeletedFalse(testBeans.TAX_CODE)).thenReturn(testBeans.CITIZEN_WITH_PARTIAL_CONSENT);
        when(serviceRepository.findByNameIn(testBeans.ONE_SERVICE_SET)).thenReturn(testBeans.ONE_SERVICE_LIST);
        consentService.postConsent(testBeans.TAX_CODE, testBeans.CLIENT_ID, testBeans.ALLOW_CONSENT_ONE_SERVICE_REQUEST);
        verify(serviceRepository).findByNameIn(testBeans.ONE_SERVICE_SET);
        verify(cardServiceRepository).saveAll(cardServiceListCaptor.capture());
        assertThat(cardServiceListCaptor.getValue()).containsExactlyInAnyOrderElementsOf(testBeans.CARD_SERVICES_FOR_ONE_SERVICE_LIST);
    }

}
