package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.resource.CompetitionReadyToOpenKeyAssessmentStatisticsResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class CompetitionReadyToOpenKeyAssessmentStatisticsResourceBuilder extends
        BaseBuilder<CompetitionReadyToOpenKeyAssessmentStatisticsResource,
                CompetitionReadyToOpenKeyAssessmentStatisticsResourceBuilder> {

    public static CompetitionReadyToOpenKeyAssessmentStatisticsResourceBuilder
    newCompetitionReadyToOpenKeyAssessmentStatisticsResource() {
        return new CompetitionReadyToOpenKeyAssessmentStatisticsResourceBuilder(emptyList());
    }

    @Override
    protected CompetitionReadyToOpenKeyAssessmentStatisticsResourceBuilder createNewBuilderWithActions
            (List<BiConsumer<Integer, CompetitionReadyToOpenKeyAssessmentStatisticsResource>> actions) {
        return new CompetitionReadyToOpenKeyAssessmentStatisticsResourceBuilder(actions);
    }

    @Override
    protected CompetitionReadyToOpenKeyAssessmentStatisticsResource createInitial() {
        return new CompetitionReadyToOpenKeyAssessmentStatisticsResource();
    }

    private CompetitionReadyToOpenKeyAssessmentStatisticsResourceBuilder(List<BiConsumer<Integer,
            CompetitionReadyToOpenKeyAssessmentStatisticsResource>> newMultiActions) {
        super(newMultiActions);
    }

    public CompetitionReadyToOpenKeyAssessmentStatisticsResourceBuilder withAssessorsInvited(Integer... assessorsInviteds) {
        return withArraySetFieldByReflection("assessorsInvited", assessorsInviteds);
    }

    public CompetitionReadyToOpenKeyAssessmentStatisticsResourceBuilder withAssessorsAccepted(Integer... assessorsAccepteds) {
        return withArraySetFieldByReflection("assessorsAccepted", assessorsAccepteds);
    }

}
