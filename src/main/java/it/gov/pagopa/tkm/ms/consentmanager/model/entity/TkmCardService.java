package it.gov.pagopa.tkm.ms.consentmanager.model.entity;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import lombok.*;
import lombok.experimental.*;

import javax.persistence.*;

@Entity
@Table(name = "TKM_CARD_SERVICE")
@Data
@Accessors(chain = true)
public class TkmCardService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARD_ID", nullable = false)
    private TkmCard card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SERVICE_ID", nullable = false)
    private TkmService service;

    @Column(name = "CONSENT_TYPE", nullable = false)
    private ConsentEntityEnum consentType;

}
