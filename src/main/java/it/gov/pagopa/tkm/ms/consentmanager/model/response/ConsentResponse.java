package it.gov.pagopa.tkm.ms.consentmanager.model.response;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;
import lombok.*;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsentResponse {

    public ConsentResponse(Consent consent) {
        setConsent(consent.getConsent());
        setHpan(consent.getHpan());
        setServices(consent.getServices());
    }

    private ConsentEnum consent;

    private String hpan;

    private Set<ServiceEnum> services;

}
