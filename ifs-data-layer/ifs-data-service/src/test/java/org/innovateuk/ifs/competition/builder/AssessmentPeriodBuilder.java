package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.domain.AssessmentPeriod;
import org.innovateuk.ifs.competition.domain.Competition;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.createDefault;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class AssessmentPeriodBuilder extends BaseBuilder<AssessmentPeriod, AssessmentPeriodBuilder> {

    private AssessmentPeriodBuilder(List<BiConsumer<Integer, AssessmentPeriod>> newMultiActions) {
        super(newMultiActions);
    }

    public static AssessmentPeriodBuilder newAssessmentPeriod() {
        return new AssessmentPeriodBuilder(emptyList()).with(uniqueIds());
    }

    public AssessmentPeriodBuilder withCompetition(Competition... competitions) {
        return withArraySetFieldByReflection("competition", competitions);
    }

    public AssessmentPeriodBuilder withIndex(Integer... indexes) {
        return withArraySetFieldByReflection("index", indexes);
    }

    @Override
    protected AssessmentPeriodBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentPeriod>> actions) {
        return new AssessmentPeriodBuilder(actions);
    }

    @Override
    protected AssessmentPeriod createInitial() {
        return createDefault(AssessmentPeriod.class);
    }
}
