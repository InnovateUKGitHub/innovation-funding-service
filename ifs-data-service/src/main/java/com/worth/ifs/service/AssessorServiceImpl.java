package com.worth.ifs.service;

import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.domain.AssessorFeedback;
import com.worth.ifs.application.repository.ResponseRepository;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.util.Either;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static com.worth.ifs.service.AssessorServiceImpl.Failures.*;
import static com.worth.ifs.service.ServiceFailure.error;
import static com.worth.ifs.util.Either.left;
import static com.worth.ifs.util.Either.right;

/**
 * Created by dwatson on 06/10/15.
 */
@Service
public class AssessorServiceImpl implements AssessorService {

    private static final Log log = LogFactory.getLog(AssessorServiceImpl.class);

    public enum Failures {
        UNEXPECTED_ERROR, //
        RESPONSE_NOT_FOUND, //
        PROCESS_ROLE_NOT_FOUND, //
        PROCESS_ROLE_INCORRECT_TYPE, //
        PROCESS_ROLE_INCORRECT_APPLICATION, //
    }

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Override
    public Either<ServiceFailure, ServiceSuccess> updateAssessorFeedback(Long responseId, Long assessorProcessRoleId, Optional<String> feedbackValue, Optional<String> feedbackText) {

        BiFunction<ProcessRole, Response, Either<ServiceFailure, ServiceSuccess>> process = (role, response) -> {
            AssessorFeedback responseFeedback = response.getOrCreateResponseAssessorFeedback(role);
            responseFeedback.setAssessmentValue(feedbackValue.orElse(null));
            responseFeedback.setAssessmentFeedback(feedbackText.orElse(null));
            responseRepository.save(response);
            return right(new ServiceSuccess());
        };

        return handlingErrors(() -> {
            return getResponse(responseId).andThen(response -> {
                return getProcessRole(assessorProcessRoleId).andThen(processRole -> {
                    return validateProcessRoleCorrectType(processRole, UserRoleType.ASSESSOR).andThen(assessorRole -> {
                        return validateProcessRoleInApplication(response, processRole).andThen(roleInApplication -> {
                            return process.apply(assessorRole, response);
                        });
                    });
                });
            });
        });
    }

    private <T> Either<ServiceFailure, T> handlingErrors(Supplier<Either<ServiceFailure, T>> serviceCode) {
        try {
            Either<ServiceFailure, T> response = serviceCode.get();

            if (response.isLeft()) {
                log.debug("Service failure encountered - performing transaction rollback");
                rollbackTransaction();
            }
            return response;
        } catch (Exception e) {
            log.warn("Uncaught exception encountered while performing service call.  Performing transaction rollback and returning ServiceFailure", e);
            rollbackTransaction();
            return errorResponse(UNEXPECTED_ERROR);
        }
    }

    private void rollbackTransaction() {
        try {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (NoTransactionException e) {
            log.trace("No transaction to roll back");
        }
    }

    private Either<ServiceFailure, Response> getResponse(Long responseId) {
        return Optional.ofNullable(responseRepository.findOne(responseId)).map(AssessorServiceImpl::successResponse)
                .orElse(errorResponse(RESPONSE_NOT_FOUND));
    };

    private Either<ServiceFailure, ProcessRole> getProcessRole(Long processRoleId) {
        return Optional.ofNullable(processRoleRepository.findOne(processRoleId)).map(AssessorServiceImpl::successResponse)
                .orElse(errorResponse(PROCESS_ROLE_NOT_FOUND));
    };

    private Either<ServiceFailure, ProcessRole> validateProcessRoleInApplication(Response response, ProcessRole processRole) {
        return response.getApplication().getId().equals(processRole.getApplication().getId()) ? successResponse(processRole) : errorResponse(PROCESS_ROLE_INCORRECT_APPLICATION);
    };

    private Either<ServiceFailure, ProcessRole> validateProcessRoleCorrectType(ProcessRole processRole, UserRoleType type) {
        return processRole.getRole().getName().equals(type.getName()) ? successResponse(processRole) : errorResponse(PROCESS_ROLE_INCORRECT_TYPE);
    };

    private static <T> Either<ServiceFailure, T> successResponse(T response) {
        return Either.<ServiceFailure, T> right(response);
    }
    
    private static <T> Either<ServiceFailure, T> errorResponse(Enum<?> error) {
        return left(error(error));
    }

}
