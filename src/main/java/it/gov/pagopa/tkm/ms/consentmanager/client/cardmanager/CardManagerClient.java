package it.gov.pagopa.tkm.ms.consentmanager.client.cardmanager;

import it.gov.pagopa.tkm.ms.consentmanager.model.response.*;
import org.springframework.cloud.openfeign.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "card-manager", url = "${client-urls.card-manager}")
public interface CardManagerClient {

    @PutMapping("/consent-update")
    @ResponseStatus(HttpStatus.OK)
    void updateConsent(@RequestBody ConsentResponse consentResponse);

}