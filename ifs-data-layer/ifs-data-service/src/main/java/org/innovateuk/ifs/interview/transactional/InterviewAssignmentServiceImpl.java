package org.innovateuk.ifs.interview.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.interview.domain.InterviewAssignmentMessageOutcome;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentRepository;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;
import org.innovateuk.ifs.interview.workflow.configuration.InterviewAssignmentWorkflowHandler;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer;
import org.innovateuk.ifs.notifications.service.senders.NotificationSender;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.INTERVIEW_PANEL_INVITE_ALREADY_CREATED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.interview.resource.InterviewAssignmentState.AWAITING_FEEDBACK_RESPONSE;
import static org.innovateuk.ifs.interview.resource.InterviewAssignmentState.SUBMITTED_FEEDBACK_RESPONSE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

/**
 * Service for managing {@link InterviewAssignment}s.
 */
@Service
@Transactional
public class InterviewAssignmentServiceImpl implements InterviewAssignmentService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private InterviewAssignmentRepository interviewAssignmentRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private NotificationTemplateRenderer renderer;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private NotificationSender notificationSender;

    @Autowired
    private InterviewAssignmentWorkflowHandler interviewAssignmentWorkflowHandler;

    enum Notifications {
        INVITE_APPLICANT_GROUP_TO_INTERVIEW
    }

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
                interviewAssignmentRepository.findByTargetCompetitionIdAndActivityState(
                        competitionId, InterviewAssignmentState.CREATED, pageable);

        return serviceSuccess(new InterviewAssignmentStagedApplicationPageResource(
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages(),
                simpleMap(pagedResult.getContent(), this::mapToPanelCreatedInviteResource),
                pagedResult.getNumber(),
                pagedResult.getSize()
        ));

    }

    @Override
    @Transactional
    public ServiceResult<InterviewAssignmentApplicationPageResource> getAssignedApplications(long competitionId, Pageable pageable) {

        final Page<InterviewAssignment> pagedResult =
                interviewAssignmentRepository
                        .findByTargetCompetitionIdAndActivityStateNot(competitionId, InterviewAssignmentState.CREATED, pageable);

        return serviceSuccess(new InterviewAssignmentApplicationPageResource(
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages(),
                simpleMap(pagedResult.getContent(), this::mapToPanelResource),
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
        stagedInvites.stream()
                .distinct()
                .map(invite -> getApplication(invite.getApplicationId()))
                .forEach(application -> assignApplicationToCompetition(application.getSuccess()));

        return serviceSuccess();
    }

    @Override
    public ServiceResult<Void> unstageApplication(long applicationId) {
        interviewAssignmentRepository.deleteByTargetIdAndActivityState(applicationId, InterviewAssignmentState.CREATED);
        return serviceSuccess();
    }

    @Override
    public ServiceResult<Void> unstageApplications(long competitionId) {
        interviewAssignmentRepository.deleteByTargetCompetitionIdAndActivityState(competitionId, InterviewAssignmentState.CREATED);
        return serviceSuccess();
    }

    public ServiceResult<ApplicantInterviewInviteResource> getEmailTemplate() {
        NotificationTarget notificationTarget = new UserNotificationTarget("", "");

        return renderer.renderTemplate(systemNotificationSource, notificationTarget, "invite_applicants_to_interview_panel_text.txt",
                Collections.emptyMap()).andOnSuccessReturn(content -> new ApplicantInterviewInviteResource(content));
    }

    @Override
    public ServiceResult<Void> sendInvites(long competitionId, AssessorInviteSendResource assessorInviteSendResource) {
        List<InterviewAssignment> interviewAssignments =
                interviewAssignmentRepository.findByTargetCompetitionIdAndActivityState(competitionId, InterviewAssignmentState.CREATED);

        ServiceResult<Void> result = serviceSuccess();
        for (InterviewAssignment assignment : interviewAssignments) {
            if (result.isSuccess()) {
                result = sendInvite(assessorInviteSendResource, assignment);
            }
        }

        return result;
    }

    @Override
    public ServiceResult<Boolean> isApplicationAssigned(long applicationId) {
        return serviceSuccess(interviewAssignmentRepository.existsByTargetIdAndActivityStateIn(applicationId,
                asList(AWAITING_FEEDBACK_RESPONSE,
                        SUBMITTED_FEEDBACK_RESPONSE)));
    }

    private ServiceResult<Void> sendInvite(AssessorInviteSendResource assessorInviteSendResource, InterviewAssignment assignment) {
        User user = assignment.getParticipant().getUser();
        NotificationTarget recipient = new UserNotificationTarget(user.getName(), user.getEmail());
        Notification notification = new Notification(
                systemNotificationSource,
                recipient,
                Notifications.INVITE_APPLICANT_GROUP_TO_INTERVIEW,
                asMap(
                        "subject", assessorInviteSendResource.getSubject(),
                        "name", user.getName(),
                        "competitionName", assignment.getTarget().getCompetition().getName(),
                        "applicationId", assignment.getTarget().getId(),
                        "applicationTitle", assignment.getTarget().getName(),
                        "message", assessorInviteSendResource.getContent()
                ));

        return notificationSender.sendNotification(notification).andOnSuccessReturnVoid(() -> {
            InterviewAssignmentMessageOutcome outcome = new InterviewAssignmentMessageOutcome();
            outcome.setAssessmentInterviewPanel(assignment);
            outcome.setMessage(assessorInviteSendResource.getContent());
            outcome.setSubject(assessorInviteSendResource.getSubject());
            interviewAssignmentWorkflowHandler.notifyInterviewPanel(assignment, outcome);
        });
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

    private InterviewAssignmentApplicationResource mapToPanelResource(InterviewAssignment panelInvite) {
        final Application application = panelInvite.getTarget();

        return getOrganisation(panelInvite.getParticipant().getOrganisationId())
                .andOnSuccessReturn(leadOrganisation ->
                        new InterviewAssignmentApplicationResource(
                                panelInvite.getId(),
                                application.getId(),
                                application.getName(),
                                leadOrganisation.getName(),
                                panelInvite.getActivityState()
                        )
                ).getSuccess();
    }

    private ServiceResult<Organisation> getOrganisation(long organisationId) {
        return find(organisationRepository.findOne(organisationId), notFoundError(Organisation.class, organisationId));
    }

    private ServiceResult<InterviewAssignment> assignApplicationToCompetition(Application application) {
        if (!interviewAssignmentRepository.existsByTargetIdAndActivityStateIn(application.getId(), singletonList(InterviewAssignmentState.CREATED))) {
            final ProcessRole pr = new ProcessRole(application.getLeadApplicant(), application.getId(), Role.INTERVIEW_LEAD_APPLICANT, application.getLeadOrganisationId());
            final InterviewAssignment panel = new InterviewAssignment(application, pr);

            interviewAssignmentRepository.save(panel);

            return serviceSuccess(panel);
        }

        return serviceFailure(INTERVIEW_PANEL_INVITE_ALREADY_CREATED);
    }
}