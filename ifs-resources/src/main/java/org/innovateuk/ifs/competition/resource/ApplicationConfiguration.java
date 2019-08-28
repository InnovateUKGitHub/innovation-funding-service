package org.innovateuk.ifs.competition.resource;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;

import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.LOAN;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.BUSINESS;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.RESEARCH;

public interface ApplicationConfiguration {

    boolean isFullyFunded();

    Boolean getIncludeJesForm();

    default boolean isMaximumFundingLevelConstant(OrganisationTypeEnum organisationType, boolean maximumFundingLevelOverridden) {
        return LOAN.equals(getFundingType()) ||
                isFullyFunded() ||
                !BUSINESS.equals(organisationType) ||
                maximumFundingLevelOverridden;
    }

    FundingType getFundingType();

    default boolean applicantShouldUseJesFinances(OrganisationTypeEnum organisationType) {
        return Boolean.TRUE.equals(getIncludeJesForm()) && RESEARCH.equals(organisationType);
    }
}
