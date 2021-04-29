package it.gov.pagopa.tkm.ms.consentmanager.controller.impl;

import it.gov.pagopa.tkm.ms.consentmanager.controller.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;

import static it.gov.pagopa.tkm.ms.consentmanager.constant.ApiParams.*;

@RestController
public class ConsentControllerImpl implements ConsentController {

    @Override
    public ConsentResponse postConsent(
            @RequestHeader(TAX_CODE_HEADER) String taxCode,
            @RequestBody @Valid Consent consent) {
        return null;
    }

}
