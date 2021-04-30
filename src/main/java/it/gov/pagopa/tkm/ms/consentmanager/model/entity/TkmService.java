package it.gov.pagopa.tkm.ms.consentmanager.model.entity;

import lombok.*;
import lombok.experimental.*;

import javax.persistence.*;

@Entity
@Table(name = "TKM_SERVICE")
@Data
@Accessors(chain = true)
public class TkmService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @Column(name = "NAME", unique = true, nullable = false, length = 20)
    private String name;

    @Column(name = "DESCRIPTION", length = 50)
    private String description;

}
