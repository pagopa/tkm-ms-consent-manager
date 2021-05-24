package it.gov.pagopa.tkm.ms.consentmanager.repository;

import it.gov.pagopa.tkm.ms.consentmanager.model.entity.*;
import org.springframework.data.jpa.repository.*;

import java.util.List;

public interface CardRepository extends JpaRepository<TkmCard, Long> {

    TkmCard findByHpanAndCitizen(String hpan, TkmCitizen citizen);
    TkmCard findByHpan(String hpan);
    List<TkmCard> findByUser(TkmUser user);
    TkmCard findByHpanAndUser(String hpan, TkmUser user);

}
