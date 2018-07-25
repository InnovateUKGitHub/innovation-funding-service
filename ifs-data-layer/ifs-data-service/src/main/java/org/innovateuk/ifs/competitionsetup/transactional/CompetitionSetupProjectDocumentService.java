package org.innovateuk.ifs.competitionsetup.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.ProjectDocumentResource;
import org.springframework.security.access.prepost.PreAuthorize;

public interface CompetitionSetupProjectDocumentService {

    @SecuredBySpring(value = "UPDATE", description = "Only comp admin, project finance or IFS admin can update project document")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<ProjectDocumentResource> save(ProjectDocumentResource projectDocumentResource);

    @SecuredBySpring(value = "UPDATE", description = "Only comp admin, project finance or IFS admin can retrieve project document")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<ProjectDocumentResource> findOne(Long id);
}
