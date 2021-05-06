package it.gov.pagopa.tkm.ms.consentmanager.service;

import it.gov.pagopa.tkm.ms.consentmanager.exception.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.*;
import it.gov.pagopa.tkm.ms.consentmanager.repository.*;
import it.gov.pagopa.tkm.ms.consentmanager.service.impl.*;
import it.gov.pagopa.tkm.ms.consentmanager.utils.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

import static it.gov.pagopa.tkm.ms.consentmanager.constant.TestBeans.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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

    @Test
    public void givenPartialConsentRequestFromUserWithGlobalConsent_expectException() {
        when(userRepository.findByTaxCode(TAX_CODE)).thenReturn(USER_WITH_GLOBAL_ALLOW_CONSENT);
        assertThrows(ConsentException.class, () -> {
            consentService.postConsent(TAX_CODE, CLIENT_ID, ALLOW_CONSENT_ALL_SERVICES_REQUEST);
        });
    }

    @Test
    public void givenNewTaxCode_createNewUser() {
        try (MockedStatic<DateUtils> dateUtils = mockStatic(DateUtils.class)) {
            dateUtils.when(DateUtils::now).thenReturn(INSTANT);
            when(userRepository.findByTaxCode(TAX_CODE)).thenReturn(null);
            consentService.postConsent(TAX_CODE, CLIENT_ID, GLOBAL_ALLOW_CONSENT_REQUEST);
            verify(userRepository).save(USER_WITH_GLOBAL_ALLOW_CONSENT);
        }
    }

    @Test
    public void givenExistingTaxCode_updateUser() {
        try (MockedStatic<DateUtils> dateUtils = mockStatic(DateUtils.class)) {
            dateUtils.when(DateUtils::now).thenReturn(INSTANT);
            when(userRepository.findByTaxCode(TAX_CODE)).thenReturn(USER_WITH_PARTIAL_CONSENT);
            consentService.postConsent(TAX_CODE, CLIENT_ID, GLOBAL_ALLOW_CONSENT_REQUEST);
            verify(userRepository).save(USER_WITH_GLOBAL_ALLOW_CONSENT_UPDATED);
        }
    }

    @Test
    public void givenNewHpan_createNewCard() {
        when(userRepository.findByTaxCode(TAX_CODE)).thenReturn(USER_WITH_PARTIAL_CONSENT);
        when(cardRepository.findByHpan(HPAN)).thenReturn(null);
        consentService.postConsent(TAX_CODE, CLIENT_ID, ALLOW_CONSENT_ALL_SERVICES_REQUEST);
        verify(cardRepository).save(CARD_FROM_USER_WITH_PARTIAL_CONSENT);
    }

    @Test
    public void givenPartialConsentRequestWithoutServices_applyConsentToAllServices() {
        try (MockedStatic<DateUtils> dateUtils = mockStatic(DateUtils.class)) {
            dateUtils.when(DateUtils::now).thenReturn(INSTANT);
            when(userRepository.findByTaxCode(TAX_CODE)).thenReturn(USER_WITH_PARTIAL_CONSENT);
            when(serviceRepository.findAll()).thenReturn(ALL_SERVICES_LIST);
            when(cardServiceRepository.findByServiceInAndCard(ALL_SERVICES_LIST, CARD_FROM_USER_WITH_PARTIAL_CONSENT)).thenReturn(CARD_SERVICES_FOR_ONE_SERVICE_LIST);
            when(cardRepository.findByHpan(HPAN)).thenReturn(CARD_FROM_USER_WITH_PARTIAL_CONSENT);
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
            when(userRepository.findByTaxCode(TAX_CODE)).thenReturn(USER_WITH_PARTIAL_CONSENT);
            when(serviceRepository.findByNameIn(ONE_SERVICE_SET)).thenReturn(ONE_SERVICE_LIST);
            when(cardServiceRepository.findByServiceInAndCard(ONE_SERVICE_LIST, CARD_FROM_USER_WITH_PARTIAL_CONSENT)).thenReturn(null);
            consentService.postConsent(TAX_CODE, CLIENT_ID, ALLOW_CONSENT_ONE_SERVICE_REQUEST);
            verify(serviceRepository).findByNameIn(ONE_SERVICE_SET);
            verify(cardServiceRepository).findByServiceInAndCard(ONE_SERVICE_LIST, CARD_FROM_USER_WITH_PARTIAL_CONSENT);
            verify(cardServiceRepository).saveAll(CARD_SERVICES_FOR_ONE_SERVICE_LIST);
        }
    }

}
