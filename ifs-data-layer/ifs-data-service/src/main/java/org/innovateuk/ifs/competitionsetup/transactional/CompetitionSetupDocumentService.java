package org.innovateuk.ifs.competitionsetup.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionDocumentResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface CompetitionSetupDocumentService {

    @SecuredBySpring(value = "UPDATE", description = "Only comp admin, project finance or IFS admin can update a competition project document")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'ifs_administrator')")
    ServiceResult<CompetitionDocumentResource> save(CompetitionDocumentResource competitionDocumentResource);

    @SecuredBySpring(value = "UPDATE_ALL", description = "Only comp admin, project finance or IFS admin can update a list of project documents required in project setup")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'ifs_administrator')")
    ServiceResult<List<CompetitionDocumentResource>> saveAll(List<CompetitionDocumentResource> competitionDocumentResources);

    @SecuredBySpring(value = "READ", description = "Only comp admin, project finance or IFS admin can retrieve a competition project document")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'ifs_administrator')")
    ServiceResult<CompetitionDocumentResource> findOne(long id);

    @SecuredBySpring(value = "READ", description = "Only comp admin, project finance or IFS admin can retrieve a list of project documents by competition id")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'ifs_administrator')")
    ServiceResult<List<CompetitionDocumentResource>> findByCompetitionId(long competitionId);

    @SecuredBySpring(value = "DELETE", description = "Only comp admin, project finance or IFS admin can delete a competition project document")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'ifs_administrator')")
    ServiceResult<Void> delete(long id);
}
