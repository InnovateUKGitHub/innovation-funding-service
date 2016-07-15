package com.worth.ifs.competition.builder;


import com.worth.ifs.BaseBuilder;
import com.worth.ifs.competition.domain.Milestone;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.idBasedNames;
import static com.worth.ifs.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class MilestoneBuilder extends BaseBuilder<Milestone, MilestoneBuilder> {

    private MilestoneBuilder(List<BiConsumer<Integer, Milestone>> newMultiActions) {
        super(newMultiActions);
    }

    public static MilestoneBuilder newCompetition() {
        return new MilestoneBuilder(emptyList()).with(uniqueIds()).with(idBasedNames("Milestone "));
    }

    public MilestoneBuilder withId(Long... ids) {
        return withArray((id, object) -> setField("id", id, object), ids);
    }

    public MilestoneBuilder withName(String name) {
        return with(milestone -> setField("name", name, milestone));
    }

    public MilestoneBuilder withDate(LocalDateTime date) {
        return with(milestone -> setField("Date", date, milestone));
    }

    @Override
    protected MilestoneBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Milestone>> actions) {
        return new MilestoneBuilder(actions);
    }

    @Override
    protected Milestone createInitial() {
        return new Milestone();
    }



}