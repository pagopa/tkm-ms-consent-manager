package it.gov.pagopa.tkm.ms.consentmanager.model.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@Table(name = "SERVICE")
public class Service {

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @Column(name = "NAME", unique = true, nullable = false, length = 20)
    private String name;

    @Column(name = "DESCRIPTION", length = 50)
    private String description;

}
