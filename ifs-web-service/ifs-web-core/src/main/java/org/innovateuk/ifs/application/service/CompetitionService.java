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
    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    CompetitionResource getById(Long id);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<UserResource> findInnovationLeads(Long competitionId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    void addInnovationLead(Long competitionId, Long innovationLeadUserId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    void removeInnovationLead(Long competitionId, Long innovationLeadUserId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    CompetitionResource getPublishedById(Long id);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<CompetitionResource> getAllCompetitions();

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<CompetitionResource> getAllCompetitionsNotInSetup();

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<CompetitionTypeResource> getAllCompetitionTypes();

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<OrganisationTypeResource> getOrganisationTypes(long id);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<AssessorCountOptionResource> getAssessorOptionsForCompetitionType(Long competitionTypeId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    PublicContentItemResource getPublicContentOfCompetition(Long competitionId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ByteArrayResource downloadPublicContentAttachment(Long contentGroupId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    FileEntryResource getPublicContentFileDetails(Long contentGroupId);
}
