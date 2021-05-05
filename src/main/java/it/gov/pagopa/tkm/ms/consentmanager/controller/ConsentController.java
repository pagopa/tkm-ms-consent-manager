package it.gov.pagopa.tkm.ms.consentmanager.controller;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import it.gov.pagopa.tkm.ms.consentmanager.exception.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.*;
import org.springframework.transaction.annotation.*;
import org.springframework.validation.annotation.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;
import javax.validation.constraints.*;

import java.util.List;

import static it.gov.pagopa.tkm.ms.consentmanager.constant.ApiEndpoints.BASE_PATH_CONSENT;
import static it.gov.pagopa.tkm.ms.consentmanager.constant.ApiParams.*;

@RequestMapping(BASE_PATH_CONSENT)
@Validated
public interface ConsentController {

    @PostMapping
    @Transactional
    ConsentResponse postConsent(
            @RequestHeader(TAX_CODE_HEADER) @Valid @Size(min = 16, max = 16) String taxCode,
            @RequestHeader(CLIENT_ID_HEADER) ClientEnum clientId,
            @RequestBody @Valid Consent consent) throws ConsentException;

    @GetMapping
    GetConsentResponse getConsent(
            @RequestHeader(TAX_CODE_HEADER) String taxCode,
            @RequestParam(value = HPAN_QUERY_PARAM, required = false)  String hpan,
            @RequestParam(value = SERVICES_QUERY_PARAM, required = false) String[] services) throws Exception;
}
