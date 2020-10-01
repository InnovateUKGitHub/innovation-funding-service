package org.innovateuk.ifs.cofunder.transactional;

import org.innovateuk.ifs.cofunder.domain.CofunderAssignment;
import org.innovateuk.ifs.cofunder.domain.CofunderOutcome;
import org.innovateuk.ifs.cofunder.repository.CofunderAssignmentRepository;
import org.innovateuk.ifs.cofunder.resource.ApplicationsForCofundingPageResource;
import org.innovateuk.ifs.cofunder.resource.CofunderAssignmentResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDecisionResource;
import org.innovateuk.ifs.cofunder.resource.CofundersAvailableForApplicationPageResource;
import org.innovateuk.ifs.cofunder.workflow.CofunderAssignmentWorkflowHandler;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Optional.of;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COFUNDER_ASSIGNMENT_ALREADY_EXISTS;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COFUNDER_WORKFLOW_TRANSITION_FAILURE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class CofunderAssignmentServiceImpl extends BaseTransactionalService implements CofunderAssignmentService {

    @Autowired
    private CofunderAssignmentWorkflowHandler cofunderAssignmentWorkflowHandler;

    @Autowired
    private CofunderAssignmentRepository cofunderAssignmentRepository;

    @Override
    public ServiceResult<CofunderAssignmentResource> getAssignment(long userId, long applicationId) {
        return findCofunderAssignmentByUserAndApplication(userId, applicationId)
                .andOnSuccessReturn(this::map);
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
                        serviceSuccess(map(cofunderAssignmentRepository.save(new CofunderAssignment(application, user))))
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
    public ServiceResult<ApplicationsForCofundingPageResource> findApplicationsNeedingCofunders(long competitionId) {
        return null;
    }

    @Override
    public ServiceResult<CofundersAvailableForApplicationPageResource> findAvailableCofudersForApplication(long applicationId) {
        return null;
    }

    private ServiceResult<CofunderAssignment> findCofunderAssignmentByUserAndApplication(long userId, long applicationId) {
        return find(cofunderAssignmentRepository.findByParticipantIdAndTargetId(userId, applicationId), notFoundError(CofunderAssignment.class, userId, applicationId));
    }

    private ServiceResult<CofunderAssignment> findCofunderAssignmentById(long assignmentId) {
        return find(cofunderAssignmentRepository.findById(assignmentId), notFoundError(CofunderAssignment.class, assignmentId));
    }

    private CofunderAssignmentResource map(CofunderAssignment assignment) {
        CofunderAssignmentResource resource = new CofunderAssignmentResource();
        //todo replace with mapstruct?
        resource.setAssignmentId(assignment.getId());
        resource.setComments(of(assignment.getCofunderOutcome()).map(CofunderOutcome::getComment).orElse(null));
        resource.setState(assignment.getProcessState());
        return resource;
    }



}
