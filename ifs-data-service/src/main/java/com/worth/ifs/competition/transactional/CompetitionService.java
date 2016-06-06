package com.worth.ifs.competition.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupCompletedSectionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSectionResource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Service for operations around the usage and processing of Competitions
 */
public interface CompetitionService {
    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<CompetitionResource> getCompetitionById(final Long id);

    @PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<CompetitionResource> update(Long id, CompetitionResource competitionResource);

    @PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<CompetitionResource> create();

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<CompetitionResource>> findAll();

    @PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<List<CompetitionSetupCompletedSectionResource>> findAllCompetitionSectionsStatuses(Long competitionId);

    @PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<List<CompetitionSetupSectionResource>> findAllCompetitionSections();
}
