package it.gov.pagopa.tkm.ms.consentmanager.service;

import it.gov.pagopa.tkm.ms.consentmanager.constant.ConsentEnum;
import it.gov.pagopa.tkm.ms.consentmanager.constant.DefaultBeans;
import it.gov.pagopa.tkm.ms.consentmanager.exception.ConsentDataNotFoundException;
import it.gov.pagopa.tkm.ms.consentmanager.exception.ConsentException;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.TkmCardService;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.Consent;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.ConsentResponse;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.GetConsentResponse;
import it.gov.pagopa.tkm.ms.consentmanager.repository.CardRepository;
import it.gov.pagopa.tkm.ms.consentmanager.repository.CardServiceRepository;
import it.gov.pagopa.tkm.ms.consentmanager.repository.ServiceRepository;
import it.gov.pagopa.tkm.ms.consentmanager.repository.UserRepository;
import it.gov.pagopa.tkm.ms.consentmanager.service.impl.ConsentServiceImpl;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
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
    private UserRepository userRepository;

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
    public void givenPartialConsentRequestFromUserWithGlobalConsent_expectException() {
        when(userRepository.findByTaxCode(testBeans.TAX_CODE)).thenReturn(testBeans.USER_WITH_GLOBAL_ALLOW_CONSENT);
        assertThrows(ConsentException.class, () -> consentService.postConsent(testBeans.TAX_CODE, testBeans.CLIENT_ID, testBeans.ALLOW_CONSENT_ALL_SERVICES_REQUEST));
    }

    @Test
    public void givenNewTaxCode_createNewUser() {
        when(userRepository.findByTaxCode(testBeans.TAX_CODE)).thenReturn(null);
        consentService.postConsent(testBeans.TAX_CODE, testBeans.CLIENT_ID, testBeans.GLOBAL_ALLOW_CONSENT_REQUEST);
        verify(userRepository).save(testBeans.USER_WITH_GLOBAL_ALLOW_CONSENT);
    }

    @Test
    public void givenExistingTaxCode_updateUser() {
        when(userRepository.findByTaxCode(testBeans.TAX_CODE)).thenReturn(testBeans.USER_WITH_PARTIAL_CONSENT);
        consentService.postConsent(testBeans.TAX_CODE, testBeans.CLIENT_ID, testBeans.GLOBAL_ALLOW_CONSENT_REQUEST);
        verify(userRepository).save(testBeans.USER_WITH_GLOBAL_ALLOW_CONSENT_UPDATED);
    }

    @Test
    public void givenNewHpan_createNewCard() {
        when(userRepository.findByTaxCode(testBeans.TAX_CODE)).thenReturn(testBeans.USER_WITH_PARTIAL_CONSENT);
        when(cardRepository.findByHpanAndUser(testBeans.HPAN, testBeans.USER_WITH_PARTIAL_CONSENT)).thenReturn(null);
        consentService.postConsent(testBeans.TAX_CODE, testBeans.CLIENT_ID, testBeans.ALLOW_CONSENT_ALL_SERVICES_REQUEST);
        verify(cardRepository).save(testBeans.CARD_FROM_USER_WITH_PARTIAL_CONSENT);
    }

    @Test
    public void givenPartialConsentRequestWithoutServices_applyConsentToAllServices() {
        when(userRepository.findByTaxCode(testBeans.TAX_CODE)).thenReturn(testBeans.USER_WITH_PARTIAL_CONSENT);
        when(serviceRepository.findAll()).thenReturn(testBeans.ALL_SERVICES_LIST);
        when(cardServiceRepository.findByServiceInAndCard(testBeans.ALL_SERVICES_LIST, testBeans.CARD_FROM_USER_WITH_PARTIAL_CONSENT)).thenReturn(testBeans.CARD_SERVICES_FOR_ONE_SERVICE_LIST);
        when(cardRepository.findByHpanAndUser(testBeans.HPAN, testBeans.USER_WITH_PARTIAL_CONSENT)).thenReturn(testBeans.CARD_FROM_USER_WITH_PARTIAL_CONSENT);
        consentService.postConsent(testBeans.TAX_CODE, testBeans.CLIENT_ID, testBeans.ALLOW_CONSENT_ALL_SERVICES_REQUEST);
        verify(serviceRepository).findAll();
        verify(cardServiceRepository).findByServiceInAndCard(testBeans.ALL_SERVICES_LIST, testBeans.CARD_FROM_USER_WITH_PARTIAL_CONSENT);
        verify(cardServiceRepository).saveAll(cardServiceListCaptor.capture());
        assertThat(cardServiceListCaptor.getValue()).containsExactlyInAnyOrderElementsOf(testBeans.CARD_SERVICES_FOR_ALL_SERVICES_LIST);
    }

    @Test
    public void givenPartialConsentRequestWithServices_applyConsentToGivenServices() {
        when(userRepository.findByTaxCode(testBeans.TAX_CODE)).thenReturn(testBeans.USER_WITH_PARTIAL_CONSENT);
        when(serviceRepository.findByNameIn(testBeans.ONE_SERVICE_SET)).thenReturn(testBeans.ONE_SERVICE_LIST);
        when(cardServiceRepository.findByServiceInAndCard(testBeans.ONE_SERVICE_LIST, testBeans.CARD_FROM_USER_WITH_PARTIAL_CONSENT)).thenReturn(null);
        consentService.postConsent(testBeans.TAX_CODE, testBeans.CLIENT_ID, testBeans.ALLOW_CONSENT_ONE_SERVICE_REQUEST);
        verify(serviceRepository).findByNameIn(testBeans.ONE_SERVICE_SET);
        verify(cardServiceRepository).findByServiceInAndCard(testBeans.ONE_SERVICE_LIST, testBeans.CARD_FROM_USER_WITH_PARTIAL_CONSENT);
        verify(cardServiceRepository).saveAll(cardServiceListCaptor.capture());
        assertThat(cardServiceListCaptor.getValue()).containsExactlyInAnyOrderElementsOf(testBeans.CARD_SERVICES_FOR_ONE_SERVICE_LIST);
    }


    //GET
    @Test
    public void get_givenTaxCodeWithGlobalDenyAndNoHpan_returnValidConsent(){
        GetConsentResponse expectedResponse= new GetConsentResponse();
        expectedResponse.setConsent(ConsentEnum.DENY);
        expectedResponse.setDetails(null);

        when(userRepository.findByTaxCode(testBeans.TAX_CODE)).thenReturn(testBeans.USER_WITH_GLOBAL_DENY_CONSENT);

        GetConsentResponse response = consentService.getGetConsentResponse(testBeans.TAX_CODE, null, null);
        Assert.assertEquals(response, expectedResponse);

    }

    @Test
    public void get_givenTaxCodeWithGlobalAllowAndNoHpan_returnValidConsent(){
        GetConsentResponse expectedResponse= new GetConsentResponse();
        expectedResponse.setConsent(ConsentEnum.ALLOW);
        expectedResponse.setDetails(null);

        when(userRepository.findByTaxCode(testBeans.TAX_CODE)).thenReturn(testBeans.USER_WITH_GLOBAL_ALLOW_CONSENT);

        GetConsentResponse response = consentService.getGetConsentResponse(testBeans.TAX_CODE, null, null);
        Assert.assertEquals(response, expectedResponse);

    }

    @Test
    public void get_givenTaxCode_returnValidConsent(){
        GetConsentResponse expectedResponse= new GetConsentResponse();
        expectedResponse.setConsent(ConsentEnum.PARTIAL);
        Consent consent1 = new Consent().setConsent(ConsentEnum.ALLOW).setHpan(testBeans.HPAN).setServices(testBeans.CARD_1_SERVICE_SET);
        ConsentResponse consentResponse1 = new ConsentResponse(consent1);
        Consent consent2 = new Consent().setConsent(ConsentEnum.ALLOW).setHpan(testBeans.HPAN_2).setServices(testBeans.CARD_2_SERVICE_SET);
        ConsentResponse consentResponse2 = new ConsentResponse(consent2);
        expectedResponse.setDetails(Arrays.asList(consentResponse1, consentResponse2));

        when(serviceRepository.findAll()).thenReturn(testBeans.MULTIPLE_TKM_SERVICES);
        when(userRepository.findByTaxCode(testBeans.TAX_CODE)).thenReturn(testBeans.USER_WITH_PARTIAL_CONSENT);
        when(cardRepository.findByUser(testBeans.USER_WITH_PARTIAL_CONSENT)).thenReturn(testBeans.PARTIAL_USER_CARDS_LIST);
        when(cardServiceRepository.findByServiceInAndCard(testBeans.MULTIPLE_TKM_SERVICES, testBeans.PARTIAL_USER_VALID_CARD)).thenReturn(testBeans.CARD_1_SERVICES);
        when(cardServiceRepository.findByServiceInAndCard(testBeans.MULTIPLE_TKM_SERVICES, testBeans.PARTIAL_USER_VALID_CARD_2)).thenReturn(testBeans.CARD_2_SERVICES);

        GetConsentResponse response = consentService.getGetConsentResponse(testBeans.TAX_CODE, null, null);
        Assert.assertEquals(response, expectedResponse);

    }

    @Test
    public void get_givenTaxCodeAndHpan_returnValidConsent(){
        GetConsentResponse expectedResponse= new GetConsentResponse();
        expectedResponse.setConsent(ConsentEnum.PARTIAL);
        Consent consent1 = new Consent().setConsent(ConsentEnum.ALLOW).setHpan(testBeans.HPAN).setServices(testBeans.CARD_1_SERVICE_SET);
        ConsentResponse consentResponse1 = new ConsentResponse(consent1);
        expectedResponse.setDetails(Arrays.asList(consentResponse1));

        when(serviceRepository.findAll()).thenReturn(testBeans.MULTIPLE_TKM_SERVICES);
        when(userRepository.findByTaxCode(testBeans.TAX_CODE)).thenReturn(testBeans.USER_WITH_PARTIAL_CONSENT);
        when(cardRepository.findByHpan(testBeans.HPAN)).thenReturn(testBeans.PARTIAL_USER_VALID_CARD);
        when(cardServiceRepository.findByServiceInAndCard(testBeans.MULTIPLE_TKM_SERVICES, testBeans.PARTIAL_USER_VALID_CARD)).thenReturn(testBeans.CARD_1_SERVICES);

        GetConsentResponse response = consentService.getGetConsentResponse(testBeans.TAX_CODE, testBeans.HPAN, null);
        Assert.assertEquals(response, expectedResponse);

    }

    @Test
    public void get_givenTaxCodeAndServices_returnValidConsent(){
        GetConsentResponse expectedResponse= new GetConsentResponse();
        expectedResponse.setConsent(ConsentEnum.PARTIAL);
        Consent consent1 = new Consent().setConsent(ConsentEnum.ALLOW).setHpan(testBeans.HPAN).setServices(testBeans.ONE_SERVICE_SET);
        ConsentResponse consentResponse1 = new ConsentResponse(consent1);
        Consent consent2 = new Consent().setConsent(ConsentEnum.ALLOW).setHpan(testBeans.HPAN_2).setServices(testBeans.CARD_2_SERVICE_SET);
        ConsentResponse consentResponse2 = new ConsentResponse(consent2);
        expectedResponse.setDetails(Arrays.asList(consentResponse1, consentResponse2));

        when(serviceRepository.findByNameIn(testBeans.MULTIPLE_SERVICE_SUB_SET)).thenReturn(testBeans.MULTIPLE_TKM_SERVICES_SUB);
        when(userRepository.findByTaxCode(testBeans.TAX_CODE)).thenReturn(testBeans.USER_WITH_PARTIAL_CONSENT);
        when(cardRepository.findByUser(testBeans.USER_WITH_PARTIAL_CONSENT)).thenReturn(testBeans.PARTIAL_USER_CARDS_LIST);
        when(cardServiceRepository.findByServiceInAndCard(testBeans.MULTIPLE_TKM_SERVICES_SUB, testBeans.PARTIAL_USER_VALID_CARD)).thenReturn(testBeans.CARD_1_SERVICES_SUB);
        when(cardServiceRepository.findByServiceInAndCard(testBeans.MULTIPLE_TKM_SERVICES_SUB, testBeans.PARTIAL_USER_VALID_CARD_2)).thenReturn(testBeans.CARD_2_SERVICES);

        GetConsentResponse response = consentService.getGetConsentResponse(testBeans.TAX_CODE, null, testBeans.SERVICES_SUB_ARRAY);
        Assert.assertEquals(response, expectedResponse);

    }

    @Test
    public void get_givenTaxCodeAndHpanAndServices_returnValidConsent(){
        GetConsentResponse expectedResponse= new GetConsentResponse();
        expectedResponse.setConsent(ConsentEnum.PARTIAL);
        Consent consent1 = new Consent().setConsent(ConsentEnum.ALLOW).setHpan(testBeans.HPAN).setServices(testBeans.CARD_1_SERVICE_SET);
        ConsentResponse consentResponse1 = new ConsentResponse(consent1);
        expectedResponse.setDetails(Arrays.asList(consentResponse1));

        when(serviceRepository.findByNameIn(testBeans.MULTIPLE_SERVICE_SUB_SET)).thenReturn(testBeans.MULTIPLE_TKM_SERVICES_SUB);
        when(userRepository.findByTaxCode(testBeans.TAX_CODE)).thenReturn(testBeans.USER_WITH_PARTIAL_CONSENT);
        when(cardRepository.findByHpan(testBeans.HPAN)).thenReturn(testBeans.PARTIAL_USER_VALID_CARD);
        when(cardServiceRepository.findByServiceInAndCard(testBeans.MULTIPLE_TKM_SERVICES_SUB, testBeans.PARTIAL_USER_VALID_CARD)).thenReturn(testBeans.CARD_1_SERVICES);

        GetConsentResponse response = consentService.getGetConsentResponse(testBeans.TAX_CODE, testBeans.HPAN, testBeans.SERVICES_SUB_ARRAY);
        Assert.assertEquals(response, expectedResponse);

    }

    @Test
    public void get_givenNotExistentTaxCode_expectNotFound() throws Exception {
        when(serviceRepository.findAll()).thenReturn(testBeans.MULTIPLE_TKM_SERVICES);
        when(userRepository.findByTaxCode(testBeans.TAX_CODE)).thenReturn(null);
        assertThrows(ConsentDataNotFoundException.class, () ->  consentService.getGetConsentResponse(testBeans.TAX_CODE, null, null));
    }

    @Test
    public void get_givenNotExistentHpan_expectNotFound() throws Exception {
        when(userRepository.findByTaxCode(testBeans.TAX_CODE)).thenReturn(testBeans.USER_WITH_PARTIAL_CONSENT);
        when(cardRepository.findByHpan(testBeans.HPAN)).thenReturn(null);
        assertThrows(ConsentDataNotFoundException.class, () -> consentService.getGetConsentResponse(testBeans.TAX_CODE, testBeans.HPAN, null));

    }

    @Test
    public void get_givenInvalidServices_expectBadRequest() throws Exception {
        assertThrows(ConsentException.class, () -> consentService.getGetConsentResponse(testBeans.TAX_CODE, null, testBeans.SERVICES_INVALID_SINGLE_ARRAY));
    }

}
