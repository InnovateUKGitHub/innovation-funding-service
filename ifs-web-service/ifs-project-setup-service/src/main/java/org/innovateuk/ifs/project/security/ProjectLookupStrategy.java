package org.innovateuk.ifs.project.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.springframework.stereotype.Component;

@Component
@PermissionEntityLookupStrategies
public class ProjectLookupStrategy {

    @PermissionEntityLookupStrategy
    public ProjectCompositeId getProjectCompositeId(Long projectId) {
        return ProjectCompositeId.id(projectId);
    }
}
