package it.gov.pagopa.tkm.ms.consentmanager.controller.impl;

import it.gov.pagopa.tkm.ms.consentmanager.constant.ErrorCodeEnum;
import it.gov.pagopa.tkm.ms.consentmanager.constant.ServiceEnum;
import it.gov.pagopa.tkm.ms.consentmanager.controller.ConsentController;
import it.gov.pagopa.tkm.ms.consentmanager.exception.ConsentException;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.Consent;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.ConsentResponse;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.GetConsentResponse;
import it.gov.pagopa.tkm.ms.consentmanager.service.ConsentService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@RestController
public class ConsentControllerImpl implements ConsentController {

    @Autowired
    private ConsentService consentManagerService;

    @Override
    public ConsentResponse postConsent(String taxCode, String clientId, Consent consent) throws ConsentException {
        return consentManagerService.postConsent(taxCode, clientId, consent);
    }

    @Override
    public GetConsentResponse getConsent(String taxCode, String hpan, List<ServiceEnum> services) {
        if ((hpan!=null && CollectionUtils.isEmpty(services)) || (hpan==null && !CollectionUtils.isEmpty(services))){
            throw new ConsentException(ErrorCodeEnum.HPAN_AND_SERVICES_PARAMS_NOT_COHERENT);
        }
        return consentManagerService.getConsentV3(taxCode, hpan, services);
    }

}
