package com.worth.ifs.competition.builder;


import com.worth.ifs.BaseBuilder;
import com.worth.ifs.competition.domain.Milestone;
import com.worth.ifs.competition.resource.MilestoneType;
import com.worth.ifs.user.builder.ProfileBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.*;
import static java.util.Collections.emptyList;

public class MilestoneBuilder extends BaseBuilder<Milestone, MilestoneBuilder> {

    private MilestoneBuilder(List<BiConsumer<Integer, Milestone>> newMultiActions) {
        super(newMultiActions);
    }

    public static MilestoneBuilder newMilestone() {
        return new MilestoneBuilder(emptyList()).with(uniqueIds());
    }

    public MilestoneBuilder withDate(LocalDateTime date) {
        return with(milestone -> setField("date", date, milestone));
    }

    public MilestoneBuilder withCompetitionId(Long competition) {
        return with(milestone -> setField("competition", competition, milestone));
    }

    @Override
    protected MilestoneBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Milestone>> actions) {
        return new MilestoneBuilder(actions);
    }

    @Override
    protected Milestone createInitial() {
        return createDefault(Milestone.class);
    }


    public MilestoneBuilder withType(MilestoneType... types) {
        return withArray((type, milestone) -> setField("type", type, milestone), types);
    }
}