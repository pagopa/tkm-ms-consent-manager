package it.gov.pagopa.tkm.ms.consentmanager.service;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import it.gov.pagopa.tkm.ms.consentmanager.exception.ConsentException;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.*;

import java.util.*;

public interface ConsentService {

    ConsentResponse postConsent(String taxCode, String clientId, Consent consent) throws ConsentException;

    ConsentResponse getConsent(String taxCode, String hpan, Set<ServiceEnum> services);
    
    void deleteUser(String taxCode, String clientId) throws ConsentException;

}
