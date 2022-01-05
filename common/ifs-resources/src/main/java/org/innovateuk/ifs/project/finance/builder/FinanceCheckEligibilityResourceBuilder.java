package org.innovateuk.ifs.project.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;


public class FinanceCheckEligibilityResourceBuilder extends BaseBuilder<FinanceCheckEligibilityResource, FinanceCheckEligibilityResourceBuilder> {
    private FinanceCheckEligibilityResourceBuilder(List<BiConsumer<Integer, FinanceCheckEligibilityResource>> multiActions) {
        super(multiActions);
    }

    public static FinanceCheckEligibilityResourceBuilder newFinanceCheckEligibilityResource() {
        return new FinanceCheckEligibilityResourceBuilder(emptyList());
    }

    @Override
    protected FinanceCheckEligibilityResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FinanceCheckEligibilityResource>> actions) {
        return new FinanceCheckEligibilityResourceBuilder(actions);
    }

    @Override
    protected FinanceCheckEligibilityResource createInitial() {
        return new FinanceCheckEligibilityResource();
    }

    public FinanceCheckEligibilityResourceBuilder withProjectId(Long... projectIds) {
        return withArray((projectId, financeCheckEligibilityResource) -> setField("projectId", projectId, financeCheckEligibilityResource), projectIds);
    }

    public FinanceCheckEligibilityResourceBuilder withProjectName(String... projectNames) {
        return withArray((projectName, financeCheckEligibilityResource) -> setField("projectName", projectName, financeCheckEligibilityResource), projectNames);
    }

    public FinanceCheckEligibilityResourceBuilder withOrganisationId(Long... organisationIds) {
        return withArray((organisationId, financeCheckEligibilityResource) -> setField("organisationId", organisationId, financeCheckEligibilityResource), organisationIds);
    }

    public FinanceCheckEligibilityResourceBuilder withOrganisationName(String... organisationNames) {
        return withArray((organisationName, financeCheckEligibilityResource) -> setField("organisationName", organisationName, financeCheckEligibilityResource), organisationNames);
    }

    public FinanceCheckEligibilityResourceBuilder withApplicationId(String... applicationIds) {
        return withArray((applicationId, financeCheckEligibilityResource) -> setField("applicationId", applicationId, financeCheckEligibilityResource), applicationIds);
    }

    public FinanceCheckEligibilityResourceBuilder withDurationInMonths(Long... durationInMonthsLst) {
        return withArray((durationInMonths, financeCheckEligibilityResource) -> setField("durationInMonths", durationInMonths, financeCheckEligibilityResource), durationInMonthsLst);
    }

    public FinanceCheckEligibilityResourceBuilder withTotalCost(BigDecimal... totalCosts) {
        return withArray((totalCost, financeCheckEligibilityResource) -> setField("totalCost", totalCost, financeCheckEligibilityResource), totalCosts);
    }

    public FinanceCheckEligibilityResourceBuilder withPercentageGrant(BigDecimal... percentageGrants) {
        return withArray((percentageGrant, financeCheckEligibilityResource) -> setField("percentageGrant", percentageGrant, financeCheckEligibilityResource), percentageGrants);
    }

    public FinanceCheckEligibilityResourceBuilder withFundingSought(BigDecimal... fundingSoughts) {
        return withArray((fundingSought, financeCheckEligibilityResource) -> setField("fundingSought", fundingSought, financeCheckEligibilityResource), fundingSoughts);
    }

    public FinanceCheckEligibilityResourceBuilder withOtherPublicSectorFunding(BigDecimal... otherPublicSectorFundings) {
        return withArray((otherPublicSectorFunding, financeCheckEligibilityResource) -> setField("otherPublicSectorFunding", otherPublicSectorFunding, financeCheckEligibilityResource), otherPublicSectorFundings);
    }

    public FinanceCheckEligibilityResourceBuilder withContributionToProject(BigDecimal... contributionToProjects) {
        return withArray((contributionToProject, financeCheckEligibilityResource) -> setField("contributionToProject", contributionToProject, financeCheckEligibilityResource), contributionToProjects);
    }

}

