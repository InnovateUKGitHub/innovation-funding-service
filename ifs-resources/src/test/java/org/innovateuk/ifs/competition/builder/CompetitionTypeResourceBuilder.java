package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;

import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class CompetitionTypeResourceBuilder extends BaseBuilder<CompetitionTypeResource, CompetitionTypeResourceBuilder> {

    private CompetitionTypeResourceBuilder(List<BiConsumer<Integer, CompetitionTypeResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static CompetitionTypeResourceBuilder newCompetitionTypeResource() {
        return new CompetitionTypeResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected CompetitionTypeResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionTypeResource>> actions) {
        return new CompetitionTypeResourceBuilder(actions);
    }

    @Override
    protected CompetitionTypeResource createInitial() {
        return new CompetitionTypeResource();
    }

    public CompetitionTypeResourceBuilder withId(Long... ids) {
        return withArray((id, object) -> setField("id", id, object), ids);
    }

    public CompetitionTypeResourceBuilder withName(String... names) {
        return withArray((name, object) -> setField("name", name, object), names);
    }

    public CompetitionTypeResourceBuilder withCompetitions(List<Long> competitions) {
        return with(object -> object.setCompetitions(competitions));
    }
}
