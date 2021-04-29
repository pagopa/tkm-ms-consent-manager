package it.gov.pagopa.tkm.ms.consentmanager.service;

import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.*;

public interface ConsentManagerService {

    ConsentResponse postConsent(String taxCode, Consent consent);

}
