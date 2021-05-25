package it.gov.pagopa.tkm.ms.consentmanager.service;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import it.gov.pagopa.tkm.ms.consentmanager.exception.ConsentException;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.*;

import java.util.List;
import java.util.Set;

public interface ConsentService {

    ConsentResponse postConsent(String taxCode, String clientId, Consent consent) throws ConsentException;
    GetConsentResponse getConsentV2(String taxCode, String hpan, ServiceEnum[] services);

}
