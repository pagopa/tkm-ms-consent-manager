package it.gov.pagopa.tkm.ms.consentmanager.model.entity;

import lombok.*;
import lombok.experimental.*;

import javax.persistence.*;

@Entity
@Table(name = "TKM_CARD")
@Data
@Accessors(chain = true)
public class TkmCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private TkmUser user;

    @Column(name = "HPAN", nullable = false, length = 64)
    private String hpan;

    @Column(name = "DELETED")
    private boolean deleted;

}
