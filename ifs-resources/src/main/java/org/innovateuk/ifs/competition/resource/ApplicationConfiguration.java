package org.innovateuk.ifs.competition.resource;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;

import java.util.function.Supplier;

import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.GRANT;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.LOAN;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.*;

/**
 * Interface to be shared between the Competition and CompetitionResource to declare methods that define the configuration
 * of an application form.
 */
public interface ApplicationConfiguration {

    String SBRI_PILOT = "The Sustainable Innovation Fund: SBRI phase 1";

    boolean isFullyFunded();

    boolean isH2020();

    boolean isKtp();

    boolean isExpressionOfInterest();

    Boolean getIncludeJesForm();

    Boolean getIncludeYourOrganisationSection();

    boolean isSbriPilot();

    default boolean isMaximumFundingLevelConstant(Supplier<OrganisationTypeEnum> organisationType, Supplier<Boolean> dbMaximumFundingLevelConstant) {
        return LOAN == getFundingType() ||
                isFullyFunded() ||
                BUSINESS != organisationType.get() ||
                dbMaximumFundingLevelConstant.get();
    }

    FundingType getFundingType();

    default boolean applicantShouldUseJesFinances(OrganisationTypeEnum organisationType) {
        return Boolean.TRUE.equals(getIncludeJesForm())
                && getFundingType() == GRANT
                && RESEARCH == organisationType;
    }

    default boolean applicantShouldSeeYourOrganisationSection(OrganisationTypeEnum organisationType) {
        return RESEARCH != organisationType ||
                Boolean.TRUE.equals(getIncludeYourOrganisationSection())
                        && getFundingType() == GRANT;
    }

    default boolean applicantNotRequiredForViabilityChecks(OrganisationTypeEnum organisationType) {
        return isH2020() || applicantShouldUseJesFinances(organisationType) ||
                (organisationType.equals(OrganisationTypeEnum.KNOWLEDGE_BASE) && this.isKtp());
    }

    default boolean applicantNotRequiredForEligibilityChecks(OrganisationTypeEnum organisationTypeEnum) {
        return !organisationTypeEnum.equals(KNOWLEDGE_BASE) && this.isKtp();
    }
}