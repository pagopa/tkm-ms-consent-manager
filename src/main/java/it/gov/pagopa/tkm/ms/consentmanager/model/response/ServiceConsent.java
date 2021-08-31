package it.gov.pagopa.tkm.ms.consentmanager.model.response;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceConsent {

    public ServiceConsent(TkmCardService tkmCardService) {
        consent = tkmCardService.getConsentType();
        service = tkmCardService.getService().getName();
    }

    private ConsentRequestEnum consent;

    private ServiceEnum service;

}
