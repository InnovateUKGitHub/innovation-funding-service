package com.worth.ifs.competition.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.BaseBuilderAmendFunctions;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.resource.MilestoneType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class MilestoneResourceBuilder extends BaseBuilder<MilestoneResource, MilestoneResourceBuilder> {

    private MilestoneResourceBuilder (List<BiConsumer<Integer, MilestoneResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static MilestoneResourceBuilder newMilestoneResource() {
        return new MilestoneResourceBuilder(emptyList()).with(uniqueIds());
    }

    public MilestoneResourceBuilder withCompetitionId(Long... competitions) {
        return withArray((competition, object) -> BaseBuilderAmendFunctions.setField("competition", competition, object), competitions);
    }

    public MilestoneResourceBuilder withName(MilestoneType... types) {
        return withArray((type, object) -> BaseBuilderAmendFunctions.setField("type", type, object), types);
    }

    public MilestoneResourceBuilder withDate(LocalDateTime... dates) {
        return withArray((date, object) -> BaseBuilderAmendFunctions.setField("date", date, object), dates);
    }

    public MilestoneResourceBuilder withId(Long... ids) {
        return withArray((id, object) -> BaseBuilderAmendFunctions.setField("id", id, object), ids);
    }

    public MilestoneResourceBuilder withType(MilestoneType... types) {
        return withArray((type, milestone) -> BaseBuilderAmendFunctions.setField("type", type, milestone), types);
    }

    @Override
    protected MilestoneResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, MilestoneResource>> actions) {
        return new MilestoneResourceBuilder(actions);
    }

    @Override
    protected MilestoneResource createInitial() {
        return new MilestoneResource();
    }
}
