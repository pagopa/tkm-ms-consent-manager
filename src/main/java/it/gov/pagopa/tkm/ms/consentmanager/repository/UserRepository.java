package it.gov.pagopa.tkm.ms.consentmanager.repository;

import it.gov.pagopa.tkm.ms.consentmanager.model.entity.*;
import org.springframework.data.jpa.repository.*;

public interface UserRepository extends JpaRepository<TkmUser, Long> {

    TkmUser findByTaxCode(String taxCode);

}
