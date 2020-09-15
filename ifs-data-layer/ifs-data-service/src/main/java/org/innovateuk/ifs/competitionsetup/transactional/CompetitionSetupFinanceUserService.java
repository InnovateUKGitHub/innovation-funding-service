package org.innovateuk.ifs.competitionsetup.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.CompetitionFinanceInviteResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface CompetitionSetupFinanceUserService {

    @SecuredBySpring(value = "SAVE_FINANCE_USER_INVITE", description = "Only comp admin, project finance or IFS admin can save a finance users invite")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<Void> inviteFinanceUser(UserResource invitedUser, long competitionId);

    @SecuredBySpring(value = "FIND_FINANCE_USER_FOR_COMPETITION", description = "Only comp admin, project finance or IFS admin can search finance users for a given competition")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<List<UserResource>> findFinanceUser(long competitionId);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "READ_STAKEHOLDER_INVITE_ON_HASH",
            description = "The System Registration user can read an invite for a given hash",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<CompetitionFinanceInviteResource> getInviteByHash(String hash);

    @SecuredBySpring(value = "ADD_FINANCE_USER_TO_COMPETITION", description = "Only comp admin, project finance or IFS admin can add finance users to a given competition")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<Void> addFinanceUser(long competitionId, long userId);

    @SecuredBySpring(value = "REMOVE_FINANCE_USER_FROM_COMPETITION", description = "Only comp admin, project finance or IFS admin can remove finance users from a given competition")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<Void> removeFinanceUser(long competitionId, long userId);

    @SecuredBySpring(value = "FIND_PENDING_FINANCE_USER_INVITES_FOR_COMPETITION", description = "Only comp admin, project finance or IFS admin can find pending finance users invites for a given competition")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<List<UserResource>> findPendingFinanceUseInvites(long competitionId);
}
