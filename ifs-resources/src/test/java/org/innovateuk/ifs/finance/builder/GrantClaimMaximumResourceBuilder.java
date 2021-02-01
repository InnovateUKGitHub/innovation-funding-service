package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class GrantClaimMaximumResourceBuilder extends BaseBuilder<GrantClaimMaximumResource,
        GrantClaimMaximumResourceBuilder> {

    private GrantClaimMaximumResourceBuilder(List<BiConsumer<Integer, GrantClaimMaximumResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static GrantClaimMaximumResourceBuilder newGrantClaimMaximumResource() {
        return new GrantClaimMaximumResourceBuilder(emptyList()).with(uniqueIds());
    }

    public GrantClaimMaximumResourceBuilder withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public GrantClaimMaximumResourceBuilder withResearchCategory(ResearchCategoryResource... researchCategory) {
        return withArray((value, max) -> max.setResearchCategory(value), researchCategory);
    }

    public GrantClaimMaximumResourceBuilder withOrganisationSize(OrganisationSize... size) {
        return withArray((value, max) -> max.setOrganisationSize(value), size);
    }

    public GrantClaimMaximumResourceBuilder withMaximum(Integer... maximum) {
        return withArray((value, max) -> max.setMaximum(value), maximum);
    }

    public GrantClaimMaximumResourceBuilder withFundingRules(FundingRules... rules) {
        return withArray((value, max) -> max.setFundingRules(value), rules);
    }
    @Override
    protected GrantClaimMaximumResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, GrantClaimMaximumResource>> actions) {
        return new GrantClaimMaximumResourceBuilder(actions);
    }

    @Override
    protected GrantClaimMaximumResource createInitial() {
        return new GrantClaimMaximumResource();
    }
}
