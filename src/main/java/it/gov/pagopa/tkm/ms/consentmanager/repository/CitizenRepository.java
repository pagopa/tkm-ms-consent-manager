package it.gov.pagopa.tkm.ms.consentmanager.repository;

import it.gov.pagopa.tkm.ms.consentmanager.model.entity.*;
import org.springframework.data.jpa.repository.*;

public interface CitizenRepository extends JpaRepository<TkmCitizen, Long> {

    TkmCitizen findByTaxCodeAndDeletedFalse(String taxCode);

}
