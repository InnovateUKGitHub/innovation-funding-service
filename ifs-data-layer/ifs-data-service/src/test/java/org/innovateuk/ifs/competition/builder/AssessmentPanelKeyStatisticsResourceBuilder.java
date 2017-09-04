package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelKeyStatisticsResource;

import java.util.List;
import java.util.function.BiConsumer;

public class AssessmentPanelKeyStatisticsResourceBuilder
    extends BaseBuilder<AssessmentPanelKeyStatisticsResource, AssessmentPanelKeyStatisticsResourceBuilder> {

    protected AssessmentPanelKeyStatisticsResourceBuilder() {
        super();
    }

    protected AssessmentPanelKeyStatisticsResourceBuilder(List<BiConsumer<Integer, AssessmentPanelKeyStatisticsResource>> newActions) {
        super(newActions);
    }

    public static AssessmentPanelKeyStatisticsResourceBuilder newAssessmentPanelKeyStatisticsResource() {
        return new AssessmentPanelKeyStatisticsResourceBuilder();
    }

    @Override
    protected AssessmentPanelKeyStatisticsResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentPanelKeyStatisticsResource>> actions) {
        return new AssessmentPanelKeyStatisticsResourceBuilder(actions);
    }

    @Override
    protected AssessmentPanelKeyStatisticsResource createInitial() {
        return new AssessmentPanelKeyStatisticsResource();
    }

    public AssessmentPanelKeyStatisticsResourceBuilder withApplicationsInPanel(Long ...applicationsInPanel) {
        return withArraySetFieldByReflection("applicationsInPanel", applicationsInPanel);
    }

    public AssessmentPanelKeyStatisticsResourceBuilder withAssessorsAccepted(Long ...assessorsAccepted) {
        return withArraySetFieldByReflection("assessorsAccepted", assessorsAccepted);
    }

    public AssessmentPanelKeyStatisticsResourceBuilder withAssessorsInvited(Long ...assessorsInvited) {
        return withArraySetFieldByReflection("assessorsInvited", assessorsInvited);
    }

}
