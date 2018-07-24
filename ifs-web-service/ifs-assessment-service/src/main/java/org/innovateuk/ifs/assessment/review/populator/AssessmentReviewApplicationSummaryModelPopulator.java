package org.innovateuk.ifs.assessment.review.populator;

import org.innovateuk.ifs.application.common.populator.SummaryViewModelFragmentPopulator;
import org.innovateuk.ifs.application.common.viewmodel.SummaryViewModel;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.assessment.review.viewmodel.AssessmentReviewApplicationSummaryViewModel;
import org.innovateuk.ifs.assessment.review.viewmodel.AssessmentReviewFeedbackViewModel;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.user.resource.Role.ASSESSOR;

/**
 * Build the model for the Application under review view.
 */
@Component
public class AssessmentReviewApplicationSummaryModelPopulator {

    @Autowired
    private SummaryViewModelFragmentPopulator summaryViewModelPopulator;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

    @Autowired
    private FormInputRestService formInputRestService;

    public AssessmentReviewApplicationSummaryViewModel populateModel(ApplicationForm form, UserResource user, long applicationId) {
        form.setAdminMode(true);
        SummaryViewModel viewModel = summaryViewModelPopulator.populate(applicationId, user, form);
        CompetitionResource competition = competitionRestService.getCompetitionById(viewModel.getCurrentApplication().getCompetition()).getSuccess();
        return new AssessmentReviewApplicationSummaryViewModel(viewModel, competition, assessmentDetails(applicationId, user, viewModel));
    }

    private AssessmentReviewFeedbackViewModel assessmentDetails(long applicationId, UserResource user, SummaryViewModel viewModel) {
        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(applicationId);
        if (isAssessorForApplication(userApplicationRoles, user)) {
            List<AssessorFormInputResponseResource> allAssessorResponses = assessorFormInputResponseRestService
                    .getAllAssessorFormInputResponsesForPanel(applicationId)
                    .getSuccess();

            if(!allAssessorResponses.isEmpty()) {

                List<AssessmentResource> feedbackSummary = viewModel.getFeedbackSummary();

                long assessmentId = feedbackSummary.get(0).getId();

                List<AssessorFormInputResponseResource> assessorResponses = allAssessorResponses
                        .stream()
                        .filter(response -> response.getAssessment().equals(assessmentId))
                        .collect(toList());

                List<AssessorFormInputResponseResource> questionScore = assessorResponses
                        .stream()
                        .filter(response -> "Question score".equals(formInputRestService.getById(response.getFormInput())
                                .getSuccess().getDescription()))
                        .collect(toList());

                List<AssessorFormInputResponseResource> questionFeedback = assessorResponses
                        .stream()
                        .filter(response -> "Feedback".equals(formInputRestService.getById(response.getFormInput())
                                .getSuccess().getDescription()))
                        .collect(toList());
                return new AssessmentReviewFeedbackViewModel(questionScore, questionFeedback);
            }
        }

        return new AssessmentReviewFeedbackViewModel();
    }

    private boolean isAssessorForApplication(List<ProcessRoleResource> userApplicationRoles, UserResource user) {
        return userApplicationRoles
                .stream()
                .filter(processRoleResource -> processRoleResource.getUser().equals(user.getId()))
                .anyMatch(processRoleResource -> processRoleResource.getRole() == ASSESSOR);

    }
}
