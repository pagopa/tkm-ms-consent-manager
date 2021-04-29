package it.gov.pagopa.tkm.ms.consentmanager.model.response;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import lombok.*;

import java.util.*;

@Data
@AllArgsConstructor
public class ConsentResponse {

    private ConsentEnum consent;

    private String hpan;

    private List<ServiceEnum> services;

}
