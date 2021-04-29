package it.gov.pagopa.tkm.ms.consentmanager.model.entity;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import lombok.*;

import javax.persistence.*;
import java.util.*;

@Data
@AllArgsConstructor
@Table(name = "TKM_INSTRUMENT")
public class User {

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @Column(name = "TAX_CODE", unique = true, nullable = false)
    private String taxCode;

    @Column(name = "CONSENT_TYPE")
    private ConsentEnum consentType;

    @Column(name = "CONSENT_DATE")
    private Date consentDate;

    @Column(name = "CONSENT_UPDATE_DATE")
    private Date consentUpdateDate;

    @Column(name = "LAST_CLIENT")
    private ClientEnum lastClient;

}
