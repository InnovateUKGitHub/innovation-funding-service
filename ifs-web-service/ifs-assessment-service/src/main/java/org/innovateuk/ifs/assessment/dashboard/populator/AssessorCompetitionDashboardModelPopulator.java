package org.innovateuk.ifs.assessment.dashboard.populator;

import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorCompetitionDashboardApplicationViewModel;
import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorCompetitionDashboardViewModel;
import org.innovateuk.ifs.assessment.resource.dashboard.ApplicationAssessmentResource;
import org.innovateuk.ifs.assessment.resource.dashboard.AssessorCompetitionDashboardResource;
import org.innovateuk.ifs.assessment.service.AssessorCompetitionDashboardRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.assessment.resource.AssessmentState.SUBMITTED;

/**
 * Build the model for the Assessor Competition Dashboard view.
 */
@Component
public class AssessorCompetitionDashboardModelPopulator {

    @Autowired
    private AssessorCompetitionDashboardRestService assessorCompetitionDashboardRestService;

    public AssessorCompetitionDashboardViewModel populateModel(Long competitionId, Long userId) {

        AssessorCompetitionDashboardResource assessorCompetitionDashboard = assessorCompetitionDashboardRestService.getAssessorCompetitionDashboard(competitionId, userId).getSuccess();

        List<AssessorCompetitionDashboardApplicationViewModel> outstanding = getOutstandingAssessments(assessorCompetitionDashboard.getApplicationAssessments());
        List<AssessorCompetitionDashboardApplicationViewModel> submitted = getSubmittedAssessments(assessorCompetitionDashboard.getApplicationAssessments());

        boolean submitVisible = outstanding.stream()
                .anyMatch(AssessorCompetitionDashboardApplicationViewModel::isReadyToSubmit);

        return new AssessorCompetitionDashboardViewModel(
                assessorCompetitionDashboard.getCompetitionId(),
                assessorCompetitionDashboard.getCompetitionName(),
                assessorCompetitionDashboard.getInnovationLead(),
                assessorCompetitionDashboard.getAssessorAcceptDate(),
                assessorCompetitionDashboard.getAssessorDeadlineDate(),
                submitted,
                outstanding,
                submitVisible
        );
    }

    private List<AssessorCompetitionDashboardApplicationViewModel> getSubmittedAssessments(List<ApplicationAssessmentResource> assessmentResources) {
        return assessmentResources.stream()
                .filter(this::isAssessmentSubmitted)
                .map(this::createApplicationViewModel)
                .collect(Collectors.toList());
    }

    private List<AssessorCompetitionDashboardApplicationViewModel> getOutstandingAssessments(List<ApplicationAssessmentResource> assessmentResources) {
        return assessmentResources.stream()
                .filter(resource -> !isAssessmentSubmitted(resource))
                .map(this::createApplicationViewModel)
                .collect(Collectors.toList());
    }

    private boolean isAssessmentSubmitted(ApplicationAssessmentResource assessmentResource) {
        return SUBMITTED == assessmentResource.getState();
    }

    private AssessorCompetitionDashboardApplicationViewModel createApplicationViewModel(ApplicationAssessmentResource assessment) {

        return new AssessorCompetitionDashboardApplicationViewModel(
                assessment.getApplicationId(),
                assessment.getAssessmentId(),
                assessment.getCompetitionName(),
                assessment.getLeadOrganisation(),
                assessment.getState(),
                assessment.getOverallScore(),
                assessment.isRecommended()
        );
    }
}
