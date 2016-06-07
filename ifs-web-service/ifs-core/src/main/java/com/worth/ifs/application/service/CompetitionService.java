package com.worth.ifs.application.service;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSectionResource;
import com.worth.ifs.competition.resource.CompetitionTypeResource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Interface for CRUD operations on {@link CompetitionResource} related data.
 */
@Service
public interface CompetitionService {
    CompetitionResource getById(Long id);

    CompetitionResource create();

    List<CompetitionResource> getAllCompetitions();

    List<CompetitionSetupSectionResource> getCompetitionSetupSectionsByCompetitionId(long competitionId);

    List<Long> getCompletedCompetitionSetupSectionStatusesByCompetitionId(long competitionId);

    List<CompetitionTypeResource> getAllCompetitionTypes();
}
