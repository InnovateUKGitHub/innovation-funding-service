package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.ZonedDateTime;
import java.util.List;

public interface CompetitionSetupService {

    @SecuredBySpring(value = "GENERATE_COMP_CODE", description = "Only those with either comp admin or project finance roles can generate competition codes")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    ServiceResult<String> generateCompetitionCode(Long id, ZonedDateTime dateTime);

    @SecuredBySpring(value = "UPDATE", description = "Only those with either comp admin or project finance roles can update competitions")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    ServiceResult<CompetitionResource> update(Long id, CompetitionResource competitionResource);

    @SecuredBySpring(value = "CREATE", description = "Only those with either comp admin or project finance roles can create competitions")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    ServiceResult<CompetitionResource> create();

    @SecuredBySpring(value = "UPDATE", description = "Only those with either comp admin or project finance roles can mark sections complete")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    ServiceResult<Void> markSectionComplete(Long competitionId, CompetitionSetupSection section);

    @SecuredBySpring(value = "UPDATE", description = "Only those with either comp admin or project finance roles can mark sections incomplete")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    ServiceResult<Void> markSectionInComplete(Long competitionId, CompetitionSetupSection section);

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
}
