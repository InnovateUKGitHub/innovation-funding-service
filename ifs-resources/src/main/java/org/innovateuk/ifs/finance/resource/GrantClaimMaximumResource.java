package org.innovateuk.ifs.finance.resource;

import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;

import java.util.List;

public class GrantClaimMaximumResource {

    private Long id;

    private ResearchCategoryResource researchCategory;

    private OrganisationTypeResource organisationType;

    private OrganisationSize organisationSize;

    private List<CompetitionResource> competitions;

    private Integer maximum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ResearchCategoryResource getResearchCategory() {
        return researchCategory;
    }

    public void setResearchCategory(ResearchCategoryResource researchCategory) {
        this.researchCategory = researchCategory;
    }

    public OrganisationTypeResource getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(OrganisationTypeResource organisationType) {
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

    public List<CompetitionResource> getCompetitions() {
        return competitions;
    }

    public void setCompetitions(List<CompetitionResource> competitions) {
        this.competitions = competitions;
    }
}
