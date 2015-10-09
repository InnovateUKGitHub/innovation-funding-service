package com.worth.ifs.competition.domain;

import com.worth.ifs.BaseBuilder;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by dwatson on 09/10/15.
 */
public class CompetitionBuilder extends BaseBuilder<Competition> {

    private CompetitionBuilder(List<Consumer<Competition>> newActions, List<BiConsumer<Integer, Competition>> newMultiActions) {
        super(newActions, newMultiActions);
    }

    private CompetitionBuilder() {
    }

    public static CompetitionBuilder newCompetition() {
        return new CompetitionBuilder();
    }

    @Override
    protected BaseBuilder<Competition> createNewBuilderWithActions(List<Consumer<Competition>> actions, List<BiConsumer<Integer, Competition>> multiActions) {
        return new CompetitionBuilder(actions, multiActions);
    }

    @Override
    protected Competition createInitial() {
        return new Competition();
    }
}
