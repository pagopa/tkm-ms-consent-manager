package it.gov.pagopa.tkm.ms.consentmanager.model.entity;

import lombok.*;

import javax.persistence.*;

@Data
@AllArgsConstructor
@Table(name = "TKM_SERVICE")
public class Service {

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @Column(name = "NAME", unique = true, nullable = false)
    private String name;

}
