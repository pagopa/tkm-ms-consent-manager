package it.gov.pagopa.tkm.ms.consentmanager.model.entity;

import lombok.*;

import javax.persistence.*;

@Data
@AllArgsConstructor
@Table(name = "TKM_INSTRUMENT")
public class Instrument {

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @Column(name = "USER")
    private User user;

    @Column(name = "HPAN")
    private String hpan;

    @Column(name = "IS_ACTIVE")
    private Boolean active;

}
