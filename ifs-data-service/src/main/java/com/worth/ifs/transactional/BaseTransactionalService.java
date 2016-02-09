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

    protected Supplier<ServiceResult<Response>> response(Long responseId) {
        return () -> getResponse(responseId);
    }

    protected ServiceResult<Response> getResponse(Long responseId) {
        return getOrFail(() -> responseRepository.findOne(responseId), notFoundError(Response.class, responseId));
    }

    protected Supplier<ServiceResult<ProcessRole>> processRole(Long processRoleId) {
        return () -> getProcessRole(processRoleId);
    }

    protected ServiceResult<ProcessRole> getProcessRole(Long processRoleId) {
        return getOrFail(() -> processRoleRepository.findOne(processRoleId), notFoundError(ProcessRole.class, processRoleId));
    }

    protected Supplier<ServiceResult<Application>> application(final Long id) {
        return () -> getApplication(id);
    }

    protected ServiceResult<Application> getApplication(final Long id) {
        return getOrFail(() -> applicationRepository.findOne(id), notFoundError(Application.class, id));
    }

    protected Supplier<ServiceResult<User>> user(final Long id) {
        return () -> getUser(id);
    }

    protected ServiceResult<User> getUser(final Long id) {
        return getOrFail(() -> userRepository.findOne(id), notFoundError(User.class, id));
    }

    protected Supplier<ServiceResult<Competition>> competition(final Long id) {
        return () -> getCompetition(id);
    }

    protected ServiceResult<Competition> getCompetition(final Long id) {
        return getOrFail(() -> competitionRepository.findOne(id), notFoundError(Competition.class, id));
    }

    protected Supplier<ServiceResult<ApplicationStatus>> applicationStatus(final Long id) {
        return () -> getApplicationStatus(id);
    }

    protected ServiceResult<ApplicationStatus> getApplicationStatus(final Long id) {
        return getOrFail(() -> applicationStatusRepository.findOne(id), notFoundError(ApplicationStatus.class, id));
    }
}
