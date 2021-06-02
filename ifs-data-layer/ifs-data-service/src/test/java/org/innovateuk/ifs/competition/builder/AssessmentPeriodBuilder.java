package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.assessment.period.domain.AssessmentPeriod;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Milestone;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.*;

public class AssessmentPeriodBuilder extends BaseBuilder<AssessmentPeriod, AssessmentPeriodBuilder> {

    private AssessmentPeriodBuilder(List<BiConsumer<Integer, AssessmentPeriod>> newMultiActions) {
        super(newMultiActions);
    }

    public static AssessmentPeriodBuilder newAssessmentPeriod() {
        return new AssessmentPeriodBuilder(emptyList()).with(uniqueIds());
    }

    public AssessmentPeriodBuilder withId(Long... ids) {
        return withArray((id, object) -> setField("id", id, object), ids);
    }

    public AssessmentPeriodBuilder withCompetition(Competition... competitions) {
        return withArraySetFieldByReflection("competition", competitions);
    }

    public AssessmentPeriodBuilder withMilestones(List<Milestone>... milestones) {
        return withArraySetFieldByReflection("milestones", milestones);
    }

    public AssessmentPeriodBuilder withApplications(List<Application>... applications) {
        return withArraySetFieldByReflection("applications", applications);
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
