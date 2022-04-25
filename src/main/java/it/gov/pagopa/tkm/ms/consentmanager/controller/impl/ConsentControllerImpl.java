package it.gov.pagopa.tkm.ms.consentmanager.controller.impl;

import it.gov.pagopa.tkm.ms.consentmanager.constant.ServiceEnum;
import it.gov.pagopa.tkm.ms.consentmanager.controller.ConsentController;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.Consent;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.ConsentResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.SpringProperties;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Log4j2
@RestController
public class ConsentControllerImpl implements ConsentController {

    @Value( "${keyvault.consentMDbUsernameAzure}" )
    private String consentMDbUsernameAzure;

    @Override
    public ConsentResponse postConsent(String taxCode, String clientId, Consent consent) {
        return new ConsentResponse();
    }

    @Override
    public void deleteCitizen(String taxCode) {
    }
    
    @Override
    public ConsentResponse getConsent(String taxCode, String hpan, Set<ServiceEnum> services) {
        return new ConsentResponse();
    }

    @Override
    public String getConsentOk() {
        return "ok " + consentMDbUsernameAzure;
    }

}
