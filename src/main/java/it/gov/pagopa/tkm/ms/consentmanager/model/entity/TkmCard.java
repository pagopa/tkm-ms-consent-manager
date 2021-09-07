package it.gov.pagopa.tkm.ms.consentmanager.model.entity;

import lombok.*;
import lombok.Builder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CARD")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TkmCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CITIZEN_ID", nullable = false)
    private TkmCitizen citizen;

    @Column(name = "HPAN", nullable = false, length = 64)
    private String hpan;

    @Column(name = "DELETED")
    private boolean deleted;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "card")
    private List<TkmCardService> tkmCardServices = new ArrayList<>();


}
