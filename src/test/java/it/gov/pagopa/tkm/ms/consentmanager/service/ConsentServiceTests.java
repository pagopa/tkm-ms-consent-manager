
package it.gov.pagopa.tkm.ms.consentmanager.service;

import it.gov.pagopa.tkm.ms.consentmanager.constant.ApiEndpoints;
import it.gov.pagopa.tkm.ms.consentmanager.constant.ApiParams;
import it.gov.pagopa.tkm.ms.consentmanager.constant.ConsentEnum;
import it.gov.pagopa.tkm.ms.consentmanager.exception.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.*;
import it.gov.pagopa.tkm.ms.consentmanager.repository.*;
import it.gov.pagopa.tkm.ms.consentmanager.service.impl.*;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.internal.matchers.apachecommons.*;
import org.mockito.junit.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.time.*;
import java.time.temporal.*;
import java.util.*;

import static it.gov.pagopa.tkm.ms.consentmanager.constant.TestBeans.*;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConsentServiceTests {

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
    private ArgumentCaptor<TkmUser> userCaptor;

    @Captor
    private ArgumentCaptor<List<TkmCardService>> cardServiceListCaptor;

    @Test
    public void givenValidConsentRequest_returnValidConsentResponse() {
        for (Consent consent : VALID_CONSENT_REQUESTS) {
            ConsentResponse consentResponse = consentService.postConsent(TAX_CODE, CLIENT_ID, consent);
            assertEquals(consentResponse, new ConsentResponse(consent));
        }
    }

    @Test(expected = ConsentException.class)
    public void givenPartialConsentRequestFromUserWithGlobalConsent_expectException() {
        given(userRepository.findByTaxCode(TAX_CODE)).willReturn(USER_WITH_GLOBAL_ALLOW_CONSENT);
        consentService.postConsent(TAX_CODE, CLIENT_ID, ALLOW_CONSENT_ALL_SERVICES_REQUEST);
    }

    @Test
    public void givenNewTaxCode_createNewUser() {
        given(userRepository.findByTaxCode(TAX_CODE)).willReturn(null);
        consentService.postConsent(TAX_CODE, CLIENT_ID, GLOBAL_ALLOW_CONSENT_REQUEST);
        verify(userRepository).save(userCaptor.capture());
        assertTrue(new ReflectionEquals(USER_WITH_GLOBAL_ALLOW_CONSENT, "consentDate").matches(userCaptor.getValue()));
        assertThat(userCaptor.getValue().getConsentDate()).isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS));
    }

    @Test
    public void givenExistingTaxCode_updateUser() {
        given(userRepository.findByTaxCode(TAX_CODE)).willReturn(USER_WITH_PARTIAL_CONSENT);
        consentService.postConsent(TAX_CODE, CLIENT_ID, GLOBAL_ALLOW_CONSENT_REQUEST);
        verify(userRepository).save(userCaptor.capture());
        assertTrue(new ReflectionEquals(USER_WITH_GLOBAL_ALLOW_CONSENT, "consentUpdateDate").matches(userCaptor.getValue()));
        assertThat(userCaptor.getValue().getConsentUpdateDate()).isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS));
    }

    @Test
    public void givenNewHpan_createNewCard() {
        given(userRepository.findByTaxCode(TAX_CODE)).willReturn(USER_WITH_PARTIAL_CONSENT);
        given(cardRepository.findByHpan(HPAN)).willReturn(null);
        consentService.postConsent(TAX_CODE, CLIENT_ID, ALLOW_CONSENT_ALL_SERVICES_REQUEST);
        verify(cardRepository).save(CARD_FROM_USER_WITH_PARTIAL_CONSENT);
    }

    @Test
    public void givenPartialConsentRequestWithoutServices_applyConsentToAllServices() {
        given(userRepository.findByTaxCode(TAX_CODE)).willReturn(USER_WITH_PARTIAL_CONSENT);
        given(serviceRepository.findAll()).willReturn(ALL_SERVICES_LIST);
        given(cardServiceRepository.findByServiceInAndCard(ALL_SERVICES_LIST, CARD_FROM_USER_WITH_PARTIAL_CONSENT)).willReturn(CARD_SERVICES_FOR_ONE_SERVICE_LIST);
        given(cardRepository.findByHpan(HPAN)).willReturn(CARD_FROM_USER_WITH_PARTIAL_CONSENT);
        consentService.postConsent(TAX_CODE, CLIENT_ID, ALLOW_CONSENT_ALL_SERVICES_REQUEST);
        verify(serviceRepository).findAll();
        verify(cardServiceRepository).findByServiceInAndCard(ALL_SERVICES_LIST, CARD_FROM_USER_WITH_PARTIAL_CONSENT);
        verify(cardServiceRepository).saveAll(cardServiceListCaptor.capture());
        assertTrue(new ReflectionEquals(CARD_SERVICES_FOR_ALL_SERVICES_LIST, "consentUpdateDate").matches(cardServiceListCaptor.getValue()));
    }

    @Test
    public void givenPartialConsentRequestWithServices_applyConsentToGivenServices() {
        given(userRepository.findByTaxCode(TAX_CODE)).willReturn(USER_WITH_PARTIAL_CONSENT);
        given(serviceRepository.findByNameIn(ONE_SERVICE_SET)).willReturn(ONE_SERVICE_LIST);
        given(cardServiceRepository.findByServiceInAndCard(ONE_SERVICE_LIST, CARD_FROM_USER_WITH_PARTIAL_CONSENT)).willReturn(null);
        consentService.postConsent(TAX_CODE, CLIENT_ID, ALLOW_CONSENT_ONE_SERVICE_REQUEST);
        verify(serviceRepository).findByNameIn(ONE_SERVICE_SET);
        verify(cardServiceRepository).findByServiceInAndCard(ONE_SERVICE_LIST, CARD_FROM_USER_WITH_PARTIAL_CONSENT);
        verify(cardServiceRepository).saveAll(CARD_SERVICES_FOR_ONE_SERVICE_LIST);
    }


    //GET
    @Test
    public void get_givenTaxCodeWithGlobalDenyAndNoHpan_returnValidConsent(){
        GetConsentResponse expectedResponse= new GetConsentResponse();
        expectedResponse.setConsent(ConsentEnum.DENY);
        expectedResponse.setDetails(null);

        given(serviceRepository.findAll()).willReturn(MULTIPLE_TKM_SERVICES);
        given(userRepository.findByTaxCode(TAX_CODE)).willReturn(USER_WITH_GLOBAL_DENY_CONSENT);

        GetConsentResponse response = consentService.getGetConsentResponse(TAX_CODE, null, null);
        Assert.assertEquals(response, expectedResponse);

    }

    @Test
    public void get_givenTaxCodeWithGlobalAllowAndNoHpan_returnValidConsent(){
        GetConsentResponse expectedResponse= new GetConsentResponse();
        expectedResponse.setConsent(ConsentEnum.ALLOW);
        expectedResponse.setDetails(null);

        given(serviceRepository.findAll()).willReturn(MULTIPLE_TKM_SERVICES);
        given(userRepository.findByTaxCode(TAX_CODE)).willReturn(USER_WITH_GLOBAL_ALLOW_CONSENT);

        GetConsentResponse response = consentService.getGetConsentResponse(TAX_CODE, null, null);
        Assert.assertEquals(response, expectedResponse);

    }

    @Test
    public void get_givenTaxCode_returnValidConsent(){
        GetConsentResponse expectedResponse= new GetConsentResponse();
        expectedResponse.setConsent(ConsentEnum.PARTIAL);
        Consent consent1 = new Consent().setConsent(ConsentEnum.ALLOW).setHpan(HPAN).setServices(CARD_1_SERVICE_SET);
        ConsentResponse consentResponse1 = new ConsentResponse(consent1);
        Consent consent2 = new Consent().setConsent(ConsentEnum.ALLOW).setHpan(HPAN_2).setServices(CARD_2_SERVICE_SET);
        ConsentResponse consentResponse2 = new ConsentResponse(consent2);
        expectedResponse.setDetails(Arrays.asList(consentResponse1, consentResponse2));

        given(serviceRepository.findAll()).willReturn(MULTIPLE_TKM_SERVICES);
        given(userRepository.findByTaxCode(TAX_CODE)).willReturn(USER_WITH_PARTIAL_CONSENT);
        given(cardRepository.findByUser(USER_WITH_PARTIAL_CONSENT)).willReturn(PARTIAL_USER_CARDS_LIST);
        given(cardServiceRepository.findByServiceInAndCard(MULTIPLE_TKM_SERVICES, PARTIAL_USER_VALID_CARD)).willReturn(CARD_1_SERVICES);
        given(cardServiceRepository.findByServiceInAndCard(MULTIPLE_TKM_SERVICES, PARTIAL_USER_VALID_CARD_2)).willReturn(CARD_2_SERVICES);

        GetConsentResponse response = consentService.getGetConsentResponse(TAX_CODE, null, null);
        Assert.assertEquals(response, expectedResponse);

    }

    @Test
    public void get_givenTaxCodeAndHpan_returnValidConsent(){
        GetConsentResponse expectedResponse= new GetConsentResponse();
        expectedResponse.setConsent(ConsentEnum.PARTIAL);
        Consent consent1 = new Consent().setConsent(ConsentEnum.ALLOW).setHpan(HPAN).setServices(CARD_1_SERVICE_SET);
        ConsentResponse consentResponse1 = new ConsentResponse(consent1);
        expectedResponse.setDetails(Arrays.asList(consentResponse1));

        given(serviceRepository.findAll()).willReturn(MULTIPLE_TKM_SERVICES);
        given(userRepository.findByTaxCode(TAX_CODE)).willReturn(USER_WITH_PARTIAL_CONSENT);
        given(cardRepository.findByHpan(HPAN)).willReturn(PARTIAL_USER_VALID_CARD);
        given(cardServiceRepository.findByServiceInAndCard(MULTIPLE_TKM_SERVICES, PARTIAL_USER_VALID_CARD)).willReturn(CARD_1_SERVICES);

        GetConsentResponse response = consentService.getGetConsentResponse(TAX_CODE, HPAN, null);
        Assert.assertEquals(response, expectedResponse);

    }

    @Test
    public void get_givenTaxCodeAndServices_returnValidConsent(){
        GetConsentResponse expectedResponse= new GetConsentResponse();
        expectedResponse.setConsent(ConsentEnum.PARTIAL);
        Consent consent1 = new Consent().setConsent(ConsentEnum.ALLOW).setHpan(HPAN).setServices(ONE_SERVICE_SET);
        ConsentResponse consentResponse1 = new ConsentResponse(consent1);
        Consent consent2 = new Consent().setConsent(ConsentEnum.ALLOW).setHpan(HPAN_2).setServices(CARD_2_SERVICE_SET);
        ConsentResponse consentResponse2 = new ConsentResponse(consent2);
        expectedResponse.setDetails(Arrays.asList(consentResponse1, consentResponse2));

        given(serviceRepository.findByNameIn(MULTIPLE_SERVICE_SUB_SET)).willReturn(MULTIPLE_TKM_SERVICES_SUB);
        given(userRepository.findByTaxCode(TAX_CODE)).willReturn(USER_WITH_PARTIAL_CONSENT);
        given(cardRepository.findByUser(USER_WITH_PARTIAL_CONSENT)).willReturn(PARTIAL_USER_CARDS_LIST);
        given(cardServiceRepository.findByServiceInAndCard(MULTIPLE_TKM_SERVICES_SUB, PARTIAL_USER_VALID_CARD)).willReturn(CARD_1_SERVICES_SUB);
        given(cardServiceRepository.findByServiceInAndCard(MULTIPLE_TKM_SERVICES_SUB, PARTIAL_USER_VALID_CARD_2)).willReturn(CARD_2_SERVICES);

        GetConsentResponse response = consentService.getGetConsentResponse(TAX_CODE, null, SERVICES_SUB_ARRAY);
        Assert.assertEquals(response, expectedResponse);

    }

    @Test
    public void get_givenTaxCodeAndHpanAndServices_returnValidConsent(){
        GetConsentResponse expectedResponse= new GetConsentResponse();
        expectedResponse.setConsent(ConsentEnum.PARTIAL);
        Consent consent1 = new Consent().setConsent(ConsentEnum.ALLOW).setHpan(HPAN).setServices(CARD_1_SERVICE_SET);
        ConsentResponse consentResponse1 = new ConsentResponse(consent1);
        expectedResponse.setDetails(Arrays.asList(consentResponse1));

        given(serviceRepository.findByNameIn(MULTIPLE_SERVICE_SUB_SET)).willReturn(MULTIPLE_TKM_SERVICES_SUB);
        given(userRepository.findByTaxCode(TAX_CODE)).willReturn(USER_WITH_PARTIAL_CONSENT);
        given(cardRepository.findByHpan(HPAN)).willReturn(PARTIAL_USER_VALID_CARD);
        given(cardServiceRepository.findByServiceInAndCard(MULTIPLE_TKM_SERVICES_SUB, PARTIAL_USER_VALID_CARD)).willReturn(CARD_1_SERVICES);

        GetConsentResponse response = consentService.getGetConsentResponse(TAX_CODE, HPAN, SERVICES_SUB_ARRAY);
        Assert.assertEquals(response, expectedResponse);

    }

    @Test(expected = ConsentDataNotFoundException.class)
    public void get_givenNotExistentTaxCode_expectNotFound() throws Exception {
        given(serviceRepository.findAll()).willReturn(MULTIPLE_TKM_SERVICES);
        given(userRepository.findByTaxCode(TAX_CODE)).willReturn(null);
        consentService.getGetConsentResponse(TAX_CODE, null, null);
    }

    @Test(expected = ConsentDataNotFoundException.class)
    public void get_givenNotExistentHpan_expectNotFound() throws Exception {
        given(serviceRepository.findAll()).willReturn(MULTIPLE_TKM_SERVICES);
        given(userRepository.findByTaxCode(TAX_CODE)).willReturn(USER_WITH_PARTIAL_CONSENT);
        given(cardRepository.findByHpan(HPAN)).willReturn(null);
        consentService.getGetConsentResponse(TAX_CODE, HPAN, null);
    }

    @Test(expected = ConsentException.class)
    public void get_givenInvalidServices_expectBadRequest() throws Exception {
        consentService.getGetConsentResponse(TAX_CODE, null, SERVICES_INVALID_SINGLE_ARRAY);
    }

}