package org.innovateuk.ifs.finance.resource;

import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;

import java.util.List;

public class GrantClaimMaximumResource {

    private Long id;

    private ResearchCategoryResource researchCategory;

    private OrganisationSize organisationSize;

    private List<Long> competitions;

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

    @ZeroDowntime(reference = "IFS-4271", description = "Retaining this method to support old REST clients. Returning" +
            " the Business type here which represents all rows")
    public OrganisationTypeResource getOrganisationType() {
        OrganisationTypeResource organisationType = new OrganisationTypeResource();
        organisationType.setId(OrganisationTypeEnum.BUSINESS.getId());
        organisationType.setName("Business");
        organisationType.setDescription("UK based business.");
        organisationType.setVisibleInSetup(true);
        return organisationType;
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

    public List<Long> getCompetitions() {
        return competitions;
    }

    public void setCompetitions(List<Long> competitions) {
        this.competitions = competitions;
    }

    @ZeroDowntime(reference = "IFS-4160", description = "Retaining this method to support old REST clients. Returning" +
            " null here since the value is no longer used")
    public Long getCompetitionType() {
        return null;
    }
}
