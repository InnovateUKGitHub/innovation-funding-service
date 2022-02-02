package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.resource.AssessorCountOptionResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class AssessorCountOptionResourceBuilder extends BaseBuilder<AssessorCountOptionResource, AssessorCountOptionResourceBuilder> {

    private AssessorCountOptionResourceBuilder(List<BiConsumer<Integer, AssessorCountOptionResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static AssessorCountOptionResourceBuilder newAssessorCountOptionResource() {
        return new AssessorCountOptionResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected AssessorCountOptionResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessorCountOptionResource>> actions) {
        return new AssessorCountOptionResourceBuilder(actions);
    }

    @Override
    protected AssessorCountOptionResource createInitial() {
        return new AssessorCountOptionResource();
    }

    public AssessorCountOptionResourceBuilder withId(Long... ids) {
        return withArray((id, object) ->setField("id", id, object), ids);
    }

    public AssessorCountOptionResourceBuilder withAssessorOptionName(String... optionNames) {
        return withArray((streamName, object) ->setField("optionName", streamName, object), optionNames);
    }

    public AssessorCountOptionResourceBuilder withCompetitionType(Long... competitionType) {
        return withArray((streamName, object) ->setField("competitionType", streamName, object), competitionType);
    }

    public AssessorCountOptionResourceBuilder withAssessorOptionValue(Integer... optionValues) {
        return withArray((streamName, object) ->setField("optionValue", streamName, object), optionValues);
    }

    public AssessorCountOptionResourceBuilder withDefaultOption(Boolean... defaultOptions) {
        return withArray((streamName, object) ->setField("defaultOption", streamName, object), defaultOptions);
    }
}
