package it.gov.pagopa.tkm.ms.consentmanager.controller;

import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;

import static it.gov.pagopa.tkm.ms.consentmanager.constant.ApiEndpoints.BASE_PATH_CONSENT;
import static it.gov.pagopa.tkm.ms.consentmanager.constant.ApiParams.*;

@RequestMapping(BASE_PATH_CONSENT)
public interface ConsentController {

    @PostMapping
    ConsentResponse postConsent(
            @RequestHeader(TAX_CODE_HEADER) String taxCode,
            @RequestBody @Valid Consent consent);

    @GetMapping
    GetConsentResponse getConsent(
            @RequestHeader(TAX_CODE_HEADER) String taxCode,
            @RequestParam(value = HPAN_QUERY_PARAM, required = false)  String hpan,
            @RequestParam(value = SERVICES_QUERY_PARAM, required = false)  String[] services);
}
