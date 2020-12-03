package org.innovateuk.ifs.finance.domain;

import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.resource.OrganisationSize;

import javax.persistence.*;
import java.util.List;

/**
 * Reference data that describes the maximum funding levels that can be applied for.
 */
@Entity
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

    @Column(name = "organisation_size_id")
    private OrganisationSize organisationSize;

    private Integer maximum;

    public GrantClaimMaximum() {}

    public GrantClaimMaximum(ResearchCategory researchCategory, OrganisationSize organisationSize, Integer maximum) {
        this.researchCategory = researchCategory;
        this.organisationSize = organisationSize;
        this.maximum = maximum;
    }

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
