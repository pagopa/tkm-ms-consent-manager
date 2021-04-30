package it.gov.pagopa.tkm.ms.consentmanager.model.request;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import lombok.*;

import javax.validation.constraints.*;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Consent {

    private ConsentEnum consent;

    @Size(min = 64, max = 64)
    private String hpan;

    private Set<ServiceEnum> services;

}
