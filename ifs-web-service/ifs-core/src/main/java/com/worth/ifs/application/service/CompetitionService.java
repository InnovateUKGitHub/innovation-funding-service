package com.worth.ifs.application.service;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.CompetitionTypeResource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    void update(CompetitionResource competition);

    void setSetupSectionMarkedAsComplete(Long competitionId, CompetitionSetupSection section);

    void setSetupSectionMarkedAsIncomplete(Long competitionId, CompetitionSetupSection section);

    void initApplicationForm(Long competitionId, Long competitionTypeId);

    String generateCompetitionCode(Long competitionId, LocalDateTime openingDate);
}
