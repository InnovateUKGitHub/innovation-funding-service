package org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.form;

import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;

public class FundingLevelMaximumForm extends CompetitionSetupForm {

    private Long grantClaimMaximumId;
    private Long categoryId;
    private OrganisationSize organisationSize;
    private Integer maximum;

    public FundingLevelMaximumForm() {}

    public FundingLevelMaximumForm(Long grantClaimMaximumId, Long categoryId, OrganisationSize organisationSize, Integer maximum) {
        this.grantClaimMaximumId = grantClaimMaximumId;
        this.categoryId = categoryId;
        this.organisationSize = organisationSize;
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

    public Integer getMaximum() {
        return maximum;
    }

    public void setMaximum(Integer maximum) {
        this.maximum = maximum;
    }

    public static FundingLevelMaximumForm fromGrantClaimMaximumResource(GrantClaimMaximumResource grantClaimMaximum) {
        return new FundingLevelMaximumForm(grantClaimMaximum.getId(), grantClaimMaximum.getResearchCategory().getId(), grantClaimMaximum.getOrganisationSize(), grantClaimMaximum.getMaximum());
    }

    public static FundingLevelMaximumForm singleValueForm(Integer maximum) {
        return new FundingLevelMaximumForm(null, null, null, maximum);
    }
}
