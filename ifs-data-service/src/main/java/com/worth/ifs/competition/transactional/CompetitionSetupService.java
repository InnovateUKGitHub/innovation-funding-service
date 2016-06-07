package com.worth.ifs.competition.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupCompletedSectionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSectionResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface CompetitionSetupService {
    @PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<List<CompetitionSetupCompletedSectionResource>> findAllCompetitionSectionsStatuses(Long competitionId);

    @PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<List<CompetitionSetupSectionResource>> findAllCompetitionSections();

    @PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<CompetitionResource> update(Long id, CompetitionResource competitionResource);

    @PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<CompetitionResource> create();

    @PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<Void> markSectionComplete(Long competitionId, Long sectionId);

    @PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<Void> markSectionInComplete(Long competitionId, Long sectionId);
}
