package org.innovateuk.ifs.finance.domain;

import org.hibernate.annotations.Immutable;
import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.organisation.domain.OrganisationType;

import javax.persistence.*;
import java.util.List;

/**
 * Reference data that describes the maximum funding levels that can be applied for.
 */
@Entity
@Immutable
public class GrantClaimMaximum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY,
            mappedBy = "grantClaimMaximums",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Competition> competitions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", referencedColumnName = "id")
    private ResearchCategory researchCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisationTypeId", referencedColumnName = "id")
    private OrganisationType organisationType;

    @Column(name = "organisation_size_id")
    private OrganisationSize organisationSize;

    private Integer maximum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Competition> getCompetitions() {
        return competitions;
    }

    public void setCompetitions(List<Competition> competitions) {
        this.competitions = competitions;
    }

    public ResearchCategory getResearchCategory() {
        return researchCategory;
    }

    public void setResearchCategory(ResearchCategory researchCategory) {
        this.researchCategory = researchCategory;
    }

    public OrganisationType getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(OrganisationType organisationType) {
        this.organisationType = organisationType;
    }

    public OrganisationSize getOrganisationSize() {
        return organisationSize;
    }

    public void setOrganisationSize(OrganisationSize organisationSize) {
        this.organisationSize = organisationSize;
    }

    public Integer getMaximum() {
        return maximum;
    }

    public void setMaximum(Integer maximum) {
        this.maximum = maximum;
    }
}
