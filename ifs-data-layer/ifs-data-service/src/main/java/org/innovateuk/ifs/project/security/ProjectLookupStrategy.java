package org.innovateuk.ifs.project.security;

import org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.mapper.ProjectMapper;
import org.innovateuk.ifs.project.repository.ProjectRepository;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
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
        //return projectMapper.mapToResource(projectRepository.findOne(projectId));

        Project project = projectRepository.findOne(projectId);

        if (project != null) {
            return projectMapper.mapToResource(project);
        } else {
            throw new ObjectNotFoundException("Project not found", null);
        }

    }
}
