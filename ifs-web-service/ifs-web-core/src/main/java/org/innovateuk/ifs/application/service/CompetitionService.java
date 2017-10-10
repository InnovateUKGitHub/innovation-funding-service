package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.resource.AssessorCountOptionResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Interface for CRUD operations on {@link CompetitionResource} related data.
 */
@Service
public interface CompetitionService {
    CompetitionResource getById(Long id);

    List<UserResource> findInnovationLeads(Long competitionId);

    void addInnovationLead(Long competitionId, Long innovationLeadUserId);

    void removeInnovationLead(Long competitionId, Long innovationLeadUserId);

    CompetitionResource getPublishedById(Long id);

    List<CompetitionResource> getAllCompetitions();

    List<CompetitionResource> getAllCompetitionsNotInSetup();

    ApplicationPageResource findUnsuccessfulApplications(Long competitionId, int pageNumber, int pageSize, String sortField);

    List<CompetitionTypeResource> getAllCompetitionTypes();

    List<OrganisationTypeResource> getOrganisationTypes(long id);

    List<AssessorCountOptionResource> getAssessorOptionsForCompetitionType(Long competitionTypeId);

    ServiceResult<Void> closeAssessment(Long competitionId);

    PublicContentItemResource getPublicContentOfCompetition(Long competitionId);

    ByteArrayResource downloadPublicContentAttachment(Long contentGroupId);

    FileEntryResource getPublicContentFileDetails(Long contentGroupId);
}
