package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Set;

public interface GrantClaimMaximumService {

    @SecuredBySpring(value = "READ", securedType = GrantClaimMaximumResource.class,
            description = "Only those with either comp admin or project finance roles can read a GrantClaimMaximum by" +
                    " id")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<GrantClaimMaximumResource> getGrantClaimMaximumById(long id);

    @SecuredBySpring(value = "READ", securedType = GrantClaimMaximumResource.class,
            description = "Only those with either comp admin or project finance roles can read a GrantClaimMaximum by" +
                    " comp id")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<List<GrantClaimMaximumResource>> getGrantClaimMaximumByCompetitionId(long competitionId);

    @SecuredBySpring(value = "UPDATE", securedType = GrantClaimMaximumResource.class,
            description = "Only those with either comp admin or project finance roles can update a GrantClaimMaximum")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<GrantClaimMaximumResource> save(GrantClaimMaximumResource grantClaimMaximumResource);

    @SecuredBySpring(value = "READ_GRANT_CLAIM_MAXIMUM", description = "A user can see the grant claim maximums for " +
            "if they have an application for the competition")
    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionResource', " +
            "'MAX_FUNDING_LEVEL_OVERRIDDEN')")
    ServiceResult<Boolean> isMaximumFundingLevelConstant(long competitionId);

    @SecuredBySpring(value = "UPDATE", securedType = GrantClaimMaximumResource.class,
            description = "Only those with either comp admin or project finance roles can update a GrantClaimMaximum")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<Set<Long>> revertToDefault(long competitionId);
}