package it.gov.pagopa.tkm.ms.consentmanager.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardServiceConsent {

    private String hpan;

    private Set<ServiceConsent> serviceConsents;

}
