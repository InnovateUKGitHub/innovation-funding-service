package com.worth.ifs.competition.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.competition.resource.CompetitionTypeResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
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

    public CompetitionTypeResourceBuilder withStateAid(Boolean... stateAid) {
        return withArray((aid, object) -> setField("stateAid", aid, object), stateAid);
    }

    public CompetitionTypeResourceBuilder withCompetitions(List<Long> competitions) {
        return with(object -> object.setCompetitions(competitions));
    }
}
