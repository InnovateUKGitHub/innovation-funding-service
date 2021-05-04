package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

import java.util.List;

/**
 * Service for operations around the usage and processing of Milestones
 */
public interface MilestoneService {
    @PreAuthorize("hasAnyAuthority('system_registrar')")
    @SecuredBySpring(value="READ", securedType=MilestoneResource.class,
            description = "All users can get see the public milestones for the given competition")
    ServiceResult<List<MilestoneResource>> getAllPublicMilestonesByCompetitionId(final Long id);

    @PreAuthorize("hasAnyAuthority('comp_admin')")
    @SecuredBySpring(value = "VALIDATE_PUBLIC_DATES", securedType = MilestoneResource.class, description = "Only comp admin or project finance can validate the public dates.")
    ServiceResult<Boolean> allPublicDatesComplete(final Long id);

    @PreAuthorize("hasPermission(#id, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'VIEW_MILESTONE')")
    ServiceResult<List<MilestoneResource>> getAllMilestonesByCompetitionId(final Long id);

    @PreAuthorize("hasPermission(#id, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'VIEW_MILESTONE_BY_TYPE')")
    ServiceResult<MilestoneResource> getMilestoneByTypeAndCompetitionId(final MilestoneType type, @P("id")final Long id);

    @PreAuthorize("hasAnyAuthority('comp_admin')")
    @SecuredBySpring(value="UPDATE", securedType=MilestoneResource.class,
            description = "Only Comp Admins and project finance users are able to save all the milestones for the given competitions")
    ServiceResult<Void> updateMilestones(List<MilestoneResource> milestones);

    @PreAuthorize("hasAnyAuthority('comp_admin')")
    @SecuredBySpring(value="UPDATE", securedType=MilestoneResource.class,
            description = "Only Comp Admins and project finance users are able to save single milestone for the given competitions")
    ServiceResult<Void> updateMilestone(MilestoneResource milestone);

    @PreAuthorize("hasAnyAuthority('comp_admin')")
    @SecuredBySpring(value="UPDATE", securedType=MilestoneResource.class,
            description = "Only Comp Admins and project finance users are able to create the milestone for the given competitions")
    ServiceResult<MilestoneResource> create(MilestoneResource milestone);

    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'UPDATE_COMPLETION_STAGE')")
    ServiceResult<Void> updateCompletionStage(long competitionId, CompetitionCompletionStage completionStage);

    @NotSecured(value = "This Service is to be used within other secured services")
    ServiceResult<Void> rebuildMilestones(long competitionId);
}
