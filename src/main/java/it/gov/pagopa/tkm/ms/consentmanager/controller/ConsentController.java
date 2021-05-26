package it.gov.pagopa.tkm.ms.consentmanager.controller;

import it.gov.pagopa.tkm.annotation.*;
import it.gov.pagopa.tkm.constant.*;
import it.gov.pagopa.tkm.ms.consentmanager.exception.ConsentException;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.Consent;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.ConsentResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import javax.validation.constraints.*;

import static it.gov.pagopa.tkm.ms.consentmanager.constant.ApiEndpoints.BASE_PATH_CONSENT;
import static it.gov.pagopa.tkm.ms.consentmanager.constant.ApiParams.CLIENT_ID_HEADER;
import static it.gov.pagopa.tkm.ms.consentmanager.constant.ApiParams.TAX_CODE_HEADER;

@RequestMapping(BASE_PATH_CONSENT)
@Validated
public interface ConsentController {

    @PostMapping
    @Transactional
    ConsentResponse postConsent(
            @RequestHeader(TAX_CODE_HEADER) @Valid @Pattern(regexp = Constants.FISCAL_CODE_REGEX) @StringFormat(StringFormatEnum.UPPERCASE) String taxCode,
            @RequestHeader(CLIENT_ID_HEADER) String clientId,
            @RequestBody @Valid Consent consent) throws ConsentException;
}
