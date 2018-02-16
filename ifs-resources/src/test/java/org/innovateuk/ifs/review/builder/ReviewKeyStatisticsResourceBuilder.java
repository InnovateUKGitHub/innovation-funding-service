package org.innovateuk.ifs.review.builder;
import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.review.resource.ReviewKeyStatisticsResource;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Resource builder for AssessmentPanelKeyStatisticsResources
 */

public class ReviewKeyStatisticsResourceBuilder
        extends BaseBuilder<ReviewKeyStatisticsResource, ReviewKeyStatisticsResourceBuilder> {

    protected ReviewKeyStatisticsResourceBuilder() {
        super();
    }

    protected ReviewKeyStatisticsResourceBuilder(List<BiConsumer<Integer, ReviewKeyStatisticsResource>> newActions) {
        super(newActions);
    }

    public static ReviewKeyStatisticsResourceBuilder newReviewKeyStatisticsResource() {
        return new ReviewKeyStatisticsResourceBuilder();
    }

    @Override
    protected ReviewKeyStatisticsResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ReviewKeyStatisticsResource>> actions) {
        return new ReviewKeyStatisticsResourceBuilder(actions);
    }

    @Override
    protected ReviewKeyStatisticsResource createInitial() {
        return new ReviewKeyStatisticsResource();
    }

    public ReviewKeyStatisticsResourceBuilder withApplicationsInPanel(Integer ...applicationsInPanel) {
        return withArraySetFieldByReflection("applicationsInPanel", applicationsInPanel);
    }

    public ReviewKeyStatisticsResourceBuilder withAssessorsAccepted(Integer ...assessorsAccepted) {
        return withArraySetFieldByReflection("assessorsAccepted", assessorsAccepted);
    }

    public ReviewKeyStatisticsResourceBuilder withAssessorsPending(Integer ...assessorsPending) {
        return withArraySetFieldByReflection("assessorsPending", assessorsPending);
    }

}
