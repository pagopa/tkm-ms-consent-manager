package it.gov.pagopa.tkm.ms.consentmanager.repository;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.*;
import org.springframework.cache.annotation.*;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface ServiceRepository extends JpaRepository<TkmService, Long> {

    @Cacheable(value = "services", unless = "#result == null")
    List<TkmService> findByNameIn(Set<ServiceEnum> names);

}
