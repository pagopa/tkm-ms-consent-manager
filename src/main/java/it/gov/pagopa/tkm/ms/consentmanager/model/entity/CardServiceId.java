package it.gov.pagopa.tkm.ms.consentmanager.model.entity;

import lombok.*;

import java.io.*;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CardServiceId implements Serializable {

    private Long card;

    private Long service;

}
