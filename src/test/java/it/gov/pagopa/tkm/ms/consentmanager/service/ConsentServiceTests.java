package it.gov.pagopa.tkm.ms.consentmanager.service;

import it.gov.pagopa.tkm.ms.consentmanager.exception.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.*;
import it.gov.pagopa.tkm.ms.consentmanager.repository.*;
import it.gov.pagopa.tkm.ms.consentmanager.service.impl.*;
import it.gov.pagopa.tkm.ms.consentmanager.utils.*;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.junit.*;

import static it.gov.pagopa.tkm.ms.consentmanager.constant.TestBeans.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
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
        try (MockedStatic<DateUtils> dateUtils = mockStatic(DateUtils.class)) {
            dateUtils.when(DateUtils::now).thenReturn(INSTANT);
            given(userRepository.findByTaxCode(TAX_CODE)).willReturn(null);
            consentService.postConsent(TAX_CODE, CLIENT_ID, GLOBAL_ALLOW_CONSENT_REQUEST);
            verify(userRepository).save(USER_WITH_GLOBAL_ALLOW_CONSENT);
        }
    }

    @Test
    public void givenExistingTaxCode_updateUser() {
        try (MockedStatic<DateUtils> dateUtils = mockStatic(DateUtils.class)) {
            dateUtils.when(DateUtils::now).thenReturn(INSTANT);
            given(userRepository.findByTaxCode(TAX_CODE)).willReturn(USER_WITH_PARTIAL_CONSENT);
            consentService.postConsent(TAX_CODE, CLIENT_ID, GLOBAL_ALLOW_CONSENT_REQUEST);
            verify(userRepository).save(USER_WITH_GLOBAL_ALLOW_CONSENT.setConsentUpdateDate(INSTANT));
        }
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
        try (MockedStatic<DateUtils> dateUtils = mockStatic(DateUtils.class)) {
            dateUtils.when(DateUtils::now).thenReturn(INSTANT);
            given(userRepository.findByTaxCode(TAX_CODE)).willReturn(USER_WITH_PARTIAL_CONSENT);
            given(serviceRepository.findAll()).willReturn(ALL_SERVICES_LIST);
            given(cardServiceRepository.findByServiceInAndCard(ALL_SERVICES_LIST, CARD_FROM_USER_WITH_PARTIAL_CONSENT)).willReturn(CARD_SERVICES_FOR_ONE_SERVICE_LIST);
            given(cardRepository.findByHpan(HPAN)).willReturn(CARD_FROM_USER_WITH_PARTIAL_CONSENT);
            consentService.postConsent(TAX_CODE, CLIENT_ID, ALLOW_CONSENT_ALL_SERVICES_REQUEST);
            verify(serviceRepository).findAll();
            verify(cardServiceRepository).findByServiceInAndCard(ALL_SERVICES_LIST, CARD_FROM_USER_WITH_PARTIAL_CONSENT);
            verify(cardServiceRepository).saveAll(CARD_SERVICES_FOR_ALL_SERVICES_LIST);
        }
    }

    @Test
    public void givenPartialConsentRequestWithServices_applyConsentToGivenServices() {
        try (MockedStatic<DateUtils> dateUtils = mockStatic(DateUtils.class)) {
            dateUtils.when(DateUtils::now).thenReturn(INSTANT);
            given(userRepository.findByTaxCode(TAX_CODE)).willReturn(USER_WITH_PARTIAL_CONSENT);
            given(serviceRepository.findByNameIn(ONE_SERVICE_SET)).willReturn(ONE_SERVICE_LIST);
            given(cardServiceRepository.findByServiceInAndCard(ONE_SERVICE_LIST, CARD_FROM_USER_WITH_PARTIAL_CONSENT)).willReturn(null);
            consentService.postConsent(TAX_CODE, CLIENT_ID, ALLOW_CONSENT_ONE_SERVICE_REQUEST);
            verify(serviceRepository).findByNameIn(ONE_SERVICE_SET);
            verify(cardServiceRepository).findByServiceInAndCard(ONE_SERVICE_LIST, CARD_FROM_USER_WITH_PARTIAL_CONSENT);
            verify(cardServiceRepository).saveAll(CARD_SERVICES_FOR_ONE_SERVICE_LIST);
        }
    }

}
