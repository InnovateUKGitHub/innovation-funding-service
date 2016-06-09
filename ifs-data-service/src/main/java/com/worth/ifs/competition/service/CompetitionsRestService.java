package com.worth.ifs.competition.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupCompletedSectionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSectionResource;
import com.worth.ifs.competition.resource.CompetitionTypeResource;

import java.time.LocalDate;
import java.util.List;


/**
 * Interface for CRUD operations on {@link com.worth.ifs.competition.domain.Competition} related data.
 */
public interface CompetitionsRestService {
    RestResult<List<CompetitionResource>> getAll();
    RestResult<CompetitionResource> getCompetitionById(Long competitionId);
    RestResult<List<CompetitionTypeResource>> getCompetitionTypes();
    RestResult<List<CompetitionSetupSectionResource>> getSetupSections();
    RestResult<List<CompetitionSetupCompletedSectionResource>> getCompletedSetupSections(Long competitionId);
    RestResult<Void> update(CompetitionResource competition);
    RestResult<CompetitionResource> create();
    RestResult<List<CompetitionSetupCompletedSectionResource>> markSectionComplete(Long competitionId, Long sectionId);
    RestResult<List<CompetitionSetupCompletedSectionResource>> markSectionInComplete(Long competitionId, Long sectionId);
    RestResult<String> generateCompetitionCode(Long competitionId, LocalDate openingDate);
}
