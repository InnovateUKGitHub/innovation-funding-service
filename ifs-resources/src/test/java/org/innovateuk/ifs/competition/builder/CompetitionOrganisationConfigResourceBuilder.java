package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class CompetitionOrganisationConfigResourceBuilder extends BaseBuilder<CompetitionOrganisationConfigResource, CompetitionOrganisationConfigResourceBuilder> {

    private CompetitionOrganisationConfigResourceBuilder (List<BiConsumer<Integer,CompetitionOrganisationConfigResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static CompetitionOrganisationConfigResourceBuilder newCompetitionOrganisationConfigResource() {
        return new CompetitionOrganisationConfigResourceBuilder(emptyList());
    }

    public CompetitionOrganisationConfigResourceBuilder withInternationalOrganisationsAllowed(Boolean... internationalOrganisationsAllowed) {
        return withArraySetFieldByReflection("internationalOrganisationsAllowed", internationalOrganisationsAllowed);
    }

    public CompetitionOrganisationConfigResourceBuilder withInternationalLeadOrganisationAllowed(Boolean... internationalLeadOrganisationAllowed) {
        return withArraySetFieldByReflection("internationalLeadOrganisationAllowed", internationalLeadOrganisationAllowed);
    }

    @Override
    protected CompetitionOrganisationConfigResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionOrganisationConfigResource>> actions) {
        return new CompetitionOrganisationConfigResourceBuilder(actions);
    }

    @Override
    protected CompetitionOrganisationConfigResource createInitial() {
        return new CompetitionOrganisationConfigResource();
    }
}
