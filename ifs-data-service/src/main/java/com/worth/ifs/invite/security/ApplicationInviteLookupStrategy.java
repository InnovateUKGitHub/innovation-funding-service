package com.worth.ifs.invite.security;

import com.worth.ifs.commons.security.PermissionEntityLookupStrategies;
import com.worth.ifs.commons.security.PermissionEntityLookupStrategy;
import com.worth.ifs.invite.mapper.ApplicationInviteMapper;
import com.worth.ifs.invite.repository.ApplicationInviteRepository;
import com.worth.ifs.invite.resource.ApplicationInviteResource;
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