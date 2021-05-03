package it.gov.pagopa.tkm.ms.consentmanager.repository;

import it.gov.pagopa.tkm.ms.consentmanager.model.entity.*;
import org.springframework.data.jpa.repository.*;

import java.util.List;

public interface CardRepository extends JpaRepository<TkmCard, Long> {

    TkmCard findByHpan(String hpan);
    List<TkmCard> findByUser(TkmUser user);

}