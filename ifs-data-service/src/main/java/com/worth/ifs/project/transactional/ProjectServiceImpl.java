package com.worth.ifs.project.transactional;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.mapper.ProjectMapper;
import com.worth.ifs.project.repository.ProjectRepository;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.ProcessRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.*;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.user.resource.UserRoleType.COLLABORATOR;
import static com.worth.ifs.user.resource.UserRoleType.FINANCE_CONTACT;
import static com.worth.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static com.worth.ifs.util.EntityLookupCallbacks.getOnlyElementOrFail;
import static java.util.Arrays.asList;

@Service
public class ProjectServiceImpl extends BaseTransactionalService implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Override
    public ServiceResult<ProjectResource> getProjectById(@P("projectId") Long projectId) {
        return getProject(projectId).andOnSuccessReturn(projectMapper::mapToResource);
    }

    @Override
    public ServiceResult<List<ProjectResource>> findAll() {
        return serviceSuccess(projectsToResources(projectRepository.findAll()));
    }

    @Override
    public ServiceResult<ProjectResource> createProjectFromApplication(Long applicationId) {
        return serviceSuccess(createProjectFromApplicationId(applicationId));
    }

    @Override
    public ServiceResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate) {
        return validateProjectStartDate(projectStartDate).
                andOnSuccess(() -> getProject(projectId).
                andOnSuccessReturnVoid(project -> project.setTargetStartDate(projectStartDate)));
    }
    
	@Override
	public ServiceResult<Void> updateFinanceContact(Long projectId, Long organisationId, Long financeContactUserId) {
		 return getProject(projectId).
				  andOnSuccess(project -> validateProjectOrganisationFinanceContact(project, organisationId, financeContactUserId).
				  andOnSuccessReturnVoid(processRole -> createFinanceContactProcessRole(processRole)));
	}

    private ServiceResult<ProcessRole> createFinanceContactProcessRole(ProcessRole originalProcessRole) {
        return getRole(FINANCE_CONTACT).andOnSuccessReturn(role -> {
            ProcessRole financeContact = new ProcessRole(originalProcessRole.getUser(), originalProcessRole.getApplication(), role, originalProcessRole.getOrganisation());
            processRoleRepository.save(financeContact);
            return financeContact;
        });
    }

    @Override
    public ServiceResult<Void> createProjectsFromFundingDecisions(Map<Long, FundingDecision> applicationFundingDecisions) {
        applicationFundingDecisions.keySet().stream().forEach(this::createProjectFromApplicationId);
        return serviceSuccess();
    }

    private ServiceResult<Void> validateProjectStartDate(LocalDate date) {

        if (date.getDayOfMonth() != 1) {
            return serviceFailure(new Error(PROJECT_SETUP_DATE_MUST_START_ON_FIRST_DAY_OF_MONTH));
        }

        if (date.isBefore(LocalDate.now())) {
            return serviceFailure(new Error(PROJECT_SETUP_DATE_MUST_BE_IN_THE_FUTURE));
        }

        return serviceSuccess();
    }

    private ServiceResult<ProcessRole> validateProjectOrganisationFinanceContact(Project project, Long organisationId, Long financeContactUserId) {

        return getApplication(project.getId()).andOnSuccess(application -> {

            List<ProcessRole> applicationProcessRoles = application.getProcessRoles();

            List<ProcessRole> matchingUserOrganisationProcessRoles = simpleFilter(applicationProcessRoles,
                    pr -> organisationId.equals(pr.getOrganisation().getId()) && financeContactUserId.equals(pr.getUser().getId()));

            List<ProcessRole> collaborativeProcessRoles = simpleFilter(matchingUserOrganisationProcessRoles,
                    pr -> asList(COLLABORATOR.getName(), LEADAPPLICANT.getName()).contains(pr.getRole().getName()));

            if (matchingUserOrganisationProcessRoles.isEmpty()) {
                return serviceFailure(new Error(PROJECT_SETUP_FINANCE_CONTACT_MUST_BE_A_USER_ON_THE_APPLICATION_FOR_THE_ORGANISATION));
            }

            if (collaborativeProcessRoles.isEmpty()) {
                return serviceFailure(new Error(PROJECT_SETUP_FINANCE_CONTACT_MUST_BE_A_LEAD_APPLICANT_OR_COLLABORATOR_ON_THE_APPLICATION_FOR_THE_ORGANISATION));
            }

            return getOnlyElementOrFail(collaborativeProcessRoles);
        });
    }
    
    private ProjectResource createProjectFromApplicationId(final Long applicationId){
        Application application = applicationRepository.findOne(applicationId);
        Project project = new Project();
        project.setId(applicationId);
        project.setDurationInMonths(application.getDurationInMonths());
        project.setName(application.getName());
        project.setTargetStartDate(application.getStartDate());
        Project createdProject = projectRepository.save(project);
        return projectMapper.mapToResource(createdProject);
    }

    private List<ProjectResource> projectsToResources(List<Project> filtered) {
        return simpleMap(filtered, project -> projectMapper.mapToResource(project));
    }

    private ServiceResult<Project> getProject(long projectId) {
        return find(projectRepository.findOne(projectId), notFoundError(Project.class, projectId));
    }

}
