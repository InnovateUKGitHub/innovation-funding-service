package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.resource.CompetitionOpenKeyAssessmentStatisticsResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class CompetitionOpenKeyAssessmentStatisticsResourceBuilder extends
        BaseBuilder<CompetitionOpenKeyAssessmentStatisticsResource,
                CompetitionOpenKeyAssessmentStatisticsResourceBuilder> {

    public static CompetitionOpenKeyAssessmentStatisticsResourceBuilder
    newCompetitionOpenKeyAssessmentStatisticsResource() {
        return new CompetitionOpenKeyAssessmentStatisticsResourceBuilder(emptyList());
    }

    @Override
    protected CompetitionOpenKeyAssessmentStatisticsResourceBuilder createNewBuilderWithActions(
            List<BiConsumer<Integer, CompetitionOpenKeyAssessmentStatisticsResource>> actions) {
        return new CompetitionOpenKeyAssessmentStatisticsResourceBuilder(actions);
    }

    @Override
    protected CompetitionOpenKeyAssessmentStatisticsResource createInitial() {
        return new CompetitionOpenKeyAssessmentStatisticsResource();
    }

    private CompetitionOpenKeyAssessmentStatisticsResourceBuilder(List<BiConsumer<Integer,
            CompetitionOpenKeyAssessmentStatisticsResource>> newMultiActions) {
        super(newMultiActions);
    }

    public CompetitionOpenKeyAssessmentStatisticsResourceBuilder withAssessorsInvited(Integer... assessorsInviteds) {
        return withArraySetFieldByReflection("assessorsInvited", assessorsInviteds);
    }

    public CompetitionOpenKeyAssessmentStatisticsResourceBuilder withAssessorsAccepted(Integer... assessorsAccepteds) {
        return withArraySetFieldByReflection("assessorsAccepted", assessorsAccepteds);
    }
}
