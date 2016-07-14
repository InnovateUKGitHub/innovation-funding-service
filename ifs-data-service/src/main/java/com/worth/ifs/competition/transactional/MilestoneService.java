package com.worth.ifs.competition.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.MilestoneResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Service for operations around the usage and processing of Milestones
 */
public interface MilestoneService {
    @PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<List<MilestoneResource>> getAllDatesByCompetitionId(final Long id);
}
