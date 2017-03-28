package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.resource.AssessorCountOptionResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Interface for CRUD operations on {@link CompetitionResource} related data.
 */
@Service
public interface CompetitionService {
    CompetitionResource getById(Long id);

    CompetitionResource getPublishedById(Long id);

    CompetitionResource create();

    List<CompetitionResource> getAllCompetitions();

    List<CompetitionResource> getAllCompetitionsNotInSetup();

    List<CompetitionTypeResource> getAllCompetitionTypes();

    ServiceResult<Void> update(CompetitionResource competition);

    ServiceResult<Void> setSetupSectionMarkedAsComplete(Long competitionId, CompetitionSetupSection section);

    ServiceResult<Void> setSetupSectionMarkedAsIncomplete(Long competitionId, CompetitionSetupSection section);

    ServiceResult<Void> initApplicationFormByCompetitionType(Long competitionId, Long competitionTypeId);

    String generateCompetitionCode(Long competitionId, ZonedDateTime openingDate);

    ServiceResult<Void> returnToSetup(Long competitionId);

    ServiceResult<Void> markAsSetup(Long competitionId);

    List<AssessorCountOptionResource> getAssessorOptionsForCompetitionType(Long competitionTypeId);

    ServiceResult<Void> closeAssessment(Long competitionId);

    ServiceResult<Void> notifyAssessors(Long competitionId);

    void releaseFeedback(Long competitionId);

    PublicContentItemResource getPublicContentOfCompetition(Long competitionId);

    ByteArrayResource downloadPublicContentAttachment(Long contentGroupId);

    FileEntryResource getPublicContentFileDetails(Long contentGroupId);

    CompetitionResource createNonIfs();
}
