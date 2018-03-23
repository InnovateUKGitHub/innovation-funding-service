package org.innovateuk.ifs.interview.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentRepository;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.notifications.resource.ExternalUserNotificationTarget;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Service for managing {@link InterviewAssignment}s.
 */
@Service
@Transactional
public class InterviewAssignmentInviteServiceImpl implements InterviewAssignmentInviteService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Autowired
    private InterviewAssignmentRepository interviewAssignmentRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private NotificationTemplateRenderer renderer;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Override
    public ServiceResult<AvailableApplicationPageResource> getAvailableApplications(long competitionId, Pageable pageable) {

            final Page<Application> pagedResult =
                    applicationRepository.findSubmittedApplicationsNotOnInterviewPanel(competitionId, pageable);

            return serviceSuccess(new AvailableApplicationPageResource(
                    pagedResult.getTotalElements(),
                    pagedResult.getTotalPages(),
                    simpleMap(pagedResult.getContent(), this::mapToAvailableApplicationResource),
                    pagedResult.getNumber(),
                    pagedResult.getSize()
            ));
        }

    @Override
    @Transactional
    public ServiceResult<InterviewAssignmentStagedApplicationPageResource> getStagedApplications(long competitionId, Pageable pageable) {
        final Page<InterviewAssignment> pagedResult =
                interviewAssignmentRepository.findByTargetCompetitionIdAndActivityStateState(
                        competitionId, InterviewAssignmentState.CREATED.getBackingState(), pageable);

        return serviceSuccess(new InterviewAssignmentStagedApplicationPageResource(
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages(),
                simpleMap(pagedResult.getContent(), this::mapToPanelCreatedInviteResource),
                pagedResult.getNumber(),
                pagedResult.getSize()
        ));

    }

    @Override
    public ServiceResult<List<Long>> getAvailableApplicationIds(long competitionId) {
        return serviceSuccess(
                simpleMap(
                        applicationRepository.findSubmittedApplicationsNotOnInterviewPanel(competitionId),
                        Application::getId
                )
        );
    }

    @Override
    public ServiceResult<Void> assignApplications(List<StagedApplicationResource> stagedInvites) {
        stagedInvites.forEach(invite -> getApplication(invite.getApplicationId()).andOnSuccess(this::assignApplicationToCompetition));
        return serviceSuccess();
    }

    @Override
    public ServiceResult<ApplicantInterviewInviteResource> getEmailTemplate() {
        NotificationTarget notificationTarget = new ExternalUserNotificationTarget("", "");

        return renderer.renderTemplate(systemNotificationSource, notificationTarget, "invite_applicants_to_interview_panel_text.txt",
                Collections.emptyMap()).andOnSuccessReturn(content -> new ApplicantInterviewInviteResource(content));
    }

    @Override
    public ServiceResult<Void> sendInvites(AssessorInviteSendResource assessorInviteSendResource) {
        return serviceFailure(CommonFailureKeys.ADMIN_INVALID_USER_ROLE);
    }

    private ServiceResult<Application> getApplication(long applicationId) {
        return find(applicationRepository.findOne(applicationId), notFoundError(Application.class, applicationId));
    }

    private AvailableApplicationResource mapToAvailableApplicationResource(Application application) {
        return getOrganisation(application.getLeadOrganisationId())
                .andOnSuccessReturn(
                        leadOrganisation ->
                                new AvailableApplicationResource(application.getId(), application.getName(), leadOrganisation.getName()
                        )
                ).getSuccess();
    }

    private InterviewAssignmentStagedApplicationResource mapToPanelCreatedInviteResource(InterviewAssignment panelInvite) {
        final Application application = panelInvite.getTarget();

        return getOrganisation(panelInvite.getParticipant().getOrganisationId())
                .andOnSuccessReturn(leadOrganisation ->
                        new InterviewAssignmentStagedApplicationResource(
                                panelInvite.getId(),
                                application.getId(),
                                application.getName(),
                                leadOrganisation.getName()
                        )
                ).getSuccess();
    }

    private ServiceResult<Organisation> getOrganisation(long organisationId) {
        return find(organisationRepository.findOne(organisationId), notFoundError(Organisation.class, organisationId));
    }

    private ServiceResult<InterviewAssignment> assignApplicationToCompetition(Application application) {
        final Role leadApplicantRole = Role.getByName(UserRoleType.INTERVIEW_LEAD_APPLICANT.getName());
        final ActivityState createdActivityState = activityStateRepository.findOneByActivityTypeAndState(ActivityType.ASSESSMENT_INTERVIEW_PANEL, InterviewAssignmentState.CREATED.getBackingState());
        final ProcessRole pr = new ProcessRole(application.getLeadApplicant(), application.getId(), leadApplicantRole, application.getLeadOrganisationId());
        final InterviewAssignment panel = new InterviewAssignment(application, pr, createdActivityState);

        interviewAssignmentRepository.save(panel);

        return serviceSuccess(panel);
    }
}