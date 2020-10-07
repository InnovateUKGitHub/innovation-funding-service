package org.innovateuk.ifs.cofunder.transactional;

import org.innovateuk.ifs.cofunder.domain.CofunderAssignment;
import org.innovateuk.ifs.cofunder.domain.CofunderOutcome;
import org.innovateuk.ifs.cofunder.mapper.CofunderAssignmentMapper;
import org.innovateuk.ifs.cofunder.repository.CofunderAssignmentRepository;
import org.innovateuk.ifs.cofunder.resource.*;
import org.innovateuk.ifs.cofunder.workflow.CofunderAssignmentWorkflowHandler;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.domain.SimpleOrganisation;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COFUNDER_ASSIGNMENT_ALREADY_EXISTS;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COFUNDER_WORKFLOW_TRANSITION_FAILURE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class CofunderAssignmentServiceImpl extends BaseTransactionalService implements CofunderAssignmentService {

    @Autowired
    private CofunderAssignmentWorkflowHandler cofunderAssignmentWorkflowHandler;

    @Autowired
    private CofunderAssignmentRepository cofunderAssignmentRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private CofunderAssignmentMapper mapper;

    @Override
    public ServiceResult<CofunderAssignmentResource> getAssignment(long userId, long applicationId) {
        return findCofunderAssignmentByUserAndApplication(userId, applicationId)
                .andOnSuccessReturn(mapper::mapToResource);
    }

    @Override
    public ServiceResult<List<CofunderAssignmentResource>> getAssignmentsByApplicationId(long applicationId) {
        return findCofunderAssignmentsByApplicationId(applicationId)
                .andOnSuccessReturn(assignments -> simpleMap(assignments,mapper::mapToResource));
    }

    @Override
    @Transactional
    public ServiceResult<CofunderAssignmentResource> assign(long userId, long applicationId) {
        boolean exists = cofunderAssignmentRepository.existsByParticipantIdAndTargetId(userId, applicationId);
        if (exists) {
            return serviceFailure(COFUNDER_ASSIGNMENT_ALREADY_EXISTS);
        }
        return find(application(applicationId), user(userId)).andOnSuccess(
                (application, user) ->
                        serviceSuccess(mapper.mapToResource(cofunderAssignmentRepository.save(new CofunderAssignment(application, user))))
        );
    }

    @Override
    @Transactional
    public ServiceResult<Void> removeAssignment(long userId, long applicationId) {
        return findCofunderAssignmentByUserAndApplication(userId, applicationId).andOnSuccessReturnVoid(assignment ->
            cofunderAssignmentRepository.delete(assignment)
        );
    }

    @Override
    @Transactional
    public ServiceResult<Void> decision(long assignmentId, CofunderDecisionResource decision) {
        return findCofunderAssignmentById(assignmentId).andOnSuccess(assignment -> {
                    CofunderOutcome outcome = new CofunderOutcome(decision.isAccept(), decision.getComments());
                    boolean success;
                    if (decision.isAccept()) {
                        success = cofunderAssignmentWorkflowHandler.accept(assignment, outcome);
                    } else {
                        success = cofunderAssignmentWorkflowHandler.reject(assignment, outcome);
                    }
                    if (!success) {
                        return serviceFailure(COFUNDER_WORKFLOW_TRANSITION_FAILURE);
                    }
                    return serviceSuccess();
                }
        );
    }

    @Override
    @Transactional
    public ServiceResult<Void> edit(long assignmentId) {
        return findCofunderAssignmentById(assignmentId).andOnSuccess(assignment -> {
                    boolean success = cofunderAssignmentWorkflowHandler.edit(assignment);
                    if (!success) {
                        return serviceFailure(COFUNDER_WORKFLOW_TRANSITION_FAILURE);
                    }
                    assignment.setCofunderOutcome(null);
                    return serviceSuccess();
                }
        );
    }

    @Override
    public ServiceResult<ApplicationsForCofundingPageResource> findApplicationsNeedingCofunders(long competitionId, String filter, Pageable pageable) {
        Page<ApplicationsForCofundingResource> result = cofunderAssignmentRepository.findApplicationsForCofunding(competitionId, filter, pageable);
        return serviceSuccess(new ApplicationsForCofundingPageResource(
                result.getTotalElements(),
                result.getTotalPages(),
                result.getContent(),
                result.getNumber(),
                result.getSize())
        );
    }

    @Override
    public ServiceResult<CofundersAvailableForApplicationPageResource> findAvailableCofundersForApplication(long applicationId, String filter, Pageable pageable) {
        Page<User> result = cofunderAssignmentRepository.findUsersAvailableForCofunding(applicationId, filter, pageable);
        List<CofunderAssignment> assignments = cofunderAssignmentRepository.findByTargetId(applicationId);
        return serviceSuccess(new CofundersAvailableForApplicationPageResource(
                result.getTotalElements(),
                result.getTotalPages(),
                result.getContent().stream().map(this::mapToCofunderUser).collect(toList()),
                result.getNumber(),
                result.getSize(),
                assignments.stream().map(CofunderAssignment::getParticipant).map(this::mapToCofunderUser).collect(toList()))
        );
    }

    private CofunderUserResource mapToCofunderUser(User user) {
        CofunderUserResource cofunderUser = new CofunderUserResource();
        Profile profile = profileRepository.findById(user.getProfileId()).orElseThrow(ObjectNotFoundException::new);
        cofunderUser.setUserId(user.getId());
        cofunderUser.setEmail(user.getEmail());
        cofunderUser.setName(user.getFirstName() + " " + user.getLastName());
        cofunderUser.setOrganisation(ofNullable(profile.getSimpleOrganisation()).map(SimpleOrganisation::getName).orElse(null));
        return cofunderUser;
    }

    private ServiceResult<CofunderAssignment> findCofunderAssignmentByUserAndApplication(long userId, long applicationId) {
        return find(cofunderAssignmentRepository.findByParticipantIdAndTargetId(userId, applicationId), notFoundError(CofunderAssignment.class, userId, applicationId));
    }

    private ServiceResult<List<CofunderAssignment>> findCofunderAssignmentsByApplicationId(long applicationId) {
        return find(cofunderAssignmentRepository.findByTargetId(applicationId), notFoundError(CofunderAssignment.class, applicationId));
    }

    private ServiceResult<CofunderAssignment> findCofunderAssignmentById(long assignmentId) {
        return find(cofunderAssignmentRepository.findById(assignmentId), notFoundError(CofunderAssignment.class, assignmentId));
    }

}
