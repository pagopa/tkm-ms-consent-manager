package it.gov.pagopa.tkm.ms.consentmanager.service;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import it.gov.pagopa.tkm.ms.consentmanager.exception.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.*;
import it.gov.pagopa.tkm.ms.consentmanager.repository.*;
import it.gov.pagopa.tkm.ms.consentmanager.service.impl.*;
import it.gov.pagopa.tkm.ms.consentmanager.utils.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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

    @BeforeEach
    public void init() {
        testBeans = new DefaultBeans();
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
        assertThrows(ConsentException.class, () -> {
            consentService.postConsent(testBeans.TAX_CODE, testBeans.CLIENT_ID, testBeans.ALLOW_CONSENT_ALL_SERVICES_REQUEST);
        });
    }

    @Test
    public void givenNewTaxCode_createNewUser() {
        try (MockedStatic<DateUtils> dateUtils = mockStatic(DateUtils.class)) {
            dateUtils.when(DateUtils::now).thenReturn(testBeans.INSTANT);
            when(userRepository.findByTaxCode(testBeans.TAX_CODE)).thenReturn(null);
            consentService.postConsent(testBeans.TAX_CODE, testBeans.CLIENT_ID, testBeans.GLOBAL_ALLOW_CONSENT_REQUEST);
            verify(userRepository).save(testBeans.USER_WITH_GLOBAL_ALLOW_CONSENT);
        }
    }

    @Test
    public void givenExistingTaxCode_updateUser() {
        try (MockedStatic<DateUtils> dateUtils = mockStatic(DateUtils.class)) {
            dateUtils.when(DateUtils::now).thenReturn(testBeans.INSTANT);
            when(userRepository.findByTaxCode(testBeans.TAX_CODE)).thenReturn(testBeans.USER_WITH_PARTIAL_CONSENT);
            consentService.postConsent(testBeans.TAX_CODE, testBeans.CLIENT_ID, testBeans.GLOBAL_ALLOW_CONSENT_REQUEST);
            verify(userRepository).save(testBeans.USER_WITH_GLOBAL_ALLOW_CONSENT_UPDATED);
        }
    }

    @Test
    public void givenNewHpan_createNewCard() {
        when(userRepository.findByTaxCode(testBeans.TAX_CODE)).thenReturn(testBeans.USER_WITH_PARTIAL_CONSENT);
        when(cardRepository.findByHpan(testBeans.HPAN)).thenReturn(null);
        consentService.postConsent(testBeans.TAX_CODE, testBeans.CLIENT_ID, testBeans.ALLOW_CONSENT_ALL_SERVICES_REQUEST);
        verify(cardRepository).save(testBeans.CARD_FROM_USER_WITH_PARTIAL_CONSENT);
    }

    @Test
    public void givenPartialConsentRequestWithoutServices_applyConsentToAllServices() {
        try (MockedStatic<DateUtils> dateUtils = mockStatic(DateUtils.class)) {
            dateUtils.when(DateUtils::now).thenReturn(testBeans.INSTANT);
            when(userRepository.findByTaxCode(testBeans.TAX_CODE)).thenReturn(testBeans.USER_WITH_PARTIAL_CONSENT);
            when(serviceRepository.findAll()).thenReturn(testBeans.ALL_SERVICES_LIST);
            when(cardServiceRepository.findByServiceInAndCard(testBeans.ALL_SERVICES_LIST, testBeans.CARD_FROM_USER_WITH_PARTIAL_CONSENT)).thenReturn(testBeans.CARD_SERVICES_FOR_ONE_SERVICE_LIST);
            when(cardRepository.findByHpan(testBeans.HPAN)).thenReturn(testBeans.CARD_FROM_USER_WITH_PARTIAL_CONSENT);
            consentService.postConsent(testBeans.TAX_CODE, testBeans.CLIENT_ID, testBeans.ALLOW_CONSENT_ALL_SERVICES_REQUEST);
            verify(serviceRepository).findAll();
            verify(cardServiceRepository).findByServiceInAndCard(testBeans.ALL_SERVICES_LIST, testBeans.CARD_FROM_USER_WITH_PARTIAL_CONSENT);
            verify(cardServiceRepository).saveAll(cardServiceListCaptor.capture());
            assertThat(cardServiceListCaptor.getValue()).containsExactlyInAnyOrderElementsOf(testBeans.CARD_SERVICES_FOR_ALL_SERVICES_LIST);
        }
    }

    @Test
    public void givenPartialConsentRequestWithServices_applyConsentToGivenServices() {
        try (MockedStatic<DateUtils> dateUtils = mockStatic(DateUtils.class)) {
            dateUtils.when(DateUtils::now).thenReturn(testBeans.INSTANT);
            when(userRepository.findByTaxCode(testBeans.TAX_CODE)).thenReturn(testBeans.USER_WITH_PARTIAL_CONSENT);
            when(serviceRepository.findByNameIn(testBeans.ONE_SERVICE_SET)).thenReturn(testBeans.ONE_SERVICE_LIST);
            when(cardServiceRepository.findByServiceInAndCard(testBeans.ONE_SERVICE_LIST, testBeans.CARD_FROM_USER_WITH_PARTIAL_CONSENT)).thenReturn(null);
            consentService.postConsent(testBeans.TAX_CODE, testBeans.CLIENT_ID, testBeans.ALLOW_CONSENT_ONE_SERVICE_REQUEST);
            verify(serviceRepository).findByNameIn(testBeans.ONE_SERVICE_SET);
            verify(cardServiceRepository).findByServiceInAndCard(testBeans.ONE_SERVICE_LIST, testBeans.CARD_FROM_USER_WITH_PARTIAL_CONSENT);
            verify(cardServiceRepository).saveAll(cardServiceListCaptor.capture());
            assertThat(cardServiceListCaptor.getValue()).containsExactlyInAnyOrderElementsOf(testBeans.CARD_SERVICES_FOR_ONE_SERVICE_LIST);
        }
    }

}
