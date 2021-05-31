package it.gov.pagopa.tkm.ms.consentmanager.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.gov.pagopa.tkm.ms.consentmanager.constant.ConsentEntityEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConsentResponse {
    private ConsentEntityEnum consent;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Europe/Rome")
    private Instant lastUpdateDate;

    private Set<CardServiceConsent> details;

}
