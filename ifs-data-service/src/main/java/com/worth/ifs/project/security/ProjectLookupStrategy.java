package com.worth.ifs.project.security;

import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.mapper.ProjectMapper;
import com.worth.ifs.project.repository.ProjectRepository;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.commons.security.PermissionEntityLookupStrategies;
import com.worth.ifs.commons.security.PermissionEntityLookupStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@PermissionEntityLookupStrategies
public class ProjectLookupStrategy {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMapper projectMapper;

    @PermissionEntityLookupStrategy
    public Project getProject(Long projectId) {
        return projectRepository.findOne(projectId);
    }

    @PermissionEntityLookupStrategy
    public ProjectResource getProjectResource(Long projectId) {
        return projectMapper.mapToResource(projectRepository.findOne(projectId));
    }
}
