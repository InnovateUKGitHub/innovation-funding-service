package org.innovateuk.ifs.interview.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.interview.domain.Interview;
import org.innovateuk.ifs.interview.domain.InterviewParticipant;
import org.innovateuk.ifs.interview.mapper.InterviewMapper;
import org.innovateuk.ifs.interview.repository.InterviewParticipantRepository;
import org.innovateuk.ifs.interview.repository.InterviewRepository;
import org.innovateuk.ifs.interview.resource.*;
import org.innovateuk.ifs.interview.workflow.configuration.InterviewWorkflowHandler;
import org.innovateuk.ifs.invite.resource.AssessorInvitesToSendResource;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer;
import org.innovateuk.ifs.notifications.service.senders.NotificationSender;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.interview.transactional.InterviewAllocationServiceImpl.Notifications.NOTIFY_ASSESSOR_OF_INTERVIEW_ALLOCATIONS;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.innovateuk.ifs.util.StringFunctions.plainTextToHtml;
import static org.innovateuk.ifs.util.StringFunctions.stripHtml;

/**
 * Service for allocating applications to assessors in interview panels
 */
@Service
@Transactional
public class InterviewAllocationServiceImpl implements InterviewAllocationService {

    @Autowired
    private InterviewParticipantRepository interviewParticipantRepository;
    @Autowired
    private InterviewRepository interviewRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompetitionRepository competitionRepository;
    @Autowired
    private SystemNotificationSource systemNotificationSource;
    @Autowired
    private NotificationTemplateRenderer renderer;
    @Autowired
    private InterviewWorkflowHandler workflowHandler;
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private NotificationSender notificationSender;
    @Autowired
    private InterviewMapper interviewMapper;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    enum Notifications {
        NOTIFY_ASSESSOR_OF_INTERVIEW_ALLOCATIONS
    }

    @Override
    public ServiceResult<InterviewAcceptedAssessorsPageResource> getInterviewAcceptedAssessors(long competitionId,
                                                                                               Pageable pageable) {
        Page<InterviewAcceptedAssessorsResource> pagedResult = interviewParticipantRepository.getInterviewAcceptedAssessorsByCompetition(
                competitionId,
                pageable);

        return serviceSuccess(new InterviewAcceptedAssessorsPageResource(
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages(),
                pagedResult.getContent(),
                pagedResult.getNumber(),
                pagedResult.getSize()
        ));
    }

    @Override
    public ServiceResult<InterviewApplicationPageResource> getUnallocatedApplications(long competitionId, long assessorUserId, Pageable pageable) {
        Page<InterviewApplicationResource> pagedResult = interviewRepository.findApplicationsNotAssignedToAssessor(
                competitionId,
                assessorUserId, pageable);

        long unallocatedApplications = interviewRepository.countUnallocatedApplications(competitionId, assessorUserId);
        long allocatedApplications = interviewRepository.countAllocatedApplications(competitionId, assessorUserId);

        return serviceSuccess(new InterviewApplicationPageResource(
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages(),
                pagedResult.getContent(),
                pagedResult.getNumber(),
                pagedResult.getSize(),
                unallocatedApplications,
                allocatedApplications
        ));
    }

    @Override
    public ServiceResult<InterviewApplicationPageResource> getAllocatedApplications(long competitionId, long assessorUserId, Pageable pageable) {
        Page<InterviewApplicationResource> pagedResult = interviewRepository.findApplicationsAssignedToAssessor(
                competitionId,
                assessorUserId, pageable);

        long unallocatedApplications = interviewRepository.countUnallocatedApplications(competitionId, assessorUserId);
        long allocatedApplications = interviewRepository.countAllocatedApplications(competitionId, assessorUserId);

        return serviceSuccess(new InterviewApplicationPageResource(
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages(),
                pagedResult.getContent(),
                pagedResult.getNumber(),
                pagedResult.getSize(),
                unallocatedApplications,
                allocatedApplications
        ));
    }

    @Override
    public ServiceResult<List<InterviewResource>> getAllocatedApplicationsByAssessorId(long competitionId, long assessorUserId) {
        List<Interview> interviews = interviewRepository.findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateAscIdAsc(
                assessorUserId,
                competitionId);

        List<InterviewResource> interviewResources = interviews.stream()
                .map(interview -> interviewMapper.mapToResource(interview))
                .collect(Collectors.toList());

        return serviceSuccess(interviewResources);
    }

    @Override
    public ServiceResult<List<InterviewApplicationResource>> getUnallocatedApplicationsById(List<Long> applicationIds) {
        return serviceSuccess(interviewRepository.findAllNotified(applicationIds));
    }

