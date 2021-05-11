package it.gov.pagopa.tkm.ms.consentmanager.service;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import it.gov.pagopa.tkm.ms.consentmanager.exception.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.*;
import org.springframework.http.HttpStatus;

public interface ConsentService {

    ConsentResponse postConsent(String taxCode, String clientId, Consent consent) throws ConsentException;

    HttpStatus deleteUser(String taxCode, String clientId) throws ConsentException;

}
