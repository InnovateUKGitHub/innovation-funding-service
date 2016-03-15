package com.worth.ifs.competition.service;

import java.util.List;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.CompetitionResource;


/**
 * Interface for CRUD operations on {@link com.worth.ifs.competition.domain.Competition} related data.
 */
public interface CompetitionsRestService {
    RestResult<List<CompetitionResource>> getAll();
    RestResult<CompetitionResource> getCompetitionById(Long competitionId);

}
