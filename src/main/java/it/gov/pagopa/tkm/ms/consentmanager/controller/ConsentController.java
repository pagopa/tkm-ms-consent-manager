package it.gov.pagopa.tkm.ms.consentmanager.controller;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import it.gov.pagopa.tkm.ms.consentmanager.exception.ConsentException;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.Consent;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.ConsentResponse;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.GetConsentResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.*;

import static it.gov.pagopa.tkm.ms.consentmanager.constant.ApiEndpoints.BASE_PATH_CONSENT;
import static it.gov.pagopa.tkm.ms.consentmanager.constant.ApiParams.CLIENT_ID_HEADER;
import static it.gov.pagopa.tkm.ms.consentmanager.constant.ApiParams.TAX_CODE_HEADER;
import static it.gov.pagopa.tkm.ms.consentmanager.constant.ApiParams.HPAN_QUERY_PARAM;
import static it.gov.pagopa.tkm.ms.consentmanager.constant.ApiParams.SERVICES_QUERY_PARAM;

@RequestMapping(BASE_PATH_CONSENT)
@Validated
public interface ConsentController {

    @PostMapping
    @Transactional
    ConsentResponse postConsent(
            @RequestHeader(TAX_CODE_HEADER) @Valid @Pattern(regexp = Constants.FISCAL_CODE_REGEX) String taxCode,
            @RequestHeader(CLIENT_ID_HEADER) String clientId,
            @RequestBody @Valid Consent consent) throws ConsentException;

    @GetMapping
    GetConsentResponse getConsent(
            @RequestHeader(TAX_CODE_HEADER) @Valid @Size(min = 16, max = 16) String taxCode,
            @RequestParam(value = HPAN_QUERY_PARAM, required = false)  @Valid @Size(min = 64, max = 64) String hpan,
            @RequestParam(value = SERVICES_QUERY_PARAM, required = false) String[] services) throws Exception;
}
