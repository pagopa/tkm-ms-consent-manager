package it.gov.pagopa.tkm.ms.consentmanager.controller;

import it.gov.pagopa.tkm.ms.consentmanager.exception.ConsentException;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.Consent;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.ConsentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import static it.gov.pagopa.tkm.ms.consentmanager.constant.ApiEndpoints.BASE_PATH_CONSENT;
import static it.gov.pagopa.tkm.ms.consentmanager.constant.ApiParams.CLIENT_ID_HEADER;
import static it.gov.pagopa.tkm.ms.consentmanager.constant.ApiParams.TAX_CODE_HEADER;

@RequestMapping(BASE_PATH_CONSENT)
@Validated
public interface ConsentController {

    @PostMapping
    @Transactional
    ConsentResponse postConsent(
            @RequestHeader(TAX_CODE_HEADER) @Valid @Size(min = 16, max = 16) String taxCode,
            @RequestHeader(CLIENT_ID_HEADER) String clientId,
            @RequestBody @Valid Consent consent) throws ConsentException;

    @DeleteMapping
    @Transactional
    HttpStatus deleteUser(
            @RequestHeader(TAX_CODE_HEADER) @Valid @Size(min = 16, max = 16) String taxCode,
            @RequestHeader(CLIENT_ID_HEADER) String clientId
    ) throws ConsentException;

}
