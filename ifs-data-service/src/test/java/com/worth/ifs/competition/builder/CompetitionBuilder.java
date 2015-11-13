package com.worth.ifs.competition.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.competition.domain.Competition;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class CompetitionBuilder extends BaseBuilder<Competition, CompetitionBuilder> {

    private CompetitionBuilder(List<BiConsumer<Integer, Competition>> newMultiActions) {
        super(newMultiActions);
    }

    public static CompetitionBuilder newCompetition() {
        return new CompetitionBuilder(emptyList()).with(uniqueIds());
    }

    public CompetitionBuilder withSections(List<Section> sections) {
        return with(competition -> competition.setSections(sections));
    }

    public CompetitionBuilder withStartDate(LocalDateTime startDate) {
        return withStartDate(startDate.toLocalDate());
    }

    public CompetitionBuilder withEndDate(LocalDateTime endDate) {
        return withEndDate(endDate.toLocalDate());
    }

    public CompetitionBuilder withStartDate(LocalDate startDate) {
        return with(competition -> setField("startDate", startDate, competition));
    }

    public CompetitionBuilder withEndDate(LocalDate endDate) {
        return with(competition -> setField("endDate", endDate, competition));
    }

    @Override
    protected CompetitionBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Competition>> actions) {
        return new CompetitionBuilder(actions);
    }

    @Override
    protected Competition createInitial() {
        return new Competition();
    }
}
