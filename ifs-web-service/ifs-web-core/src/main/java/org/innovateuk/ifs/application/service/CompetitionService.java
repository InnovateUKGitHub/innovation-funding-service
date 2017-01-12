package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.*;
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

    List<CompetitionTypeResource> getAllCompetitionTypes();

    Map<CompetitionStatus, List<CompetitionSearchResultItem>> getLiveCompetitions();

    Map<CompetitionStatus, List<CompetitionSearchResultItem>> getProjectSetupCompetitions();

    Map<CompetitionStatus, List<CompetitionSearchResultItem>> getUpcomingCompetitions();

    CompetitionSearchResult searchCompetitions(String searchQuery, int page);

    CompetitionCountResource getCompetitionCounts();

    ServiceResult<Void> update(CompetitionResource competition);

    ServiceResult<Void> setSetupSectionMarkedAsComplete(Long competitionId, CompetitionSetupSection section);

    void setSetupSectionMarkedAsIncomplete(Long competitionId, CompetitionSetupSection section);

    ServiceResult<Void> initApplicationFormByCompetitionType(Long competitionId, Long competitionTypeId);

    String generateCompetitionCode(Long competitionId, LocalDateTime openingDate);

    void returnToSetup(Long competitionId);

    void markAsSetup(Long competitionId);

    List<AssessorCountOptionResource> getAssessorOptionsForCompetitionType(Long competitionTypeId);

    void closeAssessment(Long competitionId);

    void notifyAssessors(Long competitionId);
}
