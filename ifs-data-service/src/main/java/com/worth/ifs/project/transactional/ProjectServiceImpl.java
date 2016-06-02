package com.worth.ifs.project.transactional;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_DATE_MUST_BE_IN_THE_FUTURE;
import static com.worth.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_DATE_MUST_START_ON_FIRST_DAY_OF_MONTH;
import static com.worth.ifs.commons.error.CommonFailureKeys.PROJECT_MANAGER_MUST_BE_IN_LEAD_ORGANISATION;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

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
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;

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
	public ServiceResult<Void> setProjectManager(Long projectId, Long projectManagerId) {
		 return getProject(projectId).
				  andOnSuccess(project -> validateProjectManager(project, projectManagerId).
				  andOnSuccess(() -> {
					  //TODO set something on the project here
					  project.setProjectManager(null);
				  }));
	}

	@Override
    public ServiceResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate) {
        return getProject(projectId).
                andOnSuccess(project -> validateProjectStartDate(projectStartDate).
                andOnSuccess(() -> project.setTargetStartDate(projectStartDate)));
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
    
    private ServiceResult<Void> validateProjectManager(Project project, Long projectManagerId) {
		Application application = applicationRepository.findOne(project.getId());
		Organisation leadPartner = application.getLeadOrganisation();
		List<ProcessRole> leadPartnerProcessRoles = application.getProcessRoles().stream().filter(pr -> leadPartner.equals(pr.getOrganisation())).collect(Collectors.toList());
		boolean match = leadPartnerProcessRoles.stream().anyMatch(lppr -> projectManagerId.equals(lppr.getUser().getId()));
		if(match) {
			return serviceSuccess();
		} else {
			return serviceFailure(new Error(PROJECT_MANAGER_MUST_BE_IN_LEAD_ORGANISATION));
		}
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
