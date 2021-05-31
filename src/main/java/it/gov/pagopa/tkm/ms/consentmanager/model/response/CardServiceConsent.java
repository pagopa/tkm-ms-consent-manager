package it.gov.pagopa.tkm.ms.consentmanager.model.response;

import lombok.*;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardServiceConsent {

    private String hpan;

    private List<ServiceConsent> serviceConsents;

}
