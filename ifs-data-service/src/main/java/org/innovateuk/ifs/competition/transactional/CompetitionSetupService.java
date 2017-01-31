package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDateTime;
import java.util.List;

public interface CompetitionSetupService {

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    ServiceResult<String> generateCompetitionCode(Long id, LocalDateTime dateTime);

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    ServiceResult<CompetitionResource> update(Long id, CompetitionResource competitionResource);

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    ServiceResult<CompetitionResource> create();

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    ServiceResult<Void> markSectionComplete(Long competitionId, CompetitionSetupSection section);

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    ServiceResult<Void> markSectionInComplete(Long competitionId, CompetitionSetupSection section);

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    ServiceResult<Void> returnToSetup(Long competitionId);

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    ServiceResult<Void> markAsSetup(Long competitionId);

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    ServiceResult<List<CompetitionTypeResource>> findAllTypes();

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    ServiceResult<Void> copyFromCompetitionTypeTemplate(Long competitionId, Long competitionTypeId);

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    ServiceResult<Void> copyFromCompetitionTemplate(Long competitionId, Long templateId);
}