    @Override
    public ServiceResult<List<Long>> getUnallocatedApplicationIds(long competitionId, long assessorId) {
        return serviceSuccess(interviewRepository.findApplicationIdsNotAssignedToAssessor(competitionId, assessorId));
    }

    @Override
    public ServiceResult<AssessorInvitesToSendResource> getInviteToSend(long competitionId, long assessorId) {
        return getCompetition(competitionId).andOnSuccess(
                competition ->
                    getUser(assessorId).andOnSuccess(
                            user ->
                                    serviceSuccess(
                                            new AssessorInvitesToSendResource(
                                                singletonList(user.getName()),
                                                competition.getId(),
                                                competition.getName(),
                                                getInvitePreviewContent(asMap(
                                                        "name", user.getName(),
                                                        "competitionName", competition.getName()
                                                        )
                                                )
                                            )
                                    )
            )
        );
    }

    @Override
    public ServiceResult<Void> unallocateApplication(long assessorId, long applicationId) {
        interviewRepository.deleteOneByParticipantUserIdAndTargetId(assessorId, applicationId);
        return serviceSuccess();
    }

    @Override
    public ServiceResult<Void> notifyAllocation(InterviewNotifyAllocationResource interviewNotifyAllocationResource) {
        return getInterviewParticipant(interviewNotifyAllocationResource.getAssessorId(), interviewNotifyAllocationResource.getCompetitionId())
                .andOnSuccess(assessor -> createInterviews(interviewNotifyAllocationResource, assessor))
                .andOnSuccess(assessor -> sendAllocationNotification(
                        interviewNotifyAllocationResource.getSubject(),
                        interviewNotifyAllocationResource.getContent(),
                        assessor,
                        NOTIFY_ASSESSOR_OF_INTERVIEW_ALLOCATIONS

                ))
                .andOnSuccessReturnVoid();
    }

    private ServiceResult<InterviewParticipant> createInterviews(InterviewNotifyAllocationResource interviewNotifyAllocationResource, InterviewParticipant interviewParticipant) {
        interviewNotifyAllocationResource.getApplicationIds().forEach(
                applicationId -> getApplication(applicationId)
                        .andOnSuccess(application -> createInterview(application, interviewParticipant))
        );
        return serviceSuccess(interviewParticipant);
    }

    private ServiceResult<Void> createInterview(Application application, InterviewParticipant interviewParticipant) {
        Interview interview = new Interview(application, interviewParticipant);
        workflowHandler.notifyInvitation(interview);
        interviewRepository.save(interview);
        return serviceSuccess();
    }

    private ServiceResult<Competition> getCompetition(long competitionId) {
        return find(competitionRepository.findById(competitionId), notFoundError(Competition.class, competitionId));
    }

    private ServiceResult<User> getUser(long userId) {
        return find(userRepository.findOne(userId), notFoundError(User.class, userId));
    }

    private String getInvitePreviewContent(Map<String, Object> arguments) {
        NotificationTarget notificationTarget = new UserNotificationTarget("", "");
        return renderer.renderTemplate(
                systemNotificationSource,
                notificationTarget,
                "allocate_interview_applications_to_assessor_text.txt",
                arguments
        ).getSuccess();
    }

    private ServiceResult<Application> getApplication(long applicationId) {
        return find(applicationRepository.findOne(applicationId), notFoundError(Application.class, applicationId));
    }

    private ServiceResult<InterviewParticipant> getInterviewParticipant(long userId, long competitionId) {
        return find(interviewParticipantRepository.findByUserIdAndCompetitionIdAndRole(userId, competitionId, CompetitionParticipantRole.INTERVIEW_ASSESSOR), notFoundError(InterviewParticipant.class, userId));
    }

    private ServiceResult<Void> sendAllocationNotification(String subject,
                                                       String customContent,
                                                       InterviewParticipant interviewParticipant,
                                                       Notifications notificationType) {
        String customTextPlain = stripHtml(customContent);
        String customTextHtml = plainTextToHtml(customTextPlain);
        User assessorUser = interviewParticipant.getUser();
        NotificationTarget recipient = new UserNotificationTarget(assessorUser.getName(), assessorUser.getEmail());

        Notification notification = new Notification(
                systemNotificationSource,
                recipient,
                notificationType,
                asMap(
                        "subject", subject,
                        "name", assessorUser.getName(),
                        "competitionName", interviewParticipant.getProcess().getName(),
                        "customTextPlain", customTextPlain,
                        "customTextHtml", customTextHtml
                ));

        return notificationSender.sendNotification(notification).andOnSuccessReturnVoid();
    }
}