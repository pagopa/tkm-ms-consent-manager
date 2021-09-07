package it.gov.pagopa.tkm.ms.consentmanager.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.gov.pagopa.tkm.constant.*;
import it.gov.pagopa.tkm.ms.consentmanager.constant.ConsentEntityEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TkmDatetimeConstant.DATE_TIME_PATTERN, timezone = TkmDatetimeConstant.DATE_TIME_TIMEZONE)
    private Instant lastUpdateDate;

    private Set<CardServiceConsent> details;

}
