package it.gov.pagopa.tkm.ms.consentmanager.controller;

import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.*;
import org.springframework.web.bind.annotation.*;

import static it.gov.pagopa.tkm.ms.consentmanager.constant.ApiEndpoints.BASE_PATH_CONSENT;

@RequestMapping(BASE_PATH_CONSENT)
public interface ConsentController {

    @PostMapping
    ConsentResponse postConsent(String taxCode, Consent consent);

}
