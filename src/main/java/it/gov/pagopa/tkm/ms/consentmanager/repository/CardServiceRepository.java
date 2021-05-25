package it.gov.pagopa.tkm.ms.consentmanager.repository;

import it.gov.pagopa.tkm.ms.consentmanager.constant.ServiceEnum;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardServiceRepository extends JpaRepository<TkmCardService, Long> {
    List<TkmCardService> findByCard(TkmCard card);
    List<TkmCardService> findByServiceInAndCard(List<TkmService> services, TkmCard card);

    @Query("select p from TkmCardService tkmCs " +
            "join tkmCs.card card " +
            "join tkmCs.service service " +
            "join card.citizen citizen" +
            "where citizen = :citizen " +
            "and card.hpan = :hpan or :hpan is null " +
            "and service.name in :services or :services is null "+
            "and tkmCs.consentType = 'Allow'")
    List<TkmCardService> findTkmCardServices(@Param("citizen") TkmCitizen citizen,
                                             @Param("hpan") String hpan,
                                             @Param("services") List<ServiceEnum> services);






}
