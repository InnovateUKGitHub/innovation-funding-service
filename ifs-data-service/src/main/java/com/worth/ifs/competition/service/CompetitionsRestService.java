package com.worth.ifs.competition.service;

import java.time.LocalDateTime;
import java.util.List;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.CompetitionTypeResource;


/**
 * Interface for CRUD operations on {@link com.worth.ifs.competition.domain.Competition} related data.
 */
public interface CompetitionsRestService {
    RestResult<List<CompetitionResource>> getAll();
    RestResult<CompetitionResource> getCompetitionById(Long competitionId);
    RestResult<List<CompetitionTypeResource>> getCompetitionTypes();
    RestResult<Void> update(CompetitionResource competition);
    RestResult<CompetitionResource> create();
    RestResult<Void> markSectionComplete(Long competitionId, CompetitionSetupSection section);
    RestResult<Void> markSectionInComplete(Long competitionId, CompetitionSetupSection section);
    RestResult<String> generateCompetitionCode(Long competitionId, LocalDateTime openingDate);
}
