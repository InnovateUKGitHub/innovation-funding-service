package org.innovateuk.ifs.invite.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.invite.mapper.ApplicationInviteMapper;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Lookup strategy for {@link ApplicationInviteResource}, used for permissioning.
 */
@Component
@PermissionEntityLookupStrategies
public class ApplicationInviteLookupStrategy {

    @Autowired
    private ApplicationInviteRepository applicationInviteRepository;

    @Autowired
    private ApplicationInviteMapper applicationInviteMapper;

    @PermissionEntityLookupStrategy
    public ApplicationInviteResource getApplicationInviteResource(final Long id){
        return applicationInviteMapper.mapToResource(applicationInviteRepository.findOne(id));
    }
}
