package com.worth.ifs.competition.transactional;

import java.util.List;

import com.worth.ifs.competition.resource.MilestoneType;
import com.worth.ifs.commons.security.SecuredBySpring;
import org.springframework.security.access.prepost.PreAuthorize;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.MilestoneResource;

/**
 * Service for operations around the usage and processing of Milestones
 */
public interface MilestoneService {
    @PreAuthorize("hasAuthority('comp_admin')")
    @SecuredBySpring(value="READ", securedType=MilestoneResource.class,
            description = "Only Comp Admins can see all the milestones for the given comopetition")
    ServiceResult<List<MilestoneResource>> getAllDatesByCompetitionId(final Long id);

    @PreAuthorize("hasAuthority('comp_admin')")
    @SecuredBySpring(value="UPDATE", securedType=MilestoneResource.class,
            description = "Only Comp Admins is able to save all the milestones for the given competitions")
    ServiceResult<Void> update(Long id, List<MilestoneResource> milestones);

    @PreAuthorize("hasAuthority('comp_admin')")
    @SecuredBySpring(value="UPDATE", securedType=MilestoneResource.class,
            description = "Only Comp Admins is able to create the milestone for the given competitions")
    ServiceResult<MilestoneResource> create(MilestoneType type, Long id);
}
