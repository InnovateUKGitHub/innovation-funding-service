package org.innovateuk.ifs.supporter.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.supporter.domain.SupporterAssignment;
import org.innovateuk.ifs.supporter.domain.SupporterOutcome;
import org.innovateuk.ifs.supporter.mapper.SupporterAssignmentMapper;
import org.innovateuk.ifs.supporter.repository.SupporterAssignmentRepository;
import org.innovateuk.ifs.supporter.resource.*;
import org.innovateuk.ifs.supporter.workflow.SupporterAssignmentWorkflowHandler;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.domain.SimpleOrganisation;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class SupporterAssignmentServiceImpl extends BaseTransactionalService implements SupporterAssignmentService {

    enum Notifications {
        ASSIGN_SUPPORTER,
        REMOVE_SUPPORTER
    }

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Autowired
    private SupporterAssignmentWorkflowHandler supporterAssignmentWorkflowHandler;

    @Autowired
    private SupporterAssignmentRepository supporterAssignmentRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private SupporterAssignmentMapper mapper;

    @Override
    public ServiceResult<SupporterAssignmentResource> getAssignment(long userId, long applicationId) {
        return findSupporterAssignmentByUserAndApplication(userId, applicationId)
                .andOnSuccessReturn(mapper::mapToResource);
    }

    @Override
    public ServiceResult<List<SupporterAssignmentResource>> getAssignmentsByApplicationId(long applicationId) {
        return findSupporterAssignmentsByApplicationId(applicationId)
                .andOnSuccessReturn(assignments -> simpleMap(assignments,mapper::mapToResource));
    }

    @Override
    @Transactional
    public ServiceResult<SupporterAssignmentResource> assign(long userId, long applicationId) {
        boolean exists = supporterAssignmentRepository.existsByParticipantIdAndTargetId(userId, applicationId);
        if (exists) {
            return serviceFailure(SUPPORTER_ASSIGNMENT_ALREADY_EXISTS);
        }
        return doAssign(userId, applicationId);
    }

    private ServiceResult<SupporterAssignmentResource> doAssign(long userId, long applicationId) {
        return find(application(applicationId), user(userId)).andOnSuccess(
                (application, user) -> {
                        if (application.getCompetition().isAssessmentClosed()) {
                            return serviceFailure(SUPPORTER_AFTER_ASSESSMENT_CLOSE);
                        }
                        return serviceSuccess(mapper.mapToResource(supporterAssignmentRepository.save(new SupporterAssignment(application, user))))
                                .andOnSuccess(resource ->
                                        notifyUserAssignedAsSupporter(user, application)
                                                .andOnSuccessReturn(() -> resource)
                                );
                }
        );
    }

    @Override
    @Transactional
    public ServiceResult<Void> assign(List<Long> userIds, long applicationId) {
        List<ServiceResult<SupporterAssignmentResource>> assignmentResults = userIds.stream().map(userId -> {
            boolean exists = supporterAssignmentRepository.existsByParticipantIdAndTargetId(userId, applicationId);
            if (!exists) {
                return doAssign(userId, applicationId);
            }
            return null;
        }).filter(Objects::nonNull)
                .collect(Collectors.toList());
        return aggregate(assignmentResults).andOnSuccessReturnVoid();
    }

    private ServiceResult<Void> notifyUserAssignedAsSupporter(User user, Application application) {
        NotificationTarget recipient = new UserNotificationTarget(user.getName(), user.getEmail());
        Map<String, Object> notificationArguments = new HashMap<>();
        notificationArguments.put("applicationId", application.getId());
        notificationArguments.put("applicationName", application.getName());
        notificationArguments.put("link", format("%s/assessment/supporter/dashboard", webBaseUrl));
        Notification notification = new Notification(systemNotificationSource, recipient, Notifications.ASSIGN_SUPPORTER, notificationArguments);
        return notificationService.sendNotificationWithFlush(notification, NotificationMedium.EMAIL);
    }

    private ServiceResult<Void> notifyUserRemovedAsSupporter(User user, Application application) {
        NotificationTarget recipient = new UserNotificationTarget(user.getName(), user.getEmail());
        Map<String, Object> notificationArguments = new HashMap<>();
        notificationArguments.put("applicationId", application.getId());
        notificationArguments.put("applicationName", application.getName());
        Notification notification = new Notification(systemNotificationSource, recipient, Notifications.REMOVE_SUPPORTER, notificationArguments);
        return notificationService.sendNotificationWithFlush(notification, NotificationMedium.EMAIL);
    }

    @Override
    @Transactional
    public ServiceResult<Void> removeAssignment(long userId, long applicationId) {
        return find(application(applicationId), user(userId)).andOnSuccess(
                (application, user) ->
                        findSupporterAssignmentByUserAndApplication(userId, applicationId)
                                .andOnSuccess((SupporterAssignment assignment) -> {
                                    if (assignment.getTarget().getCompetition().isAssessmentClosed()) {
                                        return serviceFailure(SUPPORTER_AFTER_ASSESSMENT_CLOSE);
                                    }
                                    supporterAssignmentRepository.delete(assignment);
                                    return notifyUserRemovedAsSupporter(user, application);
                                }));
    }

    @Override
    @Transactional
    public ServiceResult<Void> decision(long assignmentId, SupporterDecisionResource decision) {
        return findSupporterAssignmentById(assignmentId).andOnSuccess(assignment -> {
                    if (assignment.getTarget().getCompetition().isAssessmentClosed()) {
                        return serviceFailure(SUPPORTER_AFTER_ASSESSMENT_CLOSE);
                    }
                    SupporterOutcome outcome = new SupporterOutcome(decision.isAccept(), decision.getComments());
                    boolean success;
                    if (decision.isAccept()) {
                        success = supporterAssignmentWorkflowHandler.accept(assignment, outcome);
                    } else {
                        success = supporterAssignmentWorkflowHandler.reject(assignment, outcome);
                    }
                    if (!success) {
                        return serviceFailure(SUPPORTER_WORKFLOW_TRANSITION_FAILURE);
                    }
                    return serviceSuccess();
                }
        );
    }

    @Override
    @Transactional
    public ServiceResult<Void> edit(long assignmentId) {
        return findSupporterAssignmentById(assignmentId).andOnSuccess(assignment -> {
                if (assignment.getTarget().getCompetition().isAssessmentClosed()) {
                    return serviceFailure(SUPPORTER_AFTER_ASSESSMENT_CLOSE);
                }
                    boolean success = supporterAssignmentWorkflowHandler.edit(assignment);
                    if (!success) {
                        return serviceFailure(SUPPORTER_WORKFLOW_TRANSITION_FAILURE);
                    }
                    assignment.setSupporterOutcome(null);
                    return serviceSuccess();
                }
        );
    }

    @Override
    public ServiceResult<ApplicationsForCofundingPageResource> findApplicationsNeedingSupporters(long competitionId, String filter, Pageable pageable) {
        Page<ApplicationsForCofundingResource> result = supporterAssignmentRepository.findApplicationsForCofunding(competitionId, filter, pageable);
        return serviceSuccess(new ApplicationsForCofundingPageResource(
                result.getTotalElements(),
                result.getTotalPages(),
                result.getContent(),
                result.getNumber(),
                result.getSize())
        );
    }

    @Override
    public ServiceResult<SupportersAvailableForApplicationPageResource> findAvailableSupportersForApplication(long applicationId, String filter, Pageable pageable) {
        Page<User> result = supporterAssignmentRepository.findUsersAvailableForCofunding(applicationId, filter, pageable);
        List<SupporterAssignment> assignments = supporterAssignmentRepository.findByTargetId(applicationId);
        return serviceSuccess(new SupportersAvailableForApplicationPageResource(
                result.getTotalElements(),
                result.getTotalPages(),
                result.getContent().stream().map(this::mapToSupporterUser).collect(toList()),
                result.getNumber(),
                result.getSize(),
                assignments.stream().map(SupporterAssignment::getParticipant).map(this::mapToSupporterUser).collect(toList()))
        );
    }

    @Override
    public ServiceResult<List<Long>> findAvailableSupportersUserIdsForApplication(long applicationId, String filter) {
        return serviceSuccess(supporterAssignmentRepository.usersAvailableForCofundingUserIds(applicationId, filter));
    }

    private SupporterUserResource mapToSupporterUser(User user) {
        SupporterUserResource supporterUser = new SupporterUserResource();
        Profile profile = profileRepository.findById(user.getProfileId()).orElseThrow(ObjectNotFoundException::new);
        supporterUser.setUserId(user.getId());
        supporterUser.setEmail(user.getEmail());
        supporterUser.setName(user.getFirstName() + " " + user.getLastName());
        supporterUser.setOrganisation(ofNullable(profile.getSimpleOrganisation()).map(SimpleOrganisation::getName).orElse(null));
        return supporterUser;
    }

    private ServiceResult<SupporterAssignment> findSupporterAssignmentByUserAndApplication(long userId, long applicationId) {
        return find(supporterAssignmentRepository.findByParticipantIdAndTargetId(userId, applicationId), notFoundError(SupporterAssignment.class, userId, applicationId));
    }

    private ServiceResult<List<SupporterAssignment>> findSupporterAssignmentsByApplicationId(long applicationId) {
        return find(supporterAssignmentRepository.findByTargetId(applicationId), notFoundError(SupporterAssignment.class, applicationId));
    }

    private ServiceResult<SupporterAssignment> findSupporterAssignmentById(long assignmentId) {
        return find(supporterAssignmentRepository.findById(assignmentId), notFoundError(SupporterAssignment.class, assignmentId));
    }

}
