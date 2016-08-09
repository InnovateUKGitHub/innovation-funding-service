package com.worth.ifs.assessment.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import org.springframework.security.access.prepost.PreFilter;

/**
 * Service for managing {@link com.worth.ifs.invite.domain.CompetitionInvite}s.
 */
public interface CompetitionInviteService {

    // TODO correct permissions
    @PreFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<CompetitionInviteResource> openInvite(String inviteHash);
}
