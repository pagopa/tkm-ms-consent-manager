package it.gov.pagopa.tkm.ms.consentmanager.service;

import it.gov.pagopa.tkm.ms.consentmanager.model.response.ConsentResponse;

import java.net.ConnectException;

public interface CircuitBreakerManager {

  void cardManagerClientUpdateConsent(ConsentResponse consentResponse, String taxcode);
  void cardManagerClientUpdateConsentFallback(ConsentResponse consentResponse, String taxCode, Throwable t) throws Exception;

}
