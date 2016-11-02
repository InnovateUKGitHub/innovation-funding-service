package com.worth.ifs.application.service;

import com.worth.ifs.competition.resource.*;
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

    Map<CompetitionResource.Status, List<CompetitionSearchResultItem>> getLiveCompetitions();

    Map<CompetitionResource.Status, List<CompetitionSearchResultItem>> getProjectSetupCompetitions();

    Map<CompetitionResource.Status, List<CompetitionSearchResultItem>> getUpcomingCompetitions();

    CompetitionSearchResult searchCompetitions(String searchQuery, int page);

    CompetitionCountResource getCompetitionCounts();

    List<CompetitionProjectsCountResource> getCompetitionProjectCounts();

    void update(CompetitionResource competition);

    void setSetupSectionMarkedAsComplete(Long competitionId, CompetitionSetupSection section);

    void setSetupSectionMarkedAsIncomplete(Long competitionId, CompetitionSetupSection section);

    void initApplicationFormByCompetitionType(Long competitionId, Long competitionTypeId);

    String generateCompetitionCode(Long competitionId, LocalDateTime openingDate);

    void returnToSetup(Long competitionId);

    void markAsSetup(Long competitionId);
}
