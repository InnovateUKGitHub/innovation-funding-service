package com.worth.ifs.competition.service;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.competition.domain.Competition;
import java.util.List;

/**
 * ApplicationRestRestService is a utility to use client-side to retrieve Application data from the data-service controllers.
 */
public interface CompetitionsRestService {
    public Application getApplicationById(Long applicationId);
    public List<Application> getApplicationsByUserId(Long userId);
    public List<Competition> getAll();
    public Competition getCompetitionById(Long competitionId);

}
