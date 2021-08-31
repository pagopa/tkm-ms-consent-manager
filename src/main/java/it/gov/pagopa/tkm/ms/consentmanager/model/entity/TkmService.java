package it.gov.pagopa.tkm.ms.consentmanager.model.entity;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import lombok.*;
import lombok.Builder;

import javax.persistence.*;

@Entity
@Table(name = "SERVICE")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TkmService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "NAME", unique = true, nullable = false, length = 20)
    private ServiceEnum name;

    @Column(name = "DESCRIPTION", length = 50)
    private String description;

}
