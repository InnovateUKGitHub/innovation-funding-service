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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.function.Supplier;

import static com.worth.ifs.assessment.transactional.AssessorServiceImpl.ServiceFailures.*;
import static com.worth.ifs.transactional.ServiceFailure.error;
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
@Transactional
public abstract class BaseTransactionalService  {

    private static final Log log = LogFactory.getLog(BaseTransactionalService.class);

    public enum Failures {
        UNEXPECTED_ERROR, //
        ROLE_NOT_FOUND, //
        RESPONSE_NOT_FOUND, //
        FORM_INPUT_RESPONSE_NOT_FOUND, //
        APPLICATION_NOT_FOUND, //
        FORM_INPUT_NOT_FOUND, //
        PROCESS_ROLE_NOT_FOUND, //
        PROCESS_ROLE_INCORRECT_TYPE, //
        PROCESS_ROLE_INCORRECT_APPLICATION, //
        USER_NOT_FOUND, //
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
     * Code to get a Response and return a Left of ServiceFailure when it's not found.
     *
     * @param responseId
     * @return
     */
    protected ServiceResult<Response> getResponse(Long responseId) {
        return getResponseById(responseId, responseRepository, RESPONSE_NOT_FOUND);
    }

    /**
     * Code to get a ProcessRole and return a Left of ServiceFailure when it's not found.
     *
     * @param processRoleId
     * @return
     */
    protected ServiceResult<ProcessRole> getProcessRole(Long processRoleId) {
        return getProcessRoleById(processRoleId, processRoleRepository, PROCESS_ROLE_NOT_FOUND);
    }

    /**
     * Create a Right of T, to indicate a success.
     *
     * @param response
     * @param <T>
     * @return
     */
    protected static <T> ServiceResult<T> successResponse(T response) {
        return ServiceResult.success(response);
    }

    /**
     * Create a Left of ServiceFailure, to indicate a failure.
     *
     * @param error
     * @param <T>
     * @return
     */
    protected static <T> ServiceResult<T> failureResponse(Enum<?> error) {
        return ServiceResult.failure(error(error));
    }



    /**
     * Create a Left of ServiceFailure, to indicate a failure.
     *
     * @param error
     * @param <T>
     * @return
     */
    protected static <T> ServiceResult<T> failureResponse(Enum<?> error, Throwable e) {
        return ServiceResult.failure(error(error, e));
    }
}
