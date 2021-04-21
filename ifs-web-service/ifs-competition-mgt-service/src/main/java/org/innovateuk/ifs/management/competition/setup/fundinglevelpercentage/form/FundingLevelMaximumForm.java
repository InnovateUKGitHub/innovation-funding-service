package org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.form;

import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;

public class FundingLevelMaximumForm extends CompetitionSetupForm {

    private Long grantClaimMaximumId;
    private Long categoryId;
    private OrganisationSize organisationSize;
    private FundingRules fundingRules;
    private Integer maximum;

    public FundingLevelMaximumForm() {}

    public FundingLevelMaximumForm(Long grantClaimMaximumId, Long categoryId, OrganisationSize organisationSize, FundingRules fundingRules, Integer maximum) {
        this.grantClaimMaximumId = grantClaimMaximumId;
        this.categoryId = categoryId;
        this.organisationSize = organisationSize;
        this.fundingRules = fundingRules;
        this.maximum = maximum;
    }

    public Long getGrantClaimMaximumId() {
        return grantClaimMaximumId;
    }

    public void setGrantClaimMaximumId(Long grantClaimMaximumId) {
        this.grantClaimMaximumId = grantClaimMaximumId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public OrganisationSize getOrganisationSize() {
        return organisationSize;
    }

    public void setOrganisationSize(OrganisationSize organisationSize) {
        this.organisationSize = organisationSize;
    }

    public FundingRules getFundingRules() {
        return fundingRules;
    }

    public void setFundingRules(FundingRules fundingRules) {
        this.fundingRules = fundingRules;
    }

    public Integer getMaximum() {
        return maximum;
    }

    public void setMaximum(Integer maximum) {
        this.maximum = maximum;
    }

    public static FundingLevelMaximumForm fromGrantClaimMaximumResource(GrantClaimMaximumResource grantClaimMaximum) {
        return new FundingLevelMaximumForm(grantClaimMaximum.getId(), grantClaimMaximum.getResearchCategory().getId(), grantClaimMaximum.getOrganisationSize(), grantClaimMaximum.getFundingRules(), grantClaimMaximum.getMaximum());
    }

    public static FundingLevelMaximumForm singleValueForm(Integer maximum, FundingRules fundingRules) {
        return new FundingLevelMaximumForm(null, null, null, fundingRules, maximum);
    }
}
