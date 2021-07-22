package it.gov.pagopa.tkm.ms.consentmanager.service.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import it.gov.pagopa.tkm.ms.consentmanager.client.cardmanager.CardManagerClient;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.ConsentResponse;
import it.gov.pagopa.tkm.ms.consentmanager.service.CircuitBreakerManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.ConnectException;

@Log4j2
@Service
public class CircuitBreakerManagerImpl implements CircuitBreakerManager {

    @Autowired
    private CardManagerClient cardManagerClient;

    @CircuitBreaker(name = "cardManagerClientCircuitBreaker", fallbackMethod = "cardManagerClientUpdateConsentFallback")
    @Retry(name ="cardManagerClientRetry", fallbackMethod = "cardManagerClientUpdateConsentFallback")
    public void cardManagerClientUpdateConsent(ConsentResponse consentResponse, String taxCode){
            cardManagerClient.updateConsent(consentResponse.setTaxCode(taxCode));
    }

    public void cardManagerClientUpdateConsentFallback(ConsentResponse consentResponse, String taxCode, Throwable t) throws Exception {
        log.info("Card Manager Client Update Consent Fallback%s- cause {} "+  t.getMessage());
        throw new Exception();
    }

}
