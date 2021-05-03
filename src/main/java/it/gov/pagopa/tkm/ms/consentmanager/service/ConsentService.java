package it.gov.pagopa.tkm.ms.consentmanager.service;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.*;

import java.util.List;
import java.util.Set;

public interface ConsentService {

    ConsentResponse postConsent(String taxCode, ClientEnum clientId, Consent consent);
    GetConsentResponse getGetConsentResponse(String taxCode, String hpan, List<String> services);

}
