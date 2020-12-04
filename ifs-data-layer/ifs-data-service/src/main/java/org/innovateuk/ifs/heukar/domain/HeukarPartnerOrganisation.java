package org.innovateuk.ifs.heukar.domain;

import org.innovateuk.ifs.organisation.domain.OrganisationType;

import javax.persistence.*;

@Entity
@Table(name = "heukar_partner_organisation")
public class HeukarPartnerOrganisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "application_id")
    private Long applicationId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "organisation_type_id", referencedColumnName = "id")
    private OrganisationType organisationType;

    public HeukarPartnerOrganisation() {
        // no arg constructor
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public OrganisationType getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(OrganisationType organisationType) {
        this.organisationType = organisationType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
