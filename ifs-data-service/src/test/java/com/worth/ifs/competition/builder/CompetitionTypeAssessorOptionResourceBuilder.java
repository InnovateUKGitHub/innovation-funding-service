package com.worth.ifs.competition.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.competition.resource.CompetitionTypeAssessorOptionResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class CompetitionTypeAssessorOptionResourceBuilder extends BaseBuilder<CompetitionTypeAssessorOptionResource, CompetitionTypeAssessorOptionResourceBuilder> {

    private CompetitionTypeAssessorOptionResourceBuilder(List<BiConsumer<Integer, CompetitionTypeAssessorOptionResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static CompetitionTypeAssessorOptionResourceBuilder newCompetitionTypeAssessorOptionResource() {
        return new CompetitionTypeAssessorOptionResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected CompetitionTypeAssessorOptionResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionTypeAssessorOptionResource>> actions) {
        return new CompetitionTypeAssessorOptionResourceBuilder(actions);
    }

    @Override
    protected CompetitionTypeAssessorOptionResource createInitial() {
        return new CompetitionTypeAssessorOptionResource();
    }

    public CompetitionTypeAssessorOptionResourceBuilder withId(Long... ids) {
        return withArray((id, object) -> setField("id", id, object), ids);
    }

    public CompetitionTypeAssessorOptionResourceBuilder withAssessorOptionName(String... optionNames) {
        return withArray((streamName, object) -> setField("assessorOptionName", streamName, object), optionNames);
    }

    public CompetitionTypeAssessorOptionResourceBuilder withCompetitionTypeId(Long... competitionTypeId) {
        return withArray((streamName, object) -> setField("competitionTypeId", streamName, object), competitionTypeId);
    }

    public CompetitionTypeAssessorOptionResourceBuilder withAssessorOptionValue(Integer... optionValues) {
        return withArray((streamName, object) -> setField("assessorOptionValue", streamName, object), optionValues);
    }

    public CompetitionTypeAssessorOptionResourceBuilder withDefaultOption(Boolean... defaultOptions) {
        return withArray((streamName, object) -> setField("defaultOption", streamName, object), defaultOptions);
    }
}
