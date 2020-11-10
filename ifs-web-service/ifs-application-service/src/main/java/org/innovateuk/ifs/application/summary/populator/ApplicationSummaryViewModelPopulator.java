package org.innovateuk.ifs.application.summary.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.populator.ApplicationReadOnlyViewModelPopulator;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationSummaryViewModel;
import org.innovateuk.ifs.application.summary.viewmodel.InterviewFeedbackViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings.defaultSettings;

@Component
public class ApplicationSummaryViewModelPopulator {

    @Autowired
    private ApplicationReadOnlyViewModelPopulator applicationReadOnlyViewModelPopulator;

    @Autowired
    private InterviewAssignmentRestService interviewAssignmentRestService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private InterviewFeedbackViewModelPopulator interviewFeedbackViewModelPopulator;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private UserRestService userRestService;

    public ApplicationSummaryViewModel populate(ApplicationResource application, CompetitionResource competition, UserResource user) {
        ApplicationReadOnlySettings settings = defaultSettings()
                .setIncludeAllAssessorFeedback(shouldDisplayFeedback(competition, application, user))
                .setIncludeAllSupporterFeedback(shouldDisplaySupporterFeedback(competition, application, user));
        ApplicationReadOnlyViewModel applicationReadOnlyViewModel = applicationReadOnlyViewModelPopulator.populate(application, competition, user, settings);

        final InterviewFeedbackViewModel interviewFeedbackViewModel;
        if (interviewAssignmentRestService.isAssignedToInterview(application.getId()).getSuccess()) {
            interviewFeedbackViewModel = interviewFeedbackViewModelPopulator.populate(application.getId(), application.getCompetitionName(), user, application.getCompetitionStatus().isFeedbackReleased());
        } else {
            interviewFeedbackViewModel = null;
        }

        OrganisationResource leadOrganisation = organisationRestService.getOrganisationById(application.getLeadOrganisationId()).getSuccess();
        List<ProcessRoleResource> processRoleResources = userRestService.findProcessRole(application.getId()).getSuccess();
        List<OrganisationResource> collaboratorOrganisations = processRoleResources.stream()
                .filter(pr -> Role.COLLABORATOR == pr.getRole())
                .map(pr -> pr.getOrganisationId())
                .distinct()
                .map(orgId -> organisationRestService.getOrganisationById(orgId).getSuccess())
                .collect(Collectors.toList());

        return new ApplicationSummaryViewModel(applicationReadOnlyViewModel,
                                               application,
                                               competition,
                                               leadOrganisation,
                                               collaboratorOrganisations,
                                               isProjectWithdrawn(application.getId()),
                                               interviewFeedbackViewModel);
    }

    private boolean shouldDisplayFeedback(CompetitionResource competition, ApplicationResource application, UserResource user) {
        boolean feedbackReleased = competition.getCompetitionStatus().isFeedbackReleased();
        if (competition.isKtp()) {
            return user.hasAnyRoles(Role.KNOWLEDGE_TRANSFER_ADVISER) && feedbackReleased;
        }
        boolean isApplicationAssignedToInterview = interviewAssignmentRestService.isAssignedToInterview(application.getId()).getSuccess();
        boolean feedbackAvailable = feedbackReleased || isApplicationAssignedToInterview;
        return application.isSubmitted() && feedbackAvailable;
    }

    private boolean shouldDisplaySupporterFeedback(CompetitionResource competition, ApplicationResource application, UserResource user) {
        boolean feedbackReleased = competition.getCompetitionStatus().isFeedbackReleased();
        return competition.isKtp() && user.hasAnyRoles(Role.KNOWLEDGE_TRANSFER_ADVISER) && application.isSubmitted() && feedbackReleased;
    }

    private boolean isProjectWithdrawn(Long applicationId) {
        ProjectResource project = projectService.getByApplicationId(applicationId);
        return project != null && project.isWithdrawn();
    }
}
