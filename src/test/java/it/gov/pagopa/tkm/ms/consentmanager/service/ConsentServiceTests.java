package it.gov.pagopa.tkm.ms.consentmanager.service;

import it.gov.pagopa.tkm.ms.consentmanager.exception.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.*;
import it.gov.pagopa.tkm.ms.consentmanager.repository.*;
import it.gov.pagopa.tkm.ms.consentmanager.service.impl.*;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.junit.*;

import static it.gov.pagopa.tkm.ms.consentmanager.TestBeans.*;
import static org.mockito.BDDMockito.given;

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

}
