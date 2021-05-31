package it.gov.pagopa.tkm.ms.consentmanager.model.response;

import com.fasterxml.jackson.annotation.*;
import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.*;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ConsentResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ConsentEntityEnum consent;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<CardServiceConsent> cardServiceConsents;

}
