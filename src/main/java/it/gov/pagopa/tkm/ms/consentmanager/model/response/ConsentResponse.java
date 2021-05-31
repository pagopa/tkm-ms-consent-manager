package it.gov.pagopa.tkm.ms.consentmanager.model.response;

import com.fasterxml.jackson.annotation.*;
import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsentResponse {

    private ConsentEntityEnum consent;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<CardServiceConsent> cardServiceConsents;

}
