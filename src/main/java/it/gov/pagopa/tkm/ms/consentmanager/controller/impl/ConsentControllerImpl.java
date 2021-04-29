package it.gov.pagopa.tkm.ms.consentmanager.controller.impl;

import it.gov.pagopa.tkm.ms.consentmanager.controller.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class ConsentControllerImpl implements ConsentController {

    @Override
    public ConsentResponse postConsent(String taxCode, Consent consent) {
        return null;
    }

}
