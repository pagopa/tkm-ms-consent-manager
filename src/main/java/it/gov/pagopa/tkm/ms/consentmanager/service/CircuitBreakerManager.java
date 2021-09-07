package it.gov.pagopa.tkm.ms.consentmanager.service;

import it.gov.pagopa.tkm.ms.consentmanager.model.response.ConsentResponse;

public interface CircuitBreakerManager {

  void cardManagerClientUpdateConsent(ConsentResponse consentResponse);

  void cardManagerClientUpdateConsentFallback(ConsentResponse consentResponse, Throwable t) throws Exception;

}
