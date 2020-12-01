package org.innovateuk.ifs.heukar.domain;

import org.innovateuk.ifs.organisation.domain.OrganisationType;

import javax.persistence.*;

@Entity
@Table(name = "heukar_org_type")
public class HeukarOrganisationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "application_id")
    private Long applicationId;

    @ManyToOne()
    @JoinColumn(name="organisation_type_id", referencedColumnName="id")
    private OrganisationType organisationType;

    public HeukarOrganisationType() {
        // no arg constructor
    }

    public Long getApplicationID() {
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
}
