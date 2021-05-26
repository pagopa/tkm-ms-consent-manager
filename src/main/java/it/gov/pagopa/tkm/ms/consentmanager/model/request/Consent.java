package it.gov.pagopa.tkm.ms.consentmanager.model.request;

import com.fasterxml.jackson.databind.annotation.*;
import it.gov.pagopa.tkm.jsondeserializer.*;
import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import lombok.*;
import lombok.experimental.*;

import javax.validation.constraints.*;
import java.util.*;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Consent {

    @NotNull
    private ConsentRequestEnum consent;

    @Size(min = 64, max = 64)
    @JsonDeserialize(using = ToLowerCaseDeserializer.class)
    private String hpan;

    private Set<ServiceEnum> services;

    public boolean isPartial() {
        return hpan != null;
    }

}
