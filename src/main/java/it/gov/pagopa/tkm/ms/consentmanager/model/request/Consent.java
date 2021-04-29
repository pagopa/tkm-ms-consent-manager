package it.gov.pagopa.tkm.ms.consentmanager.model.request;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import lombok.*;

import javax.validation.constraints.*;
import java.util.*;

@Data
@AllArgsConstructor
public class Consent {

    private ConsentEnum consent;

    @Size(min = 16, max = 16)
    private String hpan;

    private List<ServiceEnum> services;

}
