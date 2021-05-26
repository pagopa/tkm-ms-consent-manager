package it.gov.pagopa.tkm.ms.consentmanager.controller.impl;

import it.gov.pagopa.tkm.ms.consentmanager.constant.ServiceEnum;
import it.gov.pagopa.tkm.ms.consentmanager.controller.ConsentController;
import it.gov.pagopa.tkm.ms.consentmanager.exception.ConsentException;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.Consent;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.ConsentResponse;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.GetConsentResponse;
import it.gov.pagopa.tkm.ms.consentmanager.service.ConsentService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@RestController
public class ConsentControllerImpl implements ConsentController {

    @Autowired
    private ConsentService consentManagerService;

    @Override
    public ConsentResponse postConsent(String taxCode, String clientId, Consent consent) throws ConsentException {
        consent.setHpan(consent.getHpan() == null ? null : consent.getHpan().toLowerCase());
        taxCode = taxCode.toUpperCase();
        return consentManagerService.postConsent(taxCode, clientId, consent);
    }

    @Override
    public GetConsentResponse getConsent(String taxCode, String hpan, List<ServiceEnum> services) {
        return consentManagerService.getConsentV3(taxCode, hpan, services);
    }

}
