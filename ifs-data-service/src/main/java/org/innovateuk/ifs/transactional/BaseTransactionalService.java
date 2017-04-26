package org.innovateuk.ifs.transactional;

import org.innovateuk.ifs.address.repository.AddressTypeRepository;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.repository.QuestionRepository;
import org.innovateuk.ifs.application.repository.SectionRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.repository.ProjectRepository;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.RoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.CommonErrors.forbiddenError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NOT_OPEN;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.OPEN;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * This class represents the base class for transactional services.  Method calls within this service will have
 * transaction boundaries provided to allow for safe atomic operations and persistence cascading.
 */
@Transactional
public abstract class BaseTransactionalService  {

    @Autowired
    protected ProcessRoleRepository processRoleRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected RoleRepository roleRepository;

    @Autowired
    protected CompetitionRepository competitionRepository;

    @Autowired
    protected ApplicationRepository applicationRepository;

    @Autowired
    protected ProjectRepository projectRepository;

    @Autowired
    protected SectionRepository sectionRepository;

    @Autowired
    protected OrganisationRepository organisationRepository;

    @Autowired
    protected AddressTypeRepository addressTypeRepository;

    @Autowired
    private QuestionRepository questionRepository;

    protected Supplier<ServiceResult<ProcessRole>> processRole(Long processRoleId) {
        return () -> getProcessRole(processRoleId);
    }

    protected ServiceResult<ProcessRole> getProcessRole(Long processRoleId) {
        return find(processRoleRepository.findOne(processRoleId), notFoundError(ProcessRole.class, processRoleId));
    }

    protected ServiceResult<List<ProcessRole>> getProcessRoles(Long applicationId, UserRoleType roleType) {
        return getRole(roleType).andOnSuccess(role -> find(processRoleRepository.findByApplicationIdAndRoleId(applicationId, role.getId()), notFoundError(ProcessRole.class, applicationId, role.getId())));
    }

    protected Supplier<ServiceResult<Section>> section(final Long id) {
        return () -> getSection(id);
    }

    protected Supplier<ServiceResult<Application>> application(final Long id) {
        return () -> getApplication(id);
    }

    protected Supplier<ServiceResult<Project>> project(final Long id) {
        return () -> getProject(id);
    }

    protected ServiceResult<Project> getProject(final Long id) {
        return find(projectRepository.findOne(id), notFoundError(Project.class, id));
    }

    protected final Supplier<ServiceResult<Application>> openApplication(long applicationId) {
        return () -> getOpenApplication(applicationId);
    }

    protected final ServiceResult<Application> getOpenApplication(long applicationId) {
        return find(application(applicationId)).andOnSuccess(application -> {
                    if (application.getCompetition() != null && !OPEN.equals(application.getCompetition().getCompetitionStatus())) {
                        return serviceFailure(COMPETITION_NOT_OPEN);
                    } else {
                        return serviceSuccess(application);
                    }
                }
        );
    }

    protected ServiceResult<Application> getApplication(final Long id) {
        return find(applicationRepository.findOne(id), notFoundError(Application.class, id));
    }
    protected ServiceResult<Section> getSection(final Long id) {
        return find(sectionRepository.findOne(id), notFoundError(Section.class, id));
    }

    protected Supplier<ServiceResult<User>> user(final Long id) {
        return () -> getUser(id);
    }

    protected ServiceResult<User> getUser(final Long id) {
        return find(userRepository.findOne(id), notFoundError(User.class, id));
    }

    protected Supplier<ServiceResult<Competition>> competition(final Long id) {
        return () -> getCompetition(id);
    }

    protected ServiceResult<Competition> getCompetition(final Long id) {
        return find(competitionRepository.findOne(id), notFoundError(Competition.class, id));
    }

    protected Supplier<ServiceResult<Role>> role(UserRoleType roleType) {
        return () -> getRole(roleType);
    }

    protected Supplier<ServiceResult<Role>> role(String roleName) {
        return () -> getRole(roleName);
    }

    protected ServiceResult<Role> getRole(UserRoleType roleType) {
        return getRole(roleType.getName());
    }

    protected ServiceResult<Role> getRole(String roleName) {
        return find(roleRepository.findOneByName(roleName), notFoundError(Role.class, roleName));
    }

    protected Supplier<ServiceResult<Organisation>> organisation(Long id) {
        return () -> getOrganisation(id);
    }

    protected ServiceResult<Organisation> getOrganisation(Long id) {
        return find(organisationRepository.findOne(id), notFoundError(Organisation.class, id));
    }

    protected ServiceResult<User> getCurrentlyLoggedInUser() {
        UserResource currentUser = (UserResource) SecurityContextHolder.getContext().getAuthentication().getDetails();

        if (currentUser == null) {
            return serviceFailure(forbiddenError());
        }

        return getUser(currentUser.getId());
    }

    protected Supplier<ServiceResult<Question>> question(Long questionId) {
        return () -> getQuestion(questionId);
    }

    protected ServiceResult<Question> getQuestion(Long questionId) {
        return find(questionRepository.findOne(questionId), notFoundError(Question.class));
    }
}
