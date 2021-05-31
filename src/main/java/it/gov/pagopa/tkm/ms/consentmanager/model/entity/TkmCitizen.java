package it.gov.pagopa.tkm.ms.consentmanager.model.entity;

import it.gov.pagopa.tkm.ms.consentmanager.constant.ConsentEntityEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "CITIZEN")
@Data
@EqualsAndHashCode(exclude = "cards")
@ToString(exclude = "cards")
@Accessors(chain = true)
public class TkmCitizen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @Column(name = "TAX_CODE", nullable = false, length = 16)
    private String taxCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "CONSENT_TYPE", nullable = false)
    private ConsentEntityEnum consentType;

    @Column(name = "CONSENT_DATE", nullable = false)
    private Instant consentDate;

    @Column(name = "CONSENT_UPDATE_DATE")
    private Instant consentUpdateDate;

    @Column(name = "CONSENT_CLIENT", nullable = false)
    private String consentClient;

    @Column(name = "CONSENT_UPDATE_CLIENT")
    private String consentUpdateClient;

    @Column(name = "DELETED")
    private boolean deleted;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "citizen")
    @Where(clause = "deleted = false")
    private Set<TkmCard> cards = new HashSet<>();

    public Instant getLastConsentUpdatetDate() {
        return ObjectUtils.firstNonNull(consentUpdateDate, consentDate);

    }

}
