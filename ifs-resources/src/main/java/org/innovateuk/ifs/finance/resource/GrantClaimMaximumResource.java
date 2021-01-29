package org.innovateuk.ifs.finance.resource;

import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.competition.resource.FundingRules;

public class GrantClaimMaximumResource {

    private Long id;

    private ResearchCategoryResource researchCategory;

    private OrganisationSize organisationSize;

    private Integer maximum;

    private FundingRules fundingRules;

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

    public FundingRules getFundingRules() {
        return fundingRules;
    }

    public void setFundingRules(FundingRules fundingRules) {
        this.fundingRules = fundingRules;
    }
}
