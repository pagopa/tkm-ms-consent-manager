package it.gov.pagopa.tkm.ms.consentmanager.repository;

import it.gov.pagopa.tkm.ms.consentmanager.constant.ServiceEnum;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardServiceRepository extends JpaRepository<TkmCardService, Long> {
    List<TkmCardService> findByCard(TkmCard card);
    List<TkmCardService> findByServiceInAndCard(List<TkmService> services, TkmCard card);

    @Query("select tkmCs from TkmCardService tkmCs" +
           " where tkmCs.consentType = 'Allow'" +
           " and tkmCs.card.citizen = :citizen" +
           " and (tkmCs.card.hpan = :hpan or :hpan is null)" +
           " and (tkmCs.service.name in :services or :services is null)")
    List<TkmCardService> findTkmCardServices(@Param("citizen")TkmCitizen citizen,
                                             @Param("hpan")String hpan,
                                             @Param("services")List<ServiceEnum> services);






}
