package org.innovateuk.ifs.interview.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.interview.domain.InterviewAssignmentMessageOutcome;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentRepository;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;
import org.innovateuk.ifs.interview.workflow.configuration.InterviewAssignmentWorkflowHandler;
import org.innovateuk.ifs.invite.resource.ApplicantInterviewInviteResource;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer;
import org.innovateuk.ifs.notifications.service.senders.NotificationSender;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

/**
 * Service for managing {@link InterviewAssignment}s.
 */
@Service
@Transactional
public class InterviewApplicationInviteServiceImpl implements InterviewApplicationInviteService {

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Autowired
    private InterviewAssignmentRepository interviewAssignmentRepository;

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
    public ServiceResult<ApplicantInterviewInviteResource> getEmailTemplate() {
        NotificationTarget notificationTarget = new UserNotificationTarget("", "");

        return renderer.renderTemplate(systemNotificationSource, notificationTarget, "invite_applicants_to_interview_panel_text.txt",
                Collections.emptyMap()).andOnSuccessReturn(content -> new ApplicantInterviewInviteResource(content));
    }

    @Override
    public ServiceResult<Void> sendInvites(long competitionId, AssessorInviteSendResource assessorInviteSendResource) {
        List<InterviewAssignment> interviewAssignments = interviewAssignmentRepository.findByTargetCompetitionIdAndActivityStateState(
                competitionId, InterviewAssignmentState.CREATED.getBackingState());

        final ActivityState awaitingFeedbackActivityState = activityStateRepository.findOneByActivityTypeAndState(ActivityType.ASSESSMENT_INTERVIEW_PANEL, InterviewAssignmentState.AWAITING_FEEDBACK_RESPONSE.getBackingState());

        ServiceResult<Void> result = serviceSuccess();
        for (InterviewAssignment assignment : interviewAssignments) {
            if (result.isSuccess()) {
                result = sendInvite(assessorInviteSendResource, assignment, awaitingFeedbackActivityState);
            }
        }

        return result;
    }

    private ServiceResult<Void> sendInvite(AssessorInviteSendResource assessorInviteSendResource, InterviewAssignment assignment, ActivityState awaitingFeedbackActivityState) {
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
        });
    }
}