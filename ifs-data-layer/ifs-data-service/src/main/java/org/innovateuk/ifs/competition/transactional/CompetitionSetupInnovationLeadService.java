package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Service for operations around the usage and processing of Innovation Leads
 */
public interface CompetitionSetupInnovationLeadService {

    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionResource', 'MANAGE_INNOVATION_LEADS')")
    ServiceResult<List<UserResource>> findInnovationLeads(final long competitionId);

    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionResource', 'MANAGE_INNOVATION_LEADS')")
    ServiceResult<List<UserResource>> findAddedInnovationLeads(final long competitionId);

    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionResource', 'MANAGE_INNOVATION_LEADS')")
    ServiceResult<Void> addInnovationLead(final long competitionId, final long innovationLeadUserId);

    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionResource', 'MANAGE_INNOVATION_LEADS')")
    ServiceResult<Void> removeInnovationLead(final long competitionId, final long innovationLeadUserId);
}
