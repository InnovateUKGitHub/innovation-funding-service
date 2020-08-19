package org.innovateuk.ifs.invite.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.invite.mapper.ApplicationKtaInviteMapper;
import org.innovateuk.ifs.invite.repository.ApplicationKtaInviteRepository;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@PermissionEntityLookupStrategies
public class ApplicationKtaInviteLookupStrategy {

    @Autowired
    private ApplicationKtaInviteRepository applicationKtaInviteRepository;

    @Autowired
    private ApplicationKtaInviteMapper applicationKtaInviteMapper;

    @PermissionEntityLookupStrategy
    public ApplicationKtaInviteResource getApplicationInviteResource(final Long id){
        return applicationKtaInviteMapper.mapToResource(applicationKtaInviteRepository.findById(id).orElse(null));
    }
}
