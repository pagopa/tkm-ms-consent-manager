package it.gov.pagopa.tkm.ms.consentmanager.model.entity;

import lombok.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Data
@AllArgsConstructor
@Table(name = "CARD")
public class Card {

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER", nullable = false)
    private User user;

    @Column(name = "HPAN", nullable = false, length = 64)
    private String hpan;

    @Column(name = "DELETED")
    private Boolean deleted;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name = "CARD_SERVICE",
            joinColumns = @JoinColumn(name = "ID_CARD"),
            inverseJoinColumns = @JoinColumn(name = "ID_SERVICE"))
    private Set<Service> services;

}
