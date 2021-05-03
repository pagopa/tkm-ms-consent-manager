package it.gov.pagopa.tkm.ms.consentmanager.controller;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import it.gov.pagopa.tkm.ms.consentmanager.exception.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.*;
import org.springframework.transaction.annotation.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;

import static it.gov.pagopa.tkm.ms.consentmanager.constant.ApiEndpoints.BASE_PATH_CONSENT;
import static it.gov.pagopa.tkm.ms.consentmanager.constant.ApiParams.*;

@RequestMapping(BASE_PATH_CONSENT)
public interface ConsentController {

    @PostMapping
    @Transactional
    ConsentResponse postConsent(
            @RequestHeader(TAX_CODE_HEADER) String taxCode,
            @RequestHeader(CLIENT_ID_HEADER) ClientEnum clientId,
            @RequestBody @Valid Consent consent) throws ConsentException;

}
