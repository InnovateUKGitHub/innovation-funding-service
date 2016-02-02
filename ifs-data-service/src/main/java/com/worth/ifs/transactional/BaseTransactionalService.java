package com.worth.ifs.transactional;

import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.repository.ApplicationStatusRepository;
import com.worth.ifs.application.repository.ResponseRepository;
import com.worth.ifs.commons.error.ErrorTemplate;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.repository.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

import static com.worth.ifs.commons.error.Errors.notFoundError;
import static com.worth.ifs.util.EntityLookupCallbacks.getProcessRoleById;
import static com.worth.ifs.util.EntityLookupCallbacks.getResponseById;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

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

    public enum Failures implements ErrorTemplate {

        UNEXPECTED_ERROR("An unexpected error occurred", INTERNAL_SERVER_ERROR), //
        NOT_FOUND_ENTITY("Unable to find entity", NOT_FOUND), //
        INCORRECT_TYPE("Argument was of an incorrect type", BAD_REQUEST);

        private String errorMessage;
        private HttpStatus category;

        Failures(String errorMessage, HttpStatus category) {
            this.errorMessage = errorMessage;
            this.category = category;
        }

        @Override
        public String getErrorKey() {
            return name();
        }

        @Override
        public String getErrorMessage() {
            return errorMessage;
        }

        @Override
        public HttpStatus getCategory() {
            return category;
        }
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
    protected CompetitionRepository competitionRepository;

    @Autowired
    protected ApplicationRepository applicationRepository;

    /**
     * Code to get a Response and return a Left of ServiceFailure when it's not found.
     *
     * @param responseId
     * @return
     */
    protected ServiceResult<Response> getResponse(Long responseId) {
        return getResponseById(responseId, responseRepository, notFoundError(Response.class, responseId));
    }

    /**
     * Code to get a ProcessRole and return a Left of ServiceFailure when it's not found.
     *
     * @param processRoleId
     * @return
     */
    protected ServiceResult<ProcessRole> getProcessRole(Long processRoleId) {
        return getProcessRoleById(processRoleId, processRoleRepository, notFoundError(ProcessRole.class, processRoleId));
    }
}
