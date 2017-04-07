package org.innovateuk.ifs.project.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckOverviewResource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

public class FinanceCheckOverviewResourceBuilder extends BaseBuilder<FinanceCheckOverviewResource, FinanceCheckOverviewResourceBuilder> {

    private FinanceCheckOverviewResourceBuilder(List<BiConsumer<Integer, FinanceCheckOverviewResource>> multiActions) {
        super(multiActions);
    }

    public static FinanceCheckOverviewResourceBuilder newFinanceCheckOverviewResource() {
        return new FinanceCheckOverviewResourceBuilder(emptyList());
    }

    @Override
    protected FinanceCheckOverviewResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FinanceCheckOverviewResource>> actions) {
        return new FinanceCheckOverviewResourceBuilder(actions);
    }

    @Override
    protected FinanceCheckOverviewResource createInitial() {
        return new FinanceCheckOverviewResource();
    }

    public FinanceCheckOverviewResourceBuilder withProjectId(Long... projectIds) {
        return withArray((projectId, FinanceCheckOverviewResource) -> setField("projectId", projectId, FinanceCheckOverviewResource), projectIds);
    }

    public FinanceCheckOverviewResourceBuilder withProjectName(String... projectNames) {
        return withArray((projectName, financeCheckOverviewResource) -> setField("projectName", projectName, financeCheckOverviewResource), projectNames);
    }

    public FinanceCheckOverviewResourceBuilder withProjectStartDate(LocalDate... projectStartDates) {
        return withArray((projectStartDate, financeCheckOverviewResource) -> setField("projectStartDate", projectStartDate, financeCheckOverviewResource), projectStartDates);
    }

    public FinanceCheckOverviewResourceBuilder withDurationInMonths(Integer... durationInMonthsLst) {
        return withArray((durationInMonths, financeCheckOverviewResource) -> setField("durationInMonths", durationInMonths, financeCheckOverviewResource), durationInMonthsLst);
    }

    public FinanceCheckOverviewResourceBuilder withTotalProjectCost(BigDecimal... totalProjectCosts) {
        return withArray((totalProjectCost, financeCheckOverviewResource) -> setField("totalProjectCost", totalProjectCost, financeCheckOverviewResource), totalProjectCosts);
    }

    public FinanceCheckOverviewResourceBuilder withGrantAppliedFor(BigDecimal... grantAppliedFors) {
        return withArray((grantAppliedFor, financeCheckOverviewResource) -> setField("grantAppliedFor", grantAppliedFor, financeCheckOverviewResource), grantAppliedFors);
    }

    public FinanceCheckOverviewResourceBuilder withOtherPublicSectorFunding(BigDecimal... otherPublicSectorFundings) {
        return withArray((otherPublicSectorFunding, financeCheckOverviewResource) -> setField("otherPublicSectorFunding", otherPublicSectorFunding, financeCheckOverviewResource), otherPublicSectorFundings);
    }

    public FinanceCheckOverviewResourceBuilder withTotalPercentageGrants(BigDecimal... totalPercentageGrants) {
        return withArray((totalPercentageGrant, financeCheckOverviewResource) -> setField("totalPercentageGrant", totalPercentageGrant, financeCheckOverviewResource), totalPercentageGrants);
    }

}