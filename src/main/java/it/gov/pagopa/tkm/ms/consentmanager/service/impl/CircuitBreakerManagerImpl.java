package it.gov.pagopa.tkm.ms.consentmanager.service.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import it.gov.pagopa.tkm.ms.consentmanager.client.cardmanager.CardManagerClient;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.ConsentResponse;
import it.gov.pagopa.tkm.ms.consentmanager.service.CircuitBreakerManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class CircuitBreakerManagerImpl implements CircuitBreakerManager {

    @Autowired
    private CardManagerClient cardManagerClient;

    @CircuitBreaker(name = "cardManagerClientCircuitBreaker", fallbackMethod = "cardManagerClientUpdateConsentFallback")
    public void cardManagerClientUpdateConsent(ConsentResponse consentResponse){
            cardManagerClient.updateConsent(consentResponse);
    }

    public void cardManagerClientUpdateConsentFallback(ConsentResponse consentResponse, Throwable t) throws Exception {
        log.info("Card Manager Client Update Consent Fallback - cause: " + t.getMessage());
        throw new Exception();
    }

}
