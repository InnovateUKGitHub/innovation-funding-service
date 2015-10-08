package com.worth.ifs.commons.service;

import com.worth.ifs.application.domain.AssessorFeedback;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.repository.ResponseRepository;
import com.worth.ifs.service.AssessorService;
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

import static com.worth.ifs.commons.service.ServiceFailure.error;
import static com.worth.ifs.service.AssessorServiceImpl.Failures.*;
import static com.worth.ifs.util.Either.left;
import static com.worth.ifs.util.Either.right;

/**
 * Created by dwatson on 06/10/15.
 */
@Service
public abstract class BaseTransactionalService  {

    private static final Log log = LogFactory.getLog(BaseTransactionalService.class);

    public enum Failures {
        UNEXPECTED_ERROR, //
        RESPONSE_NOT_FOUND, //
        PROCESS_ROLE_NOT_FOUND, //
        PROCESS_ROLE_INCORRECT_TYPE, //
        PROCESS_ROLE_INCORRECT_APPLICATION, //
    }

    @Autowired
    protected ResponseRepository responseRepository;

    @Autowired
    protected ProcessRoleRepository processRoleRepository;

    protected <T> Either<ServiceFailure, T> handlingErrors(Supplier<Either<ServiceFailure, T>> serviceCode) {
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

    protected Either<ServiceFailure, Response> getResponse(Long responseId) {
        return Optional.ofNullable(responseRepository.findOne(responseId)).map(BaseTransactionalService::successResponse)
                .orElse(errorResponse(RESPONSE_NOT_FOUND));
    };

    protected Either<ServiceFailure, ProcessRole> getProcessRole(Long processRoleId) {
        return Optional.ofNullable(processRoleRepository.findOne(processRoleId)).map(BaseTransactionalService::successResponse)
                .orElse(errorResponse(PROCESS_ROLE_NOT_FOUND));
    };

    protected static <T> Either<ServiceFailure, T> successResponse(T response) {
        return Either.<ServiceFailure, T> right(response);
    }
    
    protected static <T> Either<ServiceFailure, T> errorResponse(Enum<?> error) {
        return left(error(error));
    }

}
