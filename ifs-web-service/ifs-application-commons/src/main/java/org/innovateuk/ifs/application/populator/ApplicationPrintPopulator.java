package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.populator.ApplicationReadOnlyViewModelPopulator;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import static org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings.defaultSettings;

@Component
public class ApplicationPrintPopulator {

    @Autowired
    private ApplicationReadOnlyViewModelPopulator applicationReadOnlyViewModelPopulator;

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private InterviewAssignmentRestService interviewAssignmentRestService;

    public String print(final Long applicationId,
                        Model model, UserResource user) {

        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        ApplicationReadOnlySettings settings = defaultSettings()
                .setIncludeAllAssessorFeedback(userCanViewFeedback(user, competition, application))
                .setIncludeAllSupporterFeedback(userCanViewSupporterFeedback(user, competition, application));

        ApplicationReadOnlyViewModel applicationReadOnlyViewModel = applicationReadOnlyViewModelPopulator.populate(applicationId, user, settings);
        model.addAttribute("model", applicationReadOnlyViewModel);
        return "application/print";
    }

    private boolean userCanViewFeedback(UserResource user, CompetitionResource competition, ApplicationResource application) {
        return (user.hasRole(Role.PROJECT_FINANCE) && competition.isProcurement()) ||
                (user.hasAnyRoles(Role.APPLICANT, Role.ASSESSOR, Role.MONITORING_OFFICER, Role.STAKEHOLDER, Role.KNOWLEDGE_TRANSFER_ADVISER) && shouldDisplayFeedback(competition, application));
    }

    private boolean userCanViewSupporterFeedback(UserResource user, CompetitionResource competition, ApplicationResource application) {
        return user.hasAnyRoles(Role.KNOWLEDGE_TRANSFER_ADVISER) && shouldDisplaySupporterFeedback(competition, application);
    }

    private boolean shouldDisplayFeedback(CompetitionResource competition, ApplicationResource application) {
        boolean isApplicationAssignedToInterview = interviewAssignmentRestService.isAssignedToInterview(application.getId()).getSuccess();
        boolean feedbackAvailable = competition.getCompetitionStatus().isFeedbackReleased() || isApplicationAssignedToInterview;
        return application.isSubmitted()
                && feedbackAvailable;
    }

    private boolean shouldDisplaySupporterFeedback(CompetitionResource competition, ApplicationResource application) {
        boolean feedbackAvailable = competition.getCompetitionStatus().isFeedbackReleased();
        return competition.isKtp() && application.isSubmitted() && feedbackAvailable;
    }
}
