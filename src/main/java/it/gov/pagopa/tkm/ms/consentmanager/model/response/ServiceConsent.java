package it.gov.pagopa.tkm.ms.consentmanager.model.response;

import it.gov.pagopa.tkm.ms.consentmanager.constant.ConsentRequestEnum;
import it.gov.pagopa.tkm.ms.consentmanager.constant.ServiceEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceConsent {

    private ConsentRequestEnum consent;

    private ServiceEnum service;

}
