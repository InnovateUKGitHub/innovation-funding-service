package com.worth.ifs.competition.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.resource.MilestoneType;
import com.worth.ifs.commons.security.SecuredBySpring;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Service for operations around the usage and processing of Milestones
 */
public interface MilestoneService {
    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    @SecuredBySpring(value="READ", securedType=MilestoneResource.class,
            description = "Only Comp Admins and project finance users can see all the milestones for the given comopetition")
    ServiceResult<List<MilestoneResource>> getAllMilestonesByCompetitionId(final Long id);

    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    @SecuredBySpring(value="READ", securedType=MilestoneResource.class,
            description = "Only Comp Admins and project finance users can request the milestones for the competition by its type")
    ServiceResult<MilestoneResource> getMilestoneByTypeAndCompetitionId(final MilestoneType type, final Long id);

    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    @SecuredBySpring(value="UPDATE", securedType=MilestoneResource.class,
            description = "Only Comp Admins and project finance users are able to save all the milestones for the given competitions")
    ServiceResult<Void> updateMilestones(Long id, List<MilestoneResource> milestones);

    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    @SecuredBySpring(value="UPDATE", securedType=MilestoneResource.class,
            description = "Only Comp Admins and project finance users are able to save single milestone for the given competitions")
    ServiceResult<Void> updateMilestone(MilestoneResource milestone);

    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    @SecuredBySpring(value="UPDATE", securedType=MilestoneResource.class,
            description = "Only Comp Admins and project finance users are able to create the milestone for the given competitions")
    ServiceResult<MilestoneResource> create(MilestoneType type, Long id);
}
