package com.worth.ifs.competition.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.CompetitionResource;

import java.util.List;


/**
 * Interface for CRUD operations on {@link com.worth.ifs.competition.domain.Competition} related data.
 */
public interface CompetitionsRestService {
    RestResult<List<CompetitionResource>> getAll();
    RestResult<CompetitionResource> getCompetitionById(Long competitionId);

    RestResult<CompetitionResource> create();
}
