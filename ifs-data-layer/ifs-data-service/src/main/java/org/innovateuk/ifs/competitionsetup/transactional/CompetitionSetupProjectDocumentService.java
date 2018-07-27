package org.innovateuk.ifs.competitionsetup.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.ProjectDocumentResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface CompetitionSetupProjectDocumentService {

    @SecuredBySpring(value = "UPDATE", description = "Only comp admin, project finance or IFS admin can update a project document")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<ProjectDocumentResource> save(ProjectDocumentResource projectDocumentResource);

    @SecuredBySpring(value = "UPDATE_ALL", description = "Only comp admin, project finance or IFS admin can update a list of project documents")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<List<ProjectDocumentResource>> saveAll(List<ProjectDocumentResource> projectDocumentResources);

    @SecuredBySpring(value = "READ", description = "Only comp admin, project finance or IFS admin can retrieve a project document")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<ProjectDocumentResource> findOne(long id);

    @SecuredBySpring(value = "READ", description = "Only comp admin, project finance or IFS admin can retrieve a list of project documents by competition id")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<List<ProjectDocumentResource>> findByCompetitionId(long competitionId);

    @SecuredBySpring(value = "DELETE", description = "Only comp admin, project finance or IFS admin can delete a project document")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<Void> delete(long id);
}
