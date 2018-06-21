package org.innovateuk.ifs.interview.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.interview.domain.InterviewAssignmentMessageOutcome;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentRepository;
import org.innovateuk.ifs.interview.resource.InterviewApplicationSentInviteResource;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;
import org.innovateuk.ifs.interview.workflow.configuration.InterviewAssignmentWorkflowHandler;
import org.innovateuk.ifs.invite.resource.ApplicantInterviewInviteResource;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer.PREVIEW_TEMPLATES_PATH;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

/**
 * Service for inviting applicants to interview panels.
 */
@Service
@Transactional
public class InterviewApplicationInviteServiceImpl implements InterviewApplicationInviteService {

    @Autowired
    private InterviewAssignmentRepository interviewAssignmentRepository;

    @Autowired
    private NotificationTemplateRenderer renderer;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private InterviewAssignmentWorkflowHandler interviewAssignmentWorkflowHandler;

    enum Notifications {
        INVITE_APPLICANT_GROUP_TO_INTERVIEW
    }

    @Override
    public ServiceResult<ApplicantInterviewInviteResource> getEmailTemplate() {
        NotificationTarget notificationTarget = new UserNotificationTarget("", "");

        return renderer.renderTemplate(systemNotificationSource, notificationTarget, PREVIEW_TEMPLATES_PATH + "invite_applicants_to_interview_panel_text.txt",
                Collections.emptyMap()).andOnSuccessReturn(content -> new ApplicantInterviewInviteResource(content));
    }

    @Override
    public ServiceResult<Void> sendInvites(long competitionId, AssessorInviteSendResource assessorInviteSendResource) {
        List<InterviewAssignment> interviewAssignments = interviewAssignmentRepository.findByTargetCompetitionIdAndActivityState(
                competitionId, InterviewAssignmentState.CREATED);

        // TODO DW - below code needs a rewrite.  Do all MySQL updates first and then send out notifications
        ServiceResult<Void> result = serviceSuccess();
        for (InterviewAssignment assignment : interviewAssignments) {
            if (result.isSuccess()) {
                result = sendInvite(assessorInviteSendResource, assignment);
            }
        }
        return result;
    }

    @Override
    public ServiceResult<InterviewApplicationSentInviteResource> getSentInvite(long applicationId) {
        InterviewAssignment assignment = interviewAssignmentRepository.findOneByTargetId(applicationId);
        return ofNullable(assignment.getMessage())
                .map(message ->
                        serviceSuccess(
                                new InterviewApplicationSentInviteResource(message.getSubject(), message.getMessage(), message.getCreatedOn())
                        )
                )
                .orElse(serviceFailure(GENERAL_NOT_FOUND));
    }

    @Override
    public ServiceResult<Void> resendInvite(long applicationId, AssessorInviteSendResource assessorInviteSendResource) {
        InterviewAssignment assignment = interviewAssignmentRepository.findOneByTargetId(applicationId);
        return sendInvite(assessorInviteSendResource, assignment);
    }

    private ServiceResult<Void> sendInvite(AssessorInviteSendResource assessorInviteSendResource, InterviewAssignment assignment) {

        InterviewAssignmentMessageOutcome outcome;
        if (assignment.getMessage() == null) {
            outcome = new InterviewAssignmentMessageOutcome();
            outcome.setAssessmentInterviewPanel(assignment);
        } else {
            outcome = assignment.getMessage();
        }
        outcome.setMessage(assessorInviteSendResource.getContent());
        outcome.setSubject(assessorInviteSendResource.getSubject());
        interviewAssignmentWorkflowHandler.notifyInterviewPanel(assignment, outcome);

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

        return notificationService.sendNotificationWithFlush(notification, EMAIL);
    }
}