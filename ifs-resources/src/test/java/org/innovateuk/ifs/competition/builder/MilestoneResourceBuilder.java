package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class MilestoneResourceBuilder extends BaseBuilder<MilestoneResource, MilestoneResourceBuilder> {

    private MilestoneResourceBuilder (List<BiConsumer<Integer, MilestoneResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static MilestoneResourceBuilder newMilestoneResource() {
        return new MilestoneResourceBuilder(emptyList()).with(uniqueIds());
    }

    public MilestoneResourceBuilder withCompetitionId(Long... competitionIds) {
        return withArray((competitionId, object) -> BaseBuilderAmendFunctions.setField("competitionId", competitionId, object), competitionIds);
    }

    public MilestoneResourceBuilder withName(MilestoneType... types) {
        return withArray((type, object) -> BaseBuilderAmendFunctions.setField("type", type, object), types);
    }

    public MilestoneResourceBuilder withDate(ZonedDateTime... dates) {
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
