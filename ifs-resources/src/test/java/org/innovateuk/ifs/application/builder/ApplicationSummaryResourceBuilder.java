package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.FundingDecision;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ApplicationSummaryResourceBuilder extends BaseBuilder<ApplicationSummaryResource, ApplicationSummaryResourceBuilder> {

    private ApplicationSummaryResourceBuilder(List<BiConsumer<Integer, ApplicationSummaryResource>> multiActions) {
        super(multiActions);
    }

    public static ApplicationSummaryResourceBuilder newApplicationSummaryResource() {
        return new ApplicationSummaryResourceBuilder(emptyList());
    }

    @Override
    protected ApplicationSummaryResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationSummaryResource>> actions) {
        return new ApplicationSummaryResourceBuilder(actions);
    }

    @Override
    protected ApplicationSummaryResource createInitial() {
        return new ApplicationSummaryResource();
    }

    public ApplicationSummaryResourceBuilder withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public ApplicationSummaryResourceBuilder withName(String... name) {
        return withArraySetFieldByReflection("name", name);
    }

    public ApplicationSummaryResourceBuilder withLead(String... lead) {
        return withArraySetFieldByReflection("lead", lead);
    }

    public ApplicationSummaryResourceBuilder withLeadApplicant(String... leadApplicant) {
        return withArraySetFieldByReflection("leadApplicant", leadApplicant);
    }

    public ApplicationSummaryResourceBuilder withManageFundingEmailDate(ZonedDateTime... manageFundingEmailDate) {
        return withArraySetFieldByReflection("manageFundingEmailDate", manageFundingEmailDate);
    }

    public ApplicationSummaryResourceBuilder withStatus(String... status) {
        return withArraySetFieldByReflection("status", status);
    }

    public ApplicationSummaryResourceBuilder withCompletedPercentage(Integer... completedPercentage) {
        return withArraySetFieldByReflection("completedPercentage", completedPercentage);
    }

    public ApplicationSummaryResourceBuilder withNumberOfPartners(Integer... numberOfPartners) {
        return withArraySetFieldByReflection("numberOfPartners", numberOfPartners);
    }

    public ApplicationSummaryResourceBuilder withGrantRequested(BigDecimal... grantRequested) {
        return withArraySetFieldByReflection("grantRequested", grantRequested);
    }

    public ApplicationSummaryResourceBuilder withTotalProjectCost(BigDecimal... totalProjectCost) {
        return withArraySetFieldByReflection("totalProjectCost", totalProjectCost);
    }

    public ApplicationSummaryResourceBuilder withDuration(Long... duration) {
        return withArraySetFieldByReflection("duration", duration);
    }

    public ApplicationSummaryResourceBuilder withFundingDecision(FundingDecision... fundingDecision) {
        return withArraySetFieldByReflection("fundingDecision", fundingDecision);
    }

    public ApplicationSummaryResourceBuilder withInnovationArea(String... innovationArea) {
        return withArraySetFieldByReflection("innovationArea", innovationArea);
    }
}
