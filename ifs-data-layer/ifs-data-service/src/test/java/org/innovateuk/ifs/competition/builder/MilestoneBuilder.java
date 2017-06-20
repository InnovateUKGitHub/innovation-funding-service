package org.innovateuk.ifs.competition.builder;


import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.competition.resource.MilestoneType;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.createDefault;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class MilestoneBuilder extends BaseBuilder<Milestone, MilestoneBuilder> {

    private MilestoneBuilder(List<BiConsumer<Integer, Milestone>> newMultiActions) {
        super(newMultiActions);
    }

    public static MilestoneBuilder newMilestone() {
        return new MilestoneBuilder(emptyList()).with(uniqueIds());
    }

    public MilestoneBuilder withDate(ZonedDateTime... dates) {
        return withArraySetFieldByReflection("date", dates);
    }

    public MilestoneBuilder withCompetition(Competition... competitions) {
        return withArraySetFieldByReflection("competition", competitions);
    }

    public MilestoneBuilder withType(MilestoneType... types) {
        return withArraySetFieldByReflection("type", types);
    }

    @Override
    protected MilestoneBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Milestone>> actions) {
        return new MilestoneBuilder(actions);
    }

    @Override
    protected Milestone createInitial() {
        return createDefault(Milestone.class);
    }
}
