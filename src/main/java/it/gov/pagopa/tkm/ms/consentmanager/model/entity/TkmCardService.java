package it.gov.pagopa.tkm.ms.consentmanager.model.entity;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import lombok.*;
import lombok.experimental.*;

import javax.persistence.*;

@Entity
@Table(name = "CARD_SERVICE")
@IdClass(CardServiceId.class)
@Data
@Accessors(chain = true)
public class TkmCardService {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARD_ID", nullable = false)
    private TkmCard card;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SERVICE_ID", nullable = false)
    private TkmService service;

    @Enumerated(EnumType.STRING)
    @Column(name = "CONSENT_TYPE", nullable = false)
    private ConsentRequestEnum consentType;

}
