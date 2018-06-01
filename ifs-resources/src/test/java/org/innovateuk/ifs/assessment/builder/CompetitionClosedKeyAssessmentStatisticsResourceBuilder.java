package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.resource.CompetitionClosedKeyAssessmentStatisticsResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class CompetitionClosedKeyAssessmentStatisticsResourceBuilder extends
        BaseBuilder<CompetitionClosedKeyAssessmentStatisticsResource,
                CompetitionClosedKeyAssessmentStatisticsResourceBuilder> {

    public static CompetitionClosedKeyAssessmentStatisticsResourceBuilder
    newCompetitionClosedKeyAssessmentStatisticsResource() {
        return new CompetitionClosedKeyAssessmentStatisticsResourceBuilder(emptyList());
    }

    @Override
    protected CompetitionClosedKeyAssessmentStatisticsResourceBuilder createNewBuilderWithActions
            (List<BiConsumer<Integer, CompetitionClosedKeyAssessmentStatisticsResource>> actions) {
        return new CompetitionClosedKeyAssessmentStatisticsResourceBuilder(actions);
    }

    @Override
    protected CompetitionClosedKeyAssessmentStatisticsResource createInitial() {
        return new CompetitionClosedKeyAssessmentStatisticsResource();
    }

    private CompetitionClosedKeyAssessmentStatisticsResourceBuilder(List<BiConsumer<Integer,
            CompetitionClosedKeyAssessmentStatisticsResource>> newMultiActions) {
        super(newMultiActions);
    }

    public CompetitionClosedKeyAssessmentStatisticsResourceBuilder withAssessorsInvited(Integer... assessorsInviteds) {
        return withArraySetFieldByReflection("assessorsInvited", assessorsInviteds);
    }

    public CompetitionClosedKeyAssessmentStatisticsResourceBuilder withAssessorsAccepted(Integer... assessorsAccepteds) {
        return withArraySetFieldByReflection("assessorsAccepted", assessorsAccepteds);
    }

    public CompetitionClosedKeyAssessmentStatisticsResourceBuilder withAssessorsWithoutApplications(Integer... assessorsWithoutApplicationss) {
        return withArraySetFieldByReflection("assessorsWithoutApplications", assessorsWithoutApplicationss);
    }

}
