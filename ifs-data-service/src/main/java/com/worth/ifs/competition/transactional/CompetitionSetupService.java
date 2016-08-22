package com.worth.ifs.competition.transactional;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.CompetitionTypeResource;

public interface CompetitionSetupService {

    @PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<String> generateCompetitionCode(Long id, LocalDateTime dateTime);

    @PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<CompetitionResource> update(Long id, CompetitionResource competitionResource);

    @PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<CompetitionResource> create();

    @PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<Void> markSectionComplete(Long competitionId, CompetitionSetupSection section);

    @PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<Void> markSectionInComplete(Long competitionId, CompetitionSetupSection section);

    @PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<Void> returnToSetup(Long competitionId);

    @PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<Void> markAsSetup(Long competitionId);

    @PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<List<CompetitionTypeResource>> findAllTypes();
    
    @PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<Void> initialiseFormForCompetitionType(Long competitionId, Long competitionType);
}
