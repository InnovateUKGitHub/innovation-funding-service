package org.innovateuk.ifs.project.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckPartnerStatusResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckSummaryResource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

public class FinanceCheckSummaryResourceBuilder extends BaseBuilder<FinanceCheckSummaryResource, FinanceCheckSummaryResourceBuilder> {

    private FinanceCheckSummaryResourceBuilder(List<BiConsumer<Integer, FinanceCheckSummaryResource>> multiActions) {
        super(multiActions);
    }

    public static FinanceCheckSummaryResourceBuilder newFinanceCheckSummaryResource() {
        return new FinanceCheckSummaryResourceBuilder(emptyList());
    }

    @Override
    protected FinanceCheckSummaryResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FinanceCheckSummaryResource>> actions) {
        return new FinanceCheckSummaryResourceBuilder(actions);
    }

    @Override
    protected FinanceCheckSummaryResource createInitial() {
        return new FinanceCheckSummaryResource();
    }

    public FinanceCheckSummaryResourceBuilder withProjectId(Long... projectIds) {
        return withArray((projectId, financeCheckResource) -> setField("projectId", projectId, financeCheckResource), projectIds);
    }

    public FinanceCheckSummaryResourceBuilder withCompetitionId(Long... competitionIds) {
        return withArray((competitionId, financeCheckResource) -> setField("competitionId", competitionId, financeCheckResource), competitionIds);
    }


    public FinanceCheckSummaryResourceBuilder withCompetitionName(String... competitionNames) {
        return withArray((competitionName, financeCheckResource) -> setField("competitionName", competitionName, financeCheckResource), competitionNames);
    }

    public FinanceCheckSummaryResourceBuilder withProjectStartDate(LocalDate... projectStartDates) {
        return withArray((projectStartDate, financeCheckResource) -> setField("projectStartDate", projectStartDate, financeCheckResource), projectStartDates);
    }


    public FinanceCheckSummaryResourceBuilder withDurationInMonths(Integer... durationInMonthsLst) {
        return withArray((durationInMonths, financeCheckResource) -> setField("durationInMonths", durationInMonths, financeCheckResource), durationInMonthsLst);
    }


    public FinanceCheckSummaryResourceBuilder withTotalProjectCost(BigDecimal... totalProjectCosts) {
        return withArray((totalProjectCost, financeCheckResource) -> setField("totalProjectCost", totalProjectCost, financeCheckResource), totalProjectCosts);
    }


    public FinanceCheckSummaryResourceBuilder withGrantAppliedFor(BigDecimal... grantAppliedForLst) {
        return withArray((grantAppliedFor, financeCheckResource) -> setField("grantAppliedFor", grantAppliedFor, financeCheckResource), grantAppliedForLst);
    }


    public FinanceCheckSummaryResourceBuilder withOtherPublicSectorFunding(BigDecimal... otherPublicSectorFundings) {
        return withArray((otherPublicSectorFunding, financeCheckResource) -> setField("otherPublicSectorFunding", otherPublicSectorFunding, financeCheckResource), otherPublicSectorFundings);
    }


    public FinanceCheckSummaryResourceBuilder withTotalPercentageGrant(BigDecimal... totalPercentageGrants) {
        return withArray((totalPercentageGrant, financeCheckResource) -> setField("totalPercentageGrant", totalPercentageGrant, financeCheckResource), totalPercentageGrants);
    }


    public FinanceCheckSummaryResourceBuilder withSpendProfilesGenerated(Boolean... spendProfilesGeneratedLst) {
        return withArray((spendProfilesGenerated, financeCheckResource) -> setField("spendProfilesGenerated", spendProfilesGenerated, financeCheckResource), spendProfilesGeneratedLst);
    }


    public FinanceCheckSummaryResourceBuilder withBankDetailslApproved(Boolean... bankDetailslApprovedLst) {
        return withArray((bankDetailslApproved, financeCheckResource) -> setField("bankDetailsApproved", bankDetailslApproved, financeCheckResource), bankDetailslApprovedLst);
    }


    public FinanceCheckSummaryResourceBuilder withSpendProfileGeneratedBy(String... spendProfileGeneratedByLst) {
        return withArray((spendProfileGeneratedBy, financeCheckResource) -> setField("spendProfileGeneratedBy", spendProfileGeneratedBy, financeCheckResource), spendProfileGeneratedByLst);
    }

    public FinanceCheckSummaryResourceBuilder withSpendProfileGeneratedDate(LocalDate... spendProfileGeneratedDates) {
        return withArray((spendProfileGeneratedDate, financeCheckResource) -> setField("spendProfileGeneratedDate", spendProfileGeneratedDate, financeCheckResource), spendProfileGeneratedDates);
    }

    public FinanceCheckSummaryResourceBuilder withH2020(Boolean... h2020Statuses) {
        return withArray((h2020Status, financeCheckResource) -> setField("h2020", h2020Status, financeCheckResource), h2020Statuses);
    }

    public FinanceCheckSummaryResourceBuilder withFundingType(FundingType... fundingTypes) {
        return withArray((fundingType, financeCheckResource) -> financeCheckResource.setFundingType(fundingType), fundingTypes);
    }

    @SafeVarargs
    public final FinanceCheckSummaryResourceBuilder withPartnerStatusResources(List<FinanceCheckPartnerStatusResource>... partnerStatusResourcesLst) {
        return withArray((competitionId, financeCheckResource) -> setField("partnerStatusResources", competitionId, financeCheckResource), partnerStatusResourcesLst);
    }
}