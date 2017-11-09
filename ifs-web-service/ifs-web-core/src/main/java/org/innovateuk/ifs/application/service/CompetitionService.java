package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.commons.security.NotSecured;
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
    @NotSecured("Not currently secured")
    CompetitionResource getById(Long id);

    @NotSecured("Not currently secured")
    List<UserResource> findInnovationLeads(Long competitionId);

    @NotSecured("Not currently secured")
    void addInnovationLead(Long competitionId, Long innovationLeadUserId);

    @NotSecured("Not currently secured")
    void removeInnovationLead(Long competitionId, Long innovationLeadUserId);

    @NotSecured("Not currently secured")
    CompetitionResource getPublishedById(Long id);

    @NotSecured("Not currently secured")
    List<CompetitionResource> getAllCompetitions();

    @NotSecured("Not currently secured")
    List<CompetitionResource> getAllCompetitionsNotInSetup();

    @NotSecured("Not currently secured")
    List<CompetitionTypeResource> getAllCompetitionTypes();

    @NotSecured("Not currently secured")
    List<OrganisationTypeResource> getOrganisationTypes(long id);

    @NotSecured("Not currently secured")
    List<AssessorCountOptionResource> getAssessorOptionsForCompetitionType(Long competitionTypeId);

    @NotSecured("Not currently secured")
    PublicContentItemResource getPublicContentOfCompetition(Long competitionId);

    @NotSecured("Not currently secured")
    ByteArrayResource downloadPublicContentAttachment(Long contentGroupId);

    @NotSecured("Not currently secured")
    FileEntryResource getPublicContentFileDetails(Long contentGroupId);
}
