package it.gov.pagopa.tkm.ms.consentmanager.service;

import it.gov.pagopa.tkm.ms.consentmanager.constant.ApiEndpoints;
import it.gov.pagopa.tkm.ms.consentmanager.constant.ApiParams;
import it.gov.pagopa.tkm.ms.consentmanager.constant.ConsentEnum;
import it.gov.pagopa.tkm.ms.consentmanager.exception.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.*;
import it.gov.pagopa.tkm.ms.consentmanager.repository.*;
import it.gov.pagopa.tkm.ms.consentmanager.service.impl.*;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.junit.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static it.gov.pagopa.tkm.ms.consentmanager.TestBeans.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class ConsentServiceTests {

    @InjectMocks
    private ConsentServiceImpl consentService;

    @Mock
    CardRepository cardRepository;

    @Mock
    CardServiceRepository cardServiceRepository;

    @Mock
    ServiceRepository serviceRepository;

    @Mock
    UserRepository userRepository;

    @Test
    public void givenValidConsentRequest_returnValidConsentResponse() {
        for (Consent consent : VALID_CONSENT_REQUESTS) {
            ConsentResponse consentResponse = consentService.postConsent(TAX_CODE, CLIENT_ID, consent);
            Assert.assertEquals(consentResponse, new ConsentResponse(consent));
        }
    }

    @Test(expected = Exception.class)
    public void givenInvalidConsentRequest_expectException() {
        for (Consent consent : INVALID_CONSENT_REQUESTS) {
            consentService.postConsent(TAX_CODE, CLIENT_ID, consent);
        }
    }

    @Test(expected = ConsentException.class)
    public void givenPartialConsentRequestFromUserWithGlobalConsent_expectException() {
        given(userRepository.findByTaxCode(TAX_CODE)).willReturn(USER_WITH_GLOBAL_ALLOW);
        consentService.postConsent(TAX_CODE, CLIENT_ID, ALLOW_CONSENT_ALL_SERVICES_REQUEST);
    }

    //TODO

    @Test
    public void get_givenTaxCodeWithGlobalAllow_returnValidConsent(){
        GetConsentResponse expectedResponse= new GetConsentResponse();
        expectedResponse.setConsent(ConsentEnum.ALLOW);
        expectedResponse.setDetails(null);

        given(serviceRepository.findAll()).willReturn(MULTIPLE_TKM_SERVICES);
        given(userRepository.findByTaxCode(TAX_CODE)).willReturn(USER_WITH_GLOBAL_ALLOW);

        GetConsentResponse response = consentService.getGetConsentResponse(TAX_CODE, null, null);
        Assert.assertEquals(response, expectedResponse);

    }


    @Test
    public void get_givenTaxCodeWithGlobalDeny_returnValidConsent(){
        GetConsentResponse expectedResponse= new GetConsentResponse();
        expectedResponse.setConsent(ConsentEnum.DENY);
        expectedResponse.setDetails(null);

        given(serviceRepository.findAll()).willReturn(MULTIPLE_TKM_SERVICES);
        given(userRepository.findByTaxCode(TAX_CODE)).willReturn(USER_WITH_GLOBAL_DENY);

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
        given(userRepository.findByTaxCode(TAX_CODE)).willReturn(USER_WITH_GLOBAL_PARTIAL);
        given(cardRepository.findByUser(USER_WITH_GLOBAL_PARTIAL)).willReturn(PARTIAL_USER_CARDS_LIST);

        given(cardServiceRepository.findByServiceInAndCard(MULTIPLE_TKM_SERVICES, PARTIAL_USER_VALID_CARD)).willReturn(CARD_1_SERVICES);
        given(cardServiceRepository.findByServiceInAndCard(MULTIPLE_TKM_SERVICES, PARTIAL_USER_VALID_CARD_2)).willReturn(CARD_2_SERVICES);

        GetConsentResponse response = consentService.getGetConsentResponse(TAX_CODE, null, null);
        Assert.assertEquals(response, expectedResponse);

    }

    @Test
    public void getGivenTaxCodeAndHpan_returnValidConsent(){
        GetConsentResponse expectedResponse= new GetConsentResponse();
        expectedResponse.setConsent(ConsentEnum.PARTIAL);
        Consent consent1 = new Consent().setConsent(ConsentEnum.ALLOW).setHpan(HPAN).setServices(CARD_1_SERVICE_SET);
        ConsentResponse consentResponse1 = new ConsentResponse(consent1);
        expectedResponse.setDetails(Arrays.asList(consentResponse1));

        given(serviceRepository.findAll()).willReturn(MULTIPLE_TKM_SERVICES);
        given(userRepository.findByTaxCode(TAX_CODE)).willReturn(USER_WITH_GLOBAL_PARTIAL);
        given(cardRepository.findByHpan(HPAN)).willReturn(PARTIAL_USER_VALID_CARD);
        given(cardServiceRepository.findByServiceInAndCard(MULTIPLE_TKM_SERVICES, PARTIAL_USER_VALID_CARD)).willReturn(CARD_1_SERVICES);

        GetConsentResponse response = consentService.getGetConsentResponse(TAX_CODE, HPAN, null);
        Assert.assertEquals(response, expectedResponse);

    }

    @Test
    public void getGivenTaxCodeAndServices_returnValidConsent(){
        GetConsentResponse expectedResponse= new GetConsentResponse();
        expectedResponse.setConsent(ConsentEnum.PARTIAL);
        Consent consent1 = new Consent().setConsent(ConsentEnum.ALLOW).setHpan(HPAN).setServices(ONE_SERVICE_SET);
        ConsentResponse consentResponse1 = new ConsentResponse(consent1);
        Consent consent2 = new Consent().setConsent(ConsentEnum.ALLOW).setHpan(HPAN_2).setServices(CARD_2_SERVICE_SET);
        ConsentResponse consentResponse2 = new ConsentResponse(consent2);
        expectedResponse.setDetails(Arrays.asList(consentResponse1, consentResponse2));

        given(serviceRepository.findByNameIn(MULTIPLE_SERVICE_SUB_SET)).willReturn(MULTIPLE_TKM_SERVICES_SUB);
        given(userRepository.findByTaxCode(TAX_CODE)).willReturn(USER_WITH_GLOBAL_PARTIAL);
        given(cardRepository.findByUser(USER_WITH_GLOBAL_PARTIAL)).willReturn(PARTIAL_USER_CARDS_LIST);
        given(cardServiceRepository.findByServiceInAndCard(MULTIPLE_TKM_SERVICES_SUB, PARTIAL_USER_VALID_CARD)).willReturn(CARD_1_SERVICES_SUB);
        given(cardServiceRepository.findByServiceInAndCard(MULTIPLE_TKM_SERVICES_SUB, PARTIAL_USER_VALID_CARD_2)).willReturn(CARD_2_SERVICES);

        GetConsentResponse response = consentService.getGetConsentResponse(TAX_CODE, null, SERVICES_SUB_ARRAY);
        Assert.assertEquals(response, expectedResponse);

    }

    @Test
    public void getGivenTaxCodeAndHpanAndServices_returnValidConsent(){
        GetConsentResponse expectedResponse= new GetConsentResponse();
        expectedResponse.setConsent(ConsentEnum.PARTIAL);
        Consent consent1 = new Consent().setConsent(ConsentEnum.ALLOW).setHpan(HPAN).setServices(CARD_1_SERVICE_SET);
        ConsentResponse consentResponse1 = new ConsentResponse(consent1);
        expectedResponse.setDetails(Arrays.asList(consentResponse1));

        given(serviceRepository.findByNameIn(MULTIPLE_SERVICE_SUB_SET)).willReturn(MULTIPLE_TKM_SERVICES_SUB);
        given(userRepository.findByTaxCode(TAX_CODE)).willReturn(USER_WITH_GLOBAL_PARTIAL);
        given(cardRepository.findByHpan(HPAN)).willReturn(PARTIAL_USER_VALID_CARD);
        given(cardServiceRepository.findByServiceInAndCard(MULTIPLE_TKM_SERVICES_SUB, PARTIAL_USER_VALID_CARD)).willReturn(CARD_1_SERVICES);

        GetConsentResponse response = consentService.getGetConsentResponse(TAX_CODE, HPAN, SERVICES_SUB_ARRAY);
        Assert.assertEquals(response, expectedResponse);

    }

    @Test
    public void getGivenInvalidTaxCode_expectNotFound() throws Exception {
        given(serviceRepository.findAll()).willReturn(MULTIPLE_TKM_SERVICES);
        given(userRepository.findByTaxCode(TAX_CODE)).willReturn(null);
        consentService.getGetConsentResponse(TAX_CODE, null, null);
    }

    @Test
    public void getGivenInvalidHpan_expectNotFound() throws Exception {
        given(serviceRepository.findAll()).willReturn(MULTIPLE_TKM_SERVICES);
        given(userRepository.findByTaxCode(TAX_CODE)).willReturn(USER_WITH_GLOBAL_PARTIAL);
        given(cardRepository.findByHpan(HPAN)).willReturn(null);
        consentService.getGetConsentResponse(TAX_CODE, HPAN, null);
    }

    @Test
    public void get_givenInvalidServices_expectBadRequest() throws Exception {
        consentService.getGetConsentResponse(TAX_CODE, null, SERVICES_INVALID_SINGLE_ARRAY);
    }

}
