package org.innovateuk.ifs.heukar.domain;

import org.innovateuk.ifs.heukar.resource.HeukarPartnerOrganisationTypeEnum;

import javax.persistence.*;

@Entity
@Table(name = "heukar_partner_organisation")
public class HeukarPartnerOrganisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "application_id")
    private Long applicationId;

    @Enumerated(EnumType.STRING)
    private HeukarPartnerOrganisationTypeEnum organisationType;

    public HeukarPartnerOrganisation() {
        // no arg constructor
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public HeukarPartnerOrganisationTypeEnum getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(HeukarPartnerOrganisationTypeEnum organisationType) {
        this.organisationType = organisationType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
