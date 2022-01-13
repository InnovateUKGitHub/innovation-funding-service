package org.innovateuk.ifs.review.builder;
import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.review.resource.ReviewInviteStatisticsResource;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Resource builder for AssessmentPanelInviteStatisticsResources
 */
public class ReviewInviteStatisticsResourceBuilder
    extends BaseBuilder<ReviewInviteStatisticsResource, ReviewInviteStatisticsResourceBuilder> {

    protected ReviewInviteStatisticsResourceBuilder() {
        super();
    }

    protected ReviewInviteStatisticsResourceBuilder(List<BiConsumer<Integer, ReviewInviteStatisticsResource>> newActions) {
        super(newActions);
    }

    public static ReviewInviteStatisticsResourceBuilder newReviewInviteStatisticsResource() {
        return new ReviewInviteStatisticsResourceBuilder();
    }

    @Override
    protected ReviewInviteStatisticsResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ReviewInviteStatisticsResource>> actions) {
        return new ReviewInviteStatisticsResourceBuilder(actions);
    }

    @Override
    protected ReviewInviteStatisticsResource createInitial() {
        return new ReviewInviteStatisticsResource();
    }

    public ReviewInviteStatisticsResourceBuilder withAssessorsInvited(Integer ...assessorsInvited) {
        return withArraySetFieldByReflection("invited", assessorsInvited);
    }

    public ReviewInviteStatisticsResourceBuilder withAssessorsAccepted(Integer ...assessorsAccepted) {
        return withArraySetFieldByReflection("accepted", assessorsAccepted);
    }

    public ReviewInviteStatisticsResourceBuilder withAssessorsRejected(Integer ...assessorsDeclined) {
        return withArraySetFieldByReflection("declined", assessorsDeclined);
    }

}
