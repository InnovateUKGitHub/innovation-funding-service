package org.innovateuk.ifs.finance.domain.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.finance.domain.GrantClaimMaximum;
import org.innovateuk.ifs.finance.resource.OrganisationSize;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class GrantClaimMaximumBuilder extends BaseBuilder<GrantClaimMaximum, GrantClaimMaximumBuilder> {

    private GrantClaimMaximumBuilder(List<BiConsumer<Integer, GrantClaimMaximum>> multiActions) {
        super(multiActions);
    }

    public static GrantClaimMaximumBuilder newGrantClaimMaximum() {
        return new GrantClaimMaximumBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected GrantClaimMaximumBuilder createNewBuilderWithActions(List<BiConsumer<Integer, GrantClaimMaximum>>
                                                                           actions) {
        return new GrantClaimMaximumBuilder(actions);
    }

    @Override
    protected GrantClaimMaximum createInitial() {
        return new GrantClaimMaximum();
    }

    public GrantClaimMaximumBuilder withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public GrantClaimMaximumBuilder withCompetitions(List<Competition>... competitions) {
        return withArray((value, max) -> max.setCompetitions(value), competitions);
    }

    public GrantClaimMaximumBuilder withResearchCategory(ResearchCategory... researchCategory) {
        return withArray((value, max) -> max.setResearchCategory(value), researchCategory);
    }

    public GrantClaimMaximumBuilder withSize(OrganisationSize... size) {
        return withArray((value, max) -> max.setOrganisationSize(value), size);
    }

    public GrantClaimMaximumBuilder withMaximum(Integer... maximum) {
        return withArray((value, max) -> max.setMaximum(value), maximum);
    }

    public GrantClaimMaximumBuilder withFundingRules(FundingRules... rules) {
        return withArray((value, max) -> max.setFundingRules(value), rules);
    }
}
