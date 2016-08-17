package com.worth.ifs.application.service;

import com.worth.ifs.competition.resource.CompetitionCountResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.CompetitionTypeResource;
import com.worth.ifs.competition.resource.CompetitionSearchResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Interface for CRUD operations on {@link CompetitionResource} related data.
 */
@Service
public interface CompetitionService {
    CompetitionResource getById(Long id);

    CompetitionResource create();

    List<CompetitionResource> getAllCompetitions();

    List<CompetitionResource> getAllCompetitionsNotInSetup();

    List<CompetitionSetupSection> getCompletedCompetitionSetupSectionStatusesByCompetitionId(Long competitionId);

    List<CompetitionTypeResource> getAllCompetitionTypes();

    Map<CompetitionResource.Status, List<CompetitionResource>> getLiveCompetitions();

    Map<CompetitionResource.Status, List<CompetitionResource>> getProjectSetupCompetitions();

    Map<CompetitionResource.Status, List<CompetitionResource>> getUpcomingCompetitions();

    CompetitionSearchResult searchCompetitions(String searchQuery, int page);

    CompetitionCountResource getCompetitionCounts();

    void update(CompetitionResource competition);

    void setSetupSectionMarkedAsComplete(Long competitionId, CompetitionSetupSection section);

    void setSetupSectionMarkedAsIncomplete(Long competitionId, CompetitionSetupSection section);

    void initApplicationFormByCompetitionType(Long competitionId, Long competitionTypeId);

    String generateCompetitionCode(Long competitionId, LocalDateTime openingDate);
}
