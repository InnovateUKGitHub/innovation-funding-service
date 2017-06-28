package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.domain.CompetitionType;
import org.innovateuk.ifs.competition.domain.AssessorCountOption;

import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.BuilderAmendFunctions.setField;
import static org.innovateuk.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class AssessorCountOptionBuilder extends BaseBuilder<AssessorCountOption, AssessorCountOptionBuilder> {

    private AssessorCountOptionBuilder(List<BiConsumer<Integer, AssessorCountOption>> newMultiActions) {
        super(newMultiActions);
    }

    public static AssessorCountOptionBuilder newAssessorCountOption() {
        return new AssessorCountOptionBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected AssessorCountOptionBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessorCountOption>> actions) {
        return new AssessorCountOptionBuilder(actions);
    }

    @Override
    protected AssessorCountOption createInitial() {
        return new AssessorCountOption();
    }

    public AssessorCountOptionBuilder withId(Long... ids) {
        return withArray((id, object) -> setField("id", id, object), ids);
    }

    public AssessorCountOptionBuilder withAssessorOptionName(String... optionNames) {
        return withArray((streamName, object) -> setField("optionName", streamName, object), optionNames);
    }

    public AssessorCountOptionBuilder withCompetitionType(CompetitionType... competitionType) {
        return withArray((streamName, object) -> setField("competitionType", streamName, object), competitionType);
    }

    public AssessorCountOptionBuilder withAssessorOptionValue(Integer... optionValues) {
        return withArray((streamName, object) -> setField("optionValue", streamName, object), optionValues);
    }

    public AssessorCountOptionBuilder withDefaultOption(Boolean... defaultOptions) {
        return withArray((streamName, object) -> setField("defaultOption", streamName, object), defaultOptions);
    }
}
