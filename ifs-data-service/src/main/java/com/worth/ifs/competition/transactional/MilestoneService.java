package com.worth.ifs.competition.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Milestone;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Service for operations around the usage and processing of Milestones
 */
public interface MilestoneService {
    @PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<List<Milestone>> getAllMilestoneDatesByCompetitionId(final Long id);
}
