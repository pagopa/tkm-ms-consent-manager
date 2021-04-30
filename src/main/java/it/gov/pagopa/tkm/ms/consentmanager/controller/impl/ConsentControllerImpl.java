package it.gov.pagopa.tkm.ms.consentmanager.controller.impl;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import it.gov.pagopa.tkm.ms.consentmanager.controller.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.*;
import it.gov.pagopa.tkm.ms.consentmanager.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class ConsentControllerImpl implements ConsentController {

    @Autowired
    private ConsentService consentManagerService;

    @Override
    public ConsentResponse postConsent(String taxCode, ClientEnum clientId, Consent consent) {
        return consentManagerService.postConsent(taxCode, clientId, consent);
    }

    @Override
    public GetConsentResponse getConsent(String taxCode, String hpan, String[] services) {  return null;  }



}
