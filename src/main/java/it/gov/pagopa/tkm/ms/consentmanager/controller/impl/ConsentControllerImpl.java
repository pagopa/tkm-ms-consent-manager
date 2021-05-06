package it.gov.pagopa.tkm.ms.consentmanager.controller.impl;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import it.gov.pagopa.tkm.ms.consentmanager.controller.*;
import it.gov.pagopa.tkm.ms.consentmanager.exception.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.*;
import it.gov.pagopa.tkm.ms.consentmanager.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

import static it.gov.pagopa.tkm.ms.consentmanager.constant.ConsentEnum.PARTIAL;
import static it.gov.pagopa.tkm.ms.consentmanager.constant.ErrorCodeEnum.CONSENT_TYPE_NOT_PERMITTED;

@RestController
public class ConsentControllerImpl implements ConsentController {

    @Autowired
    private ConsentService consentManagerService;

    @Override
    public ConsentResponse postConsent(String taxCode, ClientEnum clientId, Consent consent) throws ConsentException {
        if (PARTIAL.equals(consent.getConsent())) {
            throw new ConsentException(CONSENT_TYPE_NOT_PERMITTED);
        }
        return consentManagerService.postConsent(taxCode, clientId, consent);
    }

}
