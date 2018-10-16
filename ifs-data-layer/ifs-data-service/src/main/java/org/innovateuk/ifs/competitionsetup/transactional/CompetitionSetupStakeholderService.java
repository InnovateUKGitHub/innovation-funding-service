package org.innovateuk.ifs.competitionsetup.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.StakeholderInviteResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Transactional and secured service providing operations around stakeholders.
 */
public interface CompetitionSetupStakeholderService {

    @SecuredBySpring(value = "SAVE_STAKEHOLDER_INVITE", description = "Only comp admin, project finance or IFS admin can save a stakeholder invite")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<Void> inviteStakeholder(UserResource invitedUser, long competitionId);

    @SecuredBySpring(value = "FIND_STAKEHOLDERS_FOR_COMPETITION", description = "Only comp admin, project finance or IFS admin can search stakeholders for a given competition")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<List<UserResource>> findStakeholders(long competitionId);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "READ_INVITE_ON_HASH",
            description = "The System Registration user can read an invite for a given hash",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<StakeholderInviteResource> getInviteByHash(String hash);

    @SecuredBySpring(value = "ADD_STAKEHOLDER_TO_COMPETITION", description = "Only comp admin, project finance or IFS admin can add stakeholders to a given competition")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<Void> addStakeholder(long competitionId, long stakeholderUserId);

    @SecuredBySpring(value = "REMOVE_STAKEHOLDER_FROM_COMPETITION", description = "Only comp admin, project finance or IFS admin can remove stakeholders from a given competition")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<Void> removeStakeholder(long competitionId, long stakeholderUserId);

    @SecuredBySpring(value = "FIND_PENDING_STAKEHOLDER_INVITES_FOR_COMPETITION", description = "Only comp admin, project finance or IFS admin can find pending stakeholder invites for a given competition")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<List<UserResource>> findPendingStakeholderInvites(long competitionId);
}