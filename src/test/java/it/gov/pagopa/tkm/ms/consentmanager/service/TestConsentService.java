package it.gov.pagopa.tkm.ms.consentmanager.service;

import it.gov.pagopa.tkm.ms.consentmanager.constant.ConsentEntityEnum;
import it.gov.pagopa.tkm.ms.consentmanager.constant.ConsentRequestEnum;
import it.gov.pagopa.tkm.ms.consentmanager.constant.DefaultBeans;
import it.gov.pagopa.tkm.ms.consentmanager.exception.ConsentDataNotFoundException;
import it.gov.pagopa.tkm.ms.consentmanager.exception.ConsentException;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.TkmCardService;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.Consent;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.ConsentResponse;
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
import java.util.*;

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
        when(cardRepository.findByHpanAndCitizenAndDeletedFalse(testBeans.HPAN, testBeans.CITIZEN_WITH_PARTIAL_CONSENT)).thenReturn(null);
        consentService.postConsent(testBeans.TAX_CODE, testBeans.CLIENT_ID, testBeans.ALLOW_CONSENT_ALL_SERVICES_REQUEST);
        verify(cardRepository).save(testBeans.CARD_FROM_CITIZEN_WITH_PARTIAL_CONSENT);
    }

    @Test
    public void givenPartialConsentRequestWithoutServices_applyConsentToAllServices() {
        when(citizenRepository.findByTaxCodeAndDeletedFalse(testBeans.TAX_CODE)).thenReturn(testBeans.CITIZEN_WITH_PARTIAL_CONSENT);
        when(serviceRepository.findAll()).thenReturn(testBeans.ALL_SERVICES_LIST);
        when(cardRepository.findByHpanAndCitizenAndDeletedFalse(testBeans.HPAN, testBeans.CITIZEN_WITH_PARTIAL_CONSENT)).thenReturn(testBeans.CARD_FROM_CITIZEN_WITH_PARTIAL_CONSENT);
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

    @Test
    public void get_givenTaxCodeWithGlobalDenyAndNoHpan_returnValidConsent(){
        ConsentResponse expectedResponse= new ConsentResponse();
        expectedResponse.setConsent(ConsentEntityEnum.Deny);
        when(citizenRepository.findByTaxCodeAndDeletedFalse(testBeans.TAX_CODE)).thenReturn(testBeans.USER_WITH_GLOBAL_DENY_CONSENT_UPDATED);
        ConsentResponse response = consentService.getConsent(testBeans.TAX_CODE, null, null);
        assertEquals(response, expectedResponse);
    }

    @Test
    public void get_givenTaxCodeWithGlobalAllowAndNoHpan_returnValidConsent(){
        ConsentResponse expectedResponse= new ConsentResponse();
        expectedResponse.setConsent(ConsentEntityEnum.Allow);
        when(citizenRepository.findByTaxCodeAndDeletedFalse(testBeans.TAX_CODE)).thenReturn(testBeans.CITIZEN_WITH_GLOBAL_ALLOW_CONSENT);
        ConsentResponse response = consentService.getConsent(testBeans.TAX_CODE, null, null);
        assertEquals(response, expectedResponse);

    }

    @Test
    public void get_givenTaxCode_returnValidConsent() {
        ConsentResponse expectedResponse= new ConsentResponse();
        expectedResponse.setConsent(ConsentEntityEnum.Partial);
        Consent consent1 = new Consent().setConsent(ConsentRequestEnum.Allow).setHpan(testBeans.HPAN).setServices(testBeans.CARD_1_SERVICE_SET);
        ConsentResponse consentResponse1 = new ConsentResponse(consent1);
        expectedResponse.setDetails(Collections.singletonList(consentResponse1));

        when(citizenRepository.findByTaxCodeAndDeletedFalse(testBeans.TAX_CODE)).thenReturn(testBeans.CITIZEN_WITH_PARTIAL_CONSENT);

        ConsentResponse response = consentService.getConsent(testBeans.TAX_CODE, null, null);
        assertEquals(response, expectedResponse);

    }

    @Test
    public void get_givenTaxCodeAndHpan_returnValidConsent(){
        ConsentResponse expectedResponse= new ConsentResponse();
        expectedResponse.setConsent(ConsentEntityEnum.Partial);
        Consent consent1 = new Consent().setConsent(ConsentRequestEnum.Allow).setHpan(testBeans.HPAN).setServices(testBeans.CARD_1_SERVICE_SET);
        ConsentResponse consentResponse1 = new ConsentResponse(consent1);
        expectedResponse.setDetails(Collections.singletonList(consentResponse1));

        when(citizenRepository.findByTaxCodeAndDeletedFalse(testBeans.TAX_CODE)).thenReturn(testBeans.CITIZEN_WITH_PARTIAL_CONSENT);
        when(cardRepository.findByHpanAndDeletedFalse(testBeans.HPAN)).thenReturn(testBeans.PARTIAL_USER_VALID_CARD);

        ConsentResponse response = consentService.getConsent(testBeans.TAX_CODE, testBeans.HPAN, null);
        assertEquals(response, expectedResponse);
    }


    @Test
    public void get_givenTaxCodeAndHpanAndServices_returnValidConsent() {
        ConsentResponse expectedResponse= new ConsentResponse();
        expectedResponse.setConsent(ConsentEntityEnum.Partial);
        Consent consent1 = new Consent().setConsent(ConsentRequestEnum.Allow).setHpan(testBeans.HPAN).setServices(testBeans.CARD_1_SERVICE_SET);
        ConsentResponse consentResponse1 = new ConsentResponse(consent1);
        expectedResponse.setDetails(Collections.singletonList(consentResponse1));

        when(citizenRepository.findByTaxCodeAndDeletedFalse(testBeans.TAX_CODE)).thenReturn(testBeans.CITIZEN_WITH_PARTIAL_CONSENT);
        when(cardRepository.findByHpanAndDeletedFalse(testBeans.HPAN)).thenReturn(testBeans.PARTIAL_USER_VALID_CARD);

        ConsentResponse response = consentService.getConsent(testBeans.TAX_CODE, testBeans.HPAN, testBeans.SERVICES_SUB_ARRAY);
        assertEquals(response, expectedResponse);

    }

    @Test
    public void get_givenNotExistentTaxCode_expectNotFound() {
        when(citizenRepository.findByTaxCodeAndDeletedFalse(testBeans.TAX_CODE)).thenReturn(null);
        assertThrows(ConsentDataNotFoundException.class, () ->  consentService.getConsent(testBeans.TAX_CODE, null, null));
    }

    @Test
    public void get_givenNotExistentHpan_expectNotFound() {
        when(citizenRepository.findByTaxCodeAndDeletedFalse(testBeans.TAX_CODE)).thenReturn(testBeans.CITIZEN_WITH_PARTIAL_CONSENT);
        when(cardRepository.findByHpanAndDeletedFalse(testBeans.HPAN)).thenReturn(null);
        assertThrows(ConsentDataNotFoundException.class, () -> consentService.getConsent(testBeans.TAX_CODE, testBeans.HPAN, null));

    }

}
