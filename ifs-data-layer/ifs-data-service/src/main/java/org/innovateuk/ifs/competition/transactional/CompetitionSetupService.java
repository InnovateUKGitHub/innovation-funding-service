package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.innovateuk.ifs.setup.resource.SetupStatusResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CompetitionSetupService {

    @SecuredBySpring(value = "GENERATE_COMP_CODE", description = "Only those with either comp admin or project finance roles can generate competition codes")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    ServiceResult<String> generateCompetitionCode(Long id, ZonedDateTime dateTime);

    @SecuredBySpring(value = "UPDATE", description = "Only those with either comp admin or project finance roles can update competitions")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    ServiceResult<CompetitionResource> update(Long id, CompetitionResource competitionResource);

    @SecuredBySpring(value = "UPDATE", description = "Only those with either comp admin or project finance roles can update competitions")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    ServiceResult<Void> updateCompetitionInitialDetails(Long competitionId, CompetitionResource competitionResource, Long existingLeadTechnologistId);

    @SecuredBySpring(value = "CREATE", description = "Only those with either comp admin or project finance roles can create competitions")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    ServiceResult<CompetitionResource> create();

    @SecuredBySpring(value = "UPDATE", description = "Only those with either comp admin or project finance roles can mark sections complete")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    ServiceResult<SetupStatusResource> markSectionComplete(Long competitionId, CompetitionSetupSection section);

    @SecuredBySpring(value = "UPDATE", description = "Only those with either comp admin or project finance roles can mark sections incomplete")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    ServiceResult<SetupStatusResource> markSectionIncomplete(Long competitionId, CompetitionSetupSection section);

    @SecuredBySpring(value = "UPDATE", description = "Only those with either comp admin or project finance roles can mark subsections complete")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    ServiceResult<SetupStatusResource> markSubsectionComplete(Long competitionId, CompetitionSetupSection parentSection, CompetitionSetupSubsection subsection);

    @SecuredBySpring(value = "UPDATE", description = "Only those with either comp admin or project finance roles can mark subsections incomplete")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    ServiceResult<SetupStatusResource> markSubsectionIncomplete(Long competitionId, CompetitionSetupSection parentSection, CompetitionSetupSubsection subsection);

    @SecuredBySpring(value = "UPDATE", description = "Only those with either comp admin or project finance roles can return projects to setup")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    ServiceResult<Void> returnToSetup(Long competitionId);

    @SecuredBySpring(value = "UPDATE", description = "Only those with either comp admin or project finance roles can mark projects as setup")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    ServiceResult<Void> markAsSetup(Long competitionId);

    @SecuredBySpring(value = "READ", description = "Only those with either comp admin or project finance roles can return read all competition types")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    ServiceResult<List<CompetitionTypeResource>> findAllTypes();

    @SecuredBySpring(value = "CREATE", description = "Only those with either comp admin or project finance roles can copy from a competition type template")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    ServiceResult<Void> copyFromCompetitionTypeTemplate(Long competitionId, Long competitionTypeId);

    @SecuredBySpring(value = "CREATE", description = "Only those with either comp admin or project finance roles can copy from a competition template")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    ServiceResult<Void> copyFromCompetitionTemplate(Long competitionId, Long templateId);

    @SecuredBySpring(value = "CREATE", description = "Only those with either comp admin or project finance roles can create a non IFS competition")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    ServiceResult<CompetitionResource> createNonIfs();

    @SecuredBySpring(value = "READ", description = "Only comp admins or projectfinances users can read the status related to competition setup")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<Map<CompetitionSetupSection, Optional<Boolean>>> getSectionStatuses(Long competitionId);

    @SecuredBySpring(value = "READ", description = "Only comp admins or projectfinances users can read the status related to competition setup")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<Map<CompetitionSetupSubsection, Optional<Boolean>>> getSubsectionStatuses(Long competitionId);
}
