package it.gov.pagopa.tkm.ms.consentmanager.model.entity;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import lombok.*;
import lombok.experimental.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "TKM_USER")
@Data
@Accessors(chain = true)
public class TkmUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @Column(name = "TAX_CODE", unique = true, nullable = false, length = 16)
    private String taxCode;

    @Column(name = "CONSENT_TYPE", nullable = false)
    private ConsentEnum consentType;

    @Column(name = "CONSENT_DATE", nullable = false)
    private Date consentDate;

    @Column(name = "CONSENT_UPDATE_DATE")
    private Date consentUpdateDate;

    @Column(name = "CONSENT_LAST_CLIENT")
    private ClientEnum consentLastClient;

}
