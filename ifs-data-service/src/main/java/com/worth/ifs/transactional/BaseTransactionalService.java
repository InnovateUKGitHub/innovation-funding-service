package com.worth.ifs.transactional;

import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.repository.ApplicationStatusRepository;
import com.worth.ifs.application.repository.ResponseRepository;
import com.worth.ifs.competition.repository.CompetitionsRepository;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.repository.UserRepository;
import com.worth.ifs.util.Either;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.function.Supplier;

import static com.worth.ifs.assessment.transactional.AssessorServiceImpl.ServiceFailures.*;
import static com.worth.ifs.transactional.ServiceFailure.error;
import static com.worth.ifs.util.Either.left;
import static com.worth.ifs.util.EntityLookupCallbacks.getProcessRoleById;
import static com.worth.ifs.util.EntityLookupCallbacks.getResponseById;

/**
 * This class represents the base class for transactional services.  Method calls within this service will have
 * transaction boundaries provided to allow for safe atomic operations and persistence cascading.  Code called
 * within a {@link #handlingErrors(Supplier)} supplier will have its exceptions converted into ServiceFailures
 * of type UNEXPECTED_ERROR and the transaction rolled back.
 *
 * Created by dwatson on 06/10/15.
 */
public abstract class BaseTransactionalService  {

    private static final Log log = LogFactory.getLog(BaseTransactionalService.class);

    public enum Failures {
        UNEXPECTED_ERROR, //
        RESPONSE_NOT_FOUND, //
        FORM_INPUT_RESPONSE_NOT_FOUND, //
        PROCESS_ROLE_NOT_FOUND, //
        PROCESS_ROLE_INCORRECT_TYPE, //
        PROCESS_ROLE_INCORRECT_APPLICATION, //
    }

    @Autowired
    protected ResponseRepository responseRepository;

    @Autowired
    protected ProcessRoleRepository processRoleRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ApplicationStatusRepository applicationStatusRepository;

    @Autowired
    protected RoleRepository roleRepository;

    @Autowired
    protected CompetitionsRepository competitionRepository;

    @Autowired
    protected ApplicationRepository applicationRepository;

    /**
     * This wrapper wraps the serviceCode function and rolls back transactions upon receiving a ServiceFailure
     * response (an Either with a left of ServiceFailure).
     *
     * It will also catch all exceptions thrown from within serviceCode and convert them into ServiceFailures of
     * type UNEXPECTED_ERROR.
     *
     * @param serviceCode
     * @param <T>
     * @return
     */
    protected <T> Either<ServiceFailure, T> handlingErrors(Supplier<Either<ServiceFailure, T>> serviceCode) {
        return handlingErrors(serviceCode, UNEXPECTED_ERROR);
    }

    /**
     * This wrapper wraps the serviceCode function and rolls back transactions upon receiving a ServiceFailure
     * response (an Either with a left of ServiceFailure).
     *
     * It will also catch all exceptions thrown from within serviceCode and convert them into ServiceFailures of
     * type UNEXPECTED_ERROR.
     *
     * @param serviceCode
     * @param <T>
     * @return
     */
    protected <T> Either<ServiceFailure, T> handlingErrors(Supplier<Either<ServiceFailure, T>> serviceCode, Enum<?> catchAllError) {
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
            return errorResponse(catchAllError);
        }
    }

    private void rollbackTransaction() {
        try {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (NoTransactionException e) {
            log.trace("No transaction to roll back");
            log.error(e);
        }
    }

    /**
     * Code to get a Response and return a Left of ServiceFailure when it's not found.
     *
     * @param responseId
     * @return
     */
    protected Either<ServiceFailure, Response> getResponse(Long responseId) {
        return getResponseById(responseId, responseRepository, () -> error(RESPONSE_NOT_FOUND));
    }

    /**
     * Code to get a ProcessRole and return a Left of ServiceFailure when it's not found.
     *
     * @param processRoleId
     * @return
     */
    protected Either<ServiceFailure, ProcessRole> getProcessRole(Long processRoleId) {
        return getProcessRoleById(processRoleId, processRoleRepository, () -> error(PROCESS_ROLE_NOT_FOUND));
    }

    /**
     * Create a Right of T, to indicate a success.
     *
     * @param response
     * @param <T>
     * @return
     */
    protected static <T> Either<ServiceFailure, T> successBody(T response) {
        return Either.<ServiceFailure, T> right(response);
    }

    /**
     * Create a Right of T, to indicate a success.
     *
     * @param response
     * @param <T>
     * @return
     */
    protected static <T> Either<ServiceFailure, ServiceSuccess<T>> successResponse(T response) {
        return Either.<ServiceFailure, ServiceSuccess<T>> right(new ServiceSuccess(response));
    }

    /**
     * Create a Left of ServiceFailure, to indicate a failure.
     *
     * @param error
     * @param <T>
     * @return
     */
    protected static <T> Either<ServiceFailure, T> errorResponse(Enum<?> error) {
        return left(error(error));
    }



    /**
     * Create a Left of ServiceFailure, to indicate a failure.
     *
     * @param error
     * @param <T>
     * @return
     */
    protected static <T> Either<ServiceFailure, T> errorResponse(Enum<?> error, Throwable e) {
        return left(error(error, e));
    }
}
