package org.innovateuk.ifs.competition.builder;
import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.review.resource.ReviewKeyStatisticsResource;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Resource builder for AssessmentPanelKeyStatisticsResources
 */

public class AssessmentPanelKeyStatisticsResourceBuilder
        extends BaseBuilder<ReviewKeyStatisticsResource, AssessmentPanelKeyStatisticsResourceBuilder> {

    protected AssessmentPanelKeyStatisticsResourceBuilder() {
        super();
    }

    protected AssessmentPanelKeyStatisticsResourceBuilder(List<BiConsumer<Integer, ReviewKeyStatisticsResource>> newActions) {
        super(newActions);
    }

    public static AssessmentPanelKeyStatisticsResourceBuilder newAssessmentPanelKeyStatisticsResource() {
        return new AssessmentPanelKeyStatisticsResourceBuilder();
    }

    @Override
    protected AssessmentPanelKeyStatisticsResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ReviewKeyStatisticsResource>> actions) {
        return new AssessmentPanelKeyStatisticsResourceBuilder(actions);
    }

    @Override
    protected ReviewKeyStatisticsResource createInitial() {
        return new ReviewKeyStatisticsResource();
    }

    public AssessmentPanelKeyStatisticsResourceBuilder withApplicationsInPanel(Integer ...applicationsInPanel) {
        return withArraySetFieldByReflection("applicationsInPanel", applicationsInPanel);
    }

    public AssessmentPanelKeyStatisticsResourceBuilder withAssessorsAccepted(Integer ...assessorsAccepted) {
        return withArraySetFieldByReflection("assessorsAccepted", assessorsAccepted);
    }

    public AssessmentPanelKeyStatisticsResourceBuilder withAssessorsPending(Integer ...assessorsPending) {
        return withArraySetFieldByReflection("assessorsPending", assessorsPending);
    }

}
