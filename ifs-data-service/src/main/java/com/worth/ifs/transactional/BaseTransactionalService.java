package com.worth.ifs.transactional;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.repository.ApplicationStatusRepository;
import com.worth.ifs.application.repository.ResponseRepository;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.repository.UserRepository;
import com.worth.ifs.util.EntityLookupCallbacks;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

import static com.worth.ifs.commons.error.Errors.notFoundError;
import static com.worth.ifs.util.EntityLookupCallbacks.getOrFail;

/**
 * This class represents the base class for transactional services.  Method calls within this service will have
 * transaction boundaries provided to allow for safe atomic operations and persistence cascading.  Code called
 * within a {@link #handlingErrors(Supplier)} supplier will have its exceptions converted into ServiceFailures
 * of type GENERAL_UNEXPECTED_ERROR and the transaction rolled back.
 *
 * Created by dwatson on 06/10/15.
 */
@Transactional
public abstract class BaseTransactionalService  {

    private static final Log log = LogFactory.getLog(BaseTransactionalService.class);

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
        return getOrFail(() -> responseRepository.findOne(responseId), notFoundError(Response.class, responseId));
    }

    /**
     * Code to get a ProcessRole and return a Left of ServiceFailure when it's not found.
     *
     * @param processRoleId
     * @return
     */
    protected ServiceResult<ProcessRole> getProcessRole(Long processRoleId) {
        return getOrFail(() -> processRoleRepository.findOne(processRoleId), notFoundError(ProcessRole.class, processRoleId));
    }

    protected ServiceResult<Application> getApplication(final Long id) {
        return getOrFail(() -> applicationRepository.findOne(id), notFoundError(Application.class, id));
    }

    protected ServiceResult<User> getUser(final Long id) {
        return getOrFail(() -> userRepository.findOne(id), notFoundError(User.class, id));
    }

    protected ServiceResult<Competition> getCompetition(final Long id) {
        return getOrFail(() -> competitionRepository.findOne(id), notFoundError(Competition.class, id));
    }

    protected ServiceResult<ApplicationStatus> getApplicationStatus(final Long id) {
        return getOrFail(() -> applicationStatusRepository.findOne(id), notFoundError(ApplicationStatus.class, id));
    }
}
