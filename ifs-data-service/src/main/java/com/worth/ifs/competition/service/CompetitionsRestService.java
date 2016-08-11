package com.worth.ifs.competition.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.*;

import java.time.LocalDateTime;
import java.util.List;


/**
 * Interface for CRUD operations on {@link com.worth.ifs.competition.domain.Competition} related data.
 */
public interface CompetitionsRestService {
    RestResult<List<CompetitionResource>> getAll();
    RestResult<List<CompetitionResource>> findLiveCompetitions();
    RestResult<List<CompetitionResource>> findProjectSetupCompetitions();
    RestResult<List<CompetitionResource>> findUpcomingCompetitions();
    RestResult<CompetitionSearchResult> searchCompetitions(String searchQuery, int page, int size);
    RestResult<CompetitionCountResource> countCompetitions();
    RestResult<CompetitionResource> getCompetitionById(Long competitionId);
    RestResult<List<CompetitionTypeResource>> getCompetitionTypes();
    RestResult<Void> update(CompetitionResource competition);
    RestResult<CompetitionResource> create();
    RestResult<Void> markSectionComplete(Long competitionId, CompetitionSetupSection section);
    RestResult<Void> markSectionInComplete(Long competitionId, CompetitionSetupSection section);
    RestResult<String> generateCompetitionCode(Long competitionId, LocalDateTime openingDate);
    RestResult<Void> initApplicationForm(Long competitionId, Long competitionTypeId);
}
