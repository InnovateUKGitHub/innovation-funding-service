package com.worth.ifs.competition.service;

import com.worth.ifs.competition.domain.Competition;

import java.util.List;


/**
 * Interface for CRUD operations on {@link com.worth.ifs.competition.domain.Competition} related data.
 */
public interface CompetitionsRestService {
    public List<Competition> getAll();
    public Competition getCompetitionById(Long competitionId);

}
