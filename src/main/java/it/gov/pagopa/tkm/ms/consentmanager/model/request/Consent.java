package it.gov.pagopa.tkm.ms.consentmanager.model.request;

import it.gov.pagopa.tkm.ms.consentmanager.constant.ConsentRequestEnum;
import it.gov.pagopa.tkm.ms.consentmanager.constant.ServiceEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consent {

    @NotNull
    private ConsentRequestEnum consent;

    @Size(min = 64, max = 64)
    private String hpan;

    private Set<ServiceEnum> services;

    public boolean isPartial() {
        return false;
    }

}
