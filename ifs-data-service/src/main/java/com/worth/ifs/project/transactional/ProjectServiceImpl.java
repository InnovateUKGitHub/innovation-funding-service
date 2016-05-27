package com.worth.ifs.project.transactional;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.mapper.ProjectMapper;
import com.worth.ifs.project.repository.ProjectRepository;
import com.worth.ifs.project.resource.ProjectResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Override
    public ServiceResult<ProjectResource> getProjectById(@P("projectId") Long projectId) {
        return serviceSuccess(projectMapper.mapToResource(projectRepository.findById(projectId)));
    }

    @Override
    public ServiceResult<List<ProjectResource>> findAll() {
        return serviceSuccess(projectsToResources(projectRepository.findAll()));
    }

    @Override
    public ServiceResult<ProjectResource> createProjectFromApplication(Long applicationId) {
        return serviceSuccess(createProjectFromApplicationId(applicationId));
    }

    private ProjectResource createProjectFromApplicationId(final Long applicationId){
        Application application = applicationRepository.findOne(applicationId);
        Project project = new Project();
        project.setId(applicationId);
        project.setDurationInMonths(application.getDurationInMonths());
        Project createdProject = projectRepository.save(project);
        return projectMapper.mapToResource(createdProject);
    }

    @Override
    public ServiceResult<Void> createProjectsFromFundingDecisions(Map<Long, FundingDecision> applicationFundingDecisions) {
        applicationFundingDecisions.keySet().stream().forEach(this::createProjectFromApplicationId);
        return serviceSuccess();
    }

    private List<ProjectResource> projectsToResources(List<Project> filtered) {
        return simpleMap(filtered, project -> projectMapper.mapToResource(project));
    }
}
