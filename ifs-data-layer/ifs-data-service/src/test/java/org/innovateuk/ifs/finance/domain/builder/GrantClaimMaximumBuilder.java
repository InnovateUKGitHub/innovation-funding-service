package org.innovateuk.ifs.finance.domain.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.GrantClaimMaximum;
import org.innovateuk.ifs.organisation.domain.OrganisationType;

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
    protected GrantClaimMaximumBuilder createNewBuilderWithActions(final List<BiConsumer<Integer,
            GrantClaimMaximum>> actions) {
        return new GrantClaimMaximumBuilder(actions);
    }

    @Override
    protected GrantClaimMaximum createInitial() {
        return new GrantClaimMaximum();
    }

    public GrantClaimMaximumBuilder withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public GrantClaimMaximumBuilder withCompetition(Competition... competition) {
        return withArraySetFieldByReflection("competition", competition);
    }

    public GrantClaimMaximumBuilder withResearchCategory(ResearchCategory... researchCategory) {
        return withArraySetFieldByReflection("researchCategory", researchCategory);
    }

    public GrantClaimMaximumBuilder withOrganisationType(OrganisationType... organisationType) {
        return withArraySetFieldByReflection("organisationType", organisationType);
    }

    public GrantClaimMaximumBuilder withDef(Integer... def) {
        return withArraySetFieldByReflection("def", def);
    }

    public GrantClaimMaximumBuilder withSmall(Integer... small) {
        return withArraySetFieldByReflection("small", small);
    }

    public GrantClaimMaximumBuilder withMedium(Integer... medium) {
        return withArraySetFieldByReflection("medium", medium);
    }

    public GrantClaimMaximumBuilder withLarge(Integer... large) {
        return withArraySetFieldByReflection("large", large);
    }
}
