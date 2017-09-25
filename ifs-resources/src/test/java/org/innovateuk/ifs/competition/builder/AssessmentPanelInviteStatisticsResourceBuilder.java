package org.innovateuk.ifs.competition.builder;
import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelInviteStatisticsResource;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Resource builder for AssessmentPanelInviteStatisticsResources
 */
public class AssessmentPanelInviteStatisticsResourceBuilder
    extends BaseBuilder<AssessmentPanelInviteStatisticsResource, AssessmentPanelInviteStatisticsResourceBuilder> {

    protected AssessmentPanelInviteStatisticsResourceBuilder() {
        super();
    }

    protected AssessmentPanelInviteStatisticsResourceBuilder(List<BiConsumer<Integer, AssessmentPanelInviteStatisticsResource>> newActions) {
        super(newActions);
    }

    public static AssessmentPanelInviteStatisticsResourceBuilder newAssessmentPanelInviteStatisticsResource() {
        return new AssessmentPanelInviteStatisticsResourceBuilder();
    }

    @Override
    protected AssessmentPanelInviteStatisticsResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentPanelInviteStatisticsResource>> actions) {
        return new AssessmentPanelInviteStatisticsResourceBuilder(actions);
    }

    @Override
    protected AssessmentPanelInviteStatisticsResource createInitial() {
        return new AssessmentPanelInviteStatisticsResource();
    }

    public AssessmentPanelInviteStatisticsResourceBuilder withAssessorsInvited(Integer ...assessorsInvited) {
        return withArraySetFieldByReflection("invited", assessorsInvited);
    }

    public AssessmentPanelInviteStatisticsResourceBuilder withAssessorsAccepted(Integer ...assessorsAccepted) {
        return withArraySetFieldByReflection("accepted", assessorsAccepted);
    }

    public AssessmentPanelInviteStatisticsResourceBuilder withAssessorsRejected(Integer ...assessorsDeclined) {
        return withArraySetFieldByReflection("declined", assessorsDeclined);
    }

    public AssessmentPanelInviteStatisticsResourceBuilder withAssessorsPending(Integer ...assessorsListed) {
        return withArraySetFieldByReflection("pending", assessorsListed);
    }
}
