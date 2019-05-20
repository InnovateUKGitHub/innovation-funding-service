package org.innovateuk.ifs.invite.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.invite.mapper.ProjectUserInviteMapper;
import org.innovateuk.ifs.invite.repository.ProjectUserInviteRepository;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Lookup strategy for {@link ProjectUserInviteResource}, used for permissioning.
 */
@Component
@PermissionEntityLookupStrategies
public class ProjectUserInviteLookupStrategy {

    @Autowired
    private ProjectUserInviteRepository projectUserInviteRepository;

    @Autowired
    private ProjectUserInviteMapper projectUserInviteMapper;

    @PermissionEntityLookupStrategy
    public ProjectUserInviteResource getProjectUserInviteResource(final Long id){
        return projectUserInviteMapper.mapToResource(projectUserInviteRepository.findById(id).orElse(null));
    }
}
