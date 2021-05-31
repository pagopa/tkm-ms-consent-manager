package it.gov.pagopa.tkm.ms.consentmanager.model.response;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceConsent {

    private ConsentRequestEnum consent;

    private ServiceEnum service;

}
