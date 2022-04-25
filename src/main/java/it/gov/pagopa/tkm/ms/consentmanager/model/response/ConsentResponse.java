package it.gov.pagopa.tkm.ms.consentmanager.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.gov.pagopa.tkm.ms.consentmanager.constant.ConsentEntityEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConsentResponse {

    private ConsentEntityEnum consent;

    private String taxCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Europe/Rome")
    private Instant lastUpdateDate;

    private Set<CardServiceConsent> details;

}
