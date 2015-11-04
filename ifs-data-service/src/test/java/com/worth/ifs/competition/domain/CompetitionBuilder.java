package com.worth.ifs.competition.domain;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.domain.Section;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Created by dwatson on 09/10/15.
 */
public class CompetitionBuilder extends BaseBuilder<Competition, CompetitionBuilder> {

    private CompetitionBuilder(List<BiConsumer<Integer, Competition>> newMultiActions) {
        super(newMultiActions);
    }

    private CompetitionBuilder() {
    }

    public static CompetitionBuilder newCompetition() {
        return new CompetitionBuilder();
    }

    public CompetitionBuilder withSections(List<Section> sections) {
        return with(competition -> competition.setSections(sections));
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
