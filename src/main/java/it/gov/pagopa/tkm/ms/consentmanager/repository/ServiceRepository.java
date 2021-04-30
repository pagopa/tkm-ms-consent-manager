package it.gov.pagopa.tkm.ms.consentmanager.repository;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.*;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface ServiceRepository extends JpaRepository<TkmService, Long> {

    TkmService findByName(ServiceEnum name);

    List<TkmService> findAllByNameIn(List<ServiceEnum> names);

}
