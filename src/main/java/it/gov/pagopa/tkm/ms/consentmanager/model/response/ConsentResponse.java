package it.gov.pagopa.tkm.ms.consentmanager.model.response;

import com.fasterxml.jackson.annotation.*;
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

    private ConsentRequestEnum consent;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String hpan;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<ServiceEnum> services;

}
