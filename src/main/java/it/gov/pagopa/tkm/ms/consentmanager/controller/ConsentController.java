package it.gov.pagopa.tkm.ms.consentmanager.controller;

import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;

import static it.gov.pagopa.tkm.ms.consentmanager.constant.ApiEndpoints.BASE_PATH_CONSENT;
import static it.gov.pagopa.tkm.ms.consentmanager.constant.ApiParams.TAX_CODE_HEADER;

@RequestMapping(BASE_PATH_CONSENT)
public interface ConsentController {

    @PostMapping
    ConsentResponse postConsent(
            @RequestHeader(TAX_CODE_HEADER) String taxCode,
            @RequestBody @Valid Consent consent);

}
