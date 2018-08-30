package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class GrantClaimMaximumResourceBuilder extends BaseBuilder<GrantClaimMaximumResource, GrantClaimMaximumResourceBuilder> {

    private GrantClaimMaximumResourceBuilder(List<BiConsumer<Integer, GrantClaimMaximumResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static GrantClaimMaximumResourceBuilder newGrantClaimMaximumResource() {
        return new GrantClaimMaximumResourceBuilder(emptyList()).with(uniqueIds());
    }

    public GrantClaimMaximumResourceBuilder withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public GrantClaimMaximumResourceBuilder withResearchCategory(ResearchCategoryResource... researchCategories) {
        return withArraySetFieldByReflection("researchCategory", researchCategories);
    }

    public GrantClaimMaximumResourceBuilder withOrganisationType(OrganisationTypeResource... organisationTypes) {
        return withArraySetFieldByReflection("organisationType", organisationTypes);
    }

    public GrantClaimMaximumResourceBuilder withOrganisationSize(OrganisationSize... organisationSizes) {
        return withArraySetFieldByReflection("organisationSize", organisationSizes);
    }

    public GrantClaimMaximumResourceBuilder withOrganisationSize(List<CompetitionResource>... competitions) {
        return withArraySetFieldByReflection("competitions", competitions);
    }

    public GrantClaimMaximumResourceBuilder withMaximum(Integer... maximums) {
        return withArraySetFieldByReflection("maximum", maximums);
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
