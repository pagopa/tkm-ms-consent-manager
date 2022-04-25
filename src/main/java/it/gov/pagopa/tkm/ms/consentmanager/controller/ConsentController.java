package it.gov.pagopa.tkm.ms.consentmanager.controller;

import it.gov.pagopa.tkm.ms.consentmanager.constant.ServiceEnum;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.Consent;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.ConsentResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.Set;

import static it.gov.pagopa.tkm.ms.consentmanager.constant.ApiEndpoints.BASE_PATH_CONSENT;
import static it.gov.pagopa.tkm.ms.consentmanager.constant.ApiParams.*;

@RequestMapping(BASE_PATH_CONSENT)
@Validated
public interface ConsentController {

    @PostMapping
    ConsentResponse postConsent(
            @RequestHeader(TAX_CODE_HEADER) @Valid String taxCode,
            @RequestHeader(CLIENT_ID_HEADER) String clientId,
            @RequestBody @Valid Consent consent);

    @GetMapping
    ConsentResponse getConsent(
            @RequestHeader(TAX_CODE_HEADER) @Valid String taxCode,
            @RequestParam(value = HPAN_QUERY_PARAM, required = false) @Valid @Size(min = 64, max = 64) String hpan,
            @RequestParam(value = SERVICES_QUERY_PARAM, required = false) Set<ServiceEnum> services);

    @GetMapping(path = "/ok")
    String getConsentOk();

    @DeleteMapping
    void deleteCitizen(
            @RequestHeader(TAX_CODE_HEADER) @Valid String taxCode);

}
