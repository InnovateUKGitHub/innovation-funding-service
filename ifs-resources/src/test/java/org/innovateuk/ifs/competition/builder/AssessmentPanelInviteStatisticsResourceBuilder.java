package org.innovateuk.ifs.competition.builder;
import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.review.resource.ReviewInviteStatisticsResource;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Resource builder for AssessmentPanelInviteStatisticsResources
 */
public class AssessmentPanelInviteStatisticsResourceBuilder
    extends BaseBuilder<ReviewInviteStatisticsResource, AssessmentPanelInviteStatisticsResourceBuilder> {

    protected AssessmentPanelInviteStatisticsResourceBuilder() {
        super();
    }

    protected AssessmentPanelInviteStatisticsResourceBuilder(List<BiConsumer<Integer, ReviewInviteStatisticsResource>> newActions) {
        super(newActions);
    }

    public static AssessmentPanelInviteStatisticsResourceBuilder newAssessmentPanelInviteStatisticsResource() {
        return new AssessmentPanelInviteStatisticsResourceBuilder();
    }

    @Override
    protected AssessmentPanelInviteStatisticsResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ReviewInviteStatisticsResource>> actions) {
        return new AssessmentPanelInviteStatisticsResourceBuilder(actions);
    }

    @Override
    protected ReviewInviteStatisticsResource createInitial() {
        return new ReviewInviteStatisticsResource();
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

}
