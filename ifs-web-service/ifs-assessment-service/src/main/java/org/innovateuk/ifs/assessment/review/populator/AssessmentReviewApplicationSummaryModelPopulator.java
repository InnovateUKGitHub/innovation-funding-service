package org.innovateuk.ifs.assessment.review.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.populator.ApplicationReadOnlyViewModelPopulator;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.assessment.review.viewmodel.AssessmentReviewApplicationSummaryViewModel;
import org.innovateuk.ifs.assessment.review.viewmodel.AssessmentReviewFeedbackViewModel;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
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
    private ApplicationReadOnlyViewModelPopulator summaryViewModelPopulator;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

    @Autowired
    private FormInputRestService formInputRestService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private AssessmentRestService assessmentRestService;

    public AssessmentReviewApplicationSummaryViewModel populateModel(UserResource user, long applicationId) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        ApplicationReadOnlyViewModel readOnlyViewModel = summaryViewModelPopulator.populate(application, competition, user, ApplicationReadOnlySettings.defaultSettings());
        long termsAndConditionsId = sectionService.getTermsAndConditionsSection(competition.getId()).getQuestions().get(0);
        return new AssessmentReviewApplicationSummaryViewModel(readOnlyViewModel,
                                                               competition,
                                                               assessmentDetails(applicationId, user),
                                                               termsAndConditionsId);
    }

    private AssessmentReviewFeedbackViewModel assessmentDetails(long applicationId, UserResource user) {
        List<ProcessRoleResource> userApplicationRoles = userRestService.findProcessRole(applicationId).getSuccess();
        if (isAssessorForApplication(userApplicationRoles, user)) {
            List<AssessorFormInputResponseResource> allAssessorResponses = assessorFormInputResponseRestService
                    .getAllAssessorFormInputResponsesForPanel(applicationId)
                    .getSuccess();

            if(!allAssessorResponses.isEmpty()) {

                List<AssessmentResource> feedbackSummary = assessmentRestService
                        .getByUserAndApplication(user.getId(), applicationId)
                        .getSuccess();

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
