package org.innovateuk.ifs.project.core.security;

import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.mapper.ProjectMapper;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Rules to look up a {@link Project}, {@link ProjectResource} or {@link ProjectCompositeId} from a {@link Long}
 * project id. This can then be feed into methods marked with the
 * {@link org.innovateuk.ifs.commons.security.PermissionRule} annotation as part of the Spring security mechanism.
 * Note that the reason we don't simply feed the {@link Long} id into permission rules is that there would then be no
 * way of distinguishing between entity types in a given rule.
 */
@Component
@PermissionEntityLookupStrategies
public class ProjectLookupStrategy {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMapper projectMapper;

    @PermissionEntityLookupStrategy
    public Project getProject(Long projectId) {
        return projectRepository.findById(projectId).orElse(null);
    }

    @PermissionEntityLookupStrategy
    public ProjectResource getProjectResource(Long projectId) {

        return projectMapper.mapToResource(projectRepository.findById(projectId)
                .orElseThrow(() -> new ObjectNotFoundException("Project not found", null)));

    }

    @PermissionEntityLookupStrategy
    public ProjectCompositeId getProjectCompositeId(Long projectId) {
        return ProjectCompositeId.id(projectId);
    }

}
