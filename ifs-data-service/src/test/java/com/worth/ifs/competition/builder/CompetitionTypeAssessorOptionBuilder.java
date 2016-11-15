package com.worth.ifs.competition.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.competition.domain.CompetitionTypeAssessorOption;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class CompetitionTypeAssessorOptionBuilder extends BaseBuilder<CompetitionTypeAssessorOption, CompetitionTypeAssessorOptionBuilder> {

    private CompetitionTypeAssessorOptionBuilder(List<BiConsumer<Integer, CompetitionTypeAssessorOption>> newMultiActions) {
        super(newMultiActions);
    }

    public static CompetitionTypeAssessorOptionBuilder newCompetitionTypeAssessorOption() {
        return new CompetitionTypeAssessorOptionBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected CompetitionTypeAssessorOptionBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionTypeAssessorOption>> actions) {
        return new CompetitionTypeAssessorOptionBuilder(actions);
    }

    @Override
    protected CompetitionTypeAssessorOption createInitial() {
        return new CompetitionTypeAssessorOption();
    }

    public CompetitionTypeAssessorOptionBuilder withId(Long... ids) {
        return withArray((id, object) -> setField("id", id, object), ids);
    }

    public CompetitionTypeAssessorOptionBuilder withAssessorOptionName(String... optionNames) {
        return withArray((streamName, object) -> setField("assessorOptionName", streamName, object), optionNames);
    }

    public CompetitionTypeAssessorOptionBuilder withCompetitionTypeId(Long... competitionTypeId) {
        return withArray((streamName, object) -> setField("competitionTypeId", streamName, object), competitionTypeId);
    }

    public CompetitionTypeAssessorOptionBuilder withAssessorOptionValue(Integer... optionValues) {
        return withArray((streamName, object) -> setField("assessorOptionValue", streamName, object), optionValues);
    }

    public CompetitionTypeAssessorOptionBuilder withDefaultOption(Boolean... defaultOptions) {
        return withArray((streamName, object) -> setField("defaultOption", streamName, object), defaultOptions);
    }
}
