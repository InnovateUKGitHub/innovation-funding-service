package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.UsersRolesService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class GrantClaimMaximumPermissionRulesTest extends BasePermissionRulesTest<GrantClaimMaximumPermissionRules> {

    @Mock
    private UsersRolesService usersRolesService;

    @Override
    protected GrantClaimMaximumPermissionRules supplyPermissionRulesUnderTest() {
        return new GrantClaimMaximumPermissionRules();
    }

    @Test
    public void usersWithApplicationForCompetitionCanCheckMaxFundingLevelOverridden() {
        CompetitionResource competition = newCompetitionResource().build();
        UserResource user = newUserResource().build();

        when(usersRolesService.userHasApplicationForCompetition(user.getId(), competition.getId()))
                .thenReturn(serviceSuccess(true));

        assertTrue(rules.internalAdminAndUsersWithApplicationForCompetitionCanCheckMaxFundingLevelOverridden
                (competition, user));

        verify(usersRolesService, only()).userHasApplicationForCompetition(user.getId(), competition.getId());
    }

    @Test
    public void usersWithApplicationForCompetitionCanCheckMaxFundingLevelOverridden_noApplicationForCompetition() {
        CompetitionResource competition = newCompetitionResource().build();
        UserResource user = newUserResource().build();

        when(usersRolesService.userHasApplicationForCompetition(user.getId(), competition.getId()))
                .thenReturn(serviceSuccess(false));

        assertFalse(rules.internalAdminAndUsersWithApplicationForCompetitionCanCheckMaxFundingLevelOverridden
                (competition, user));

        verify(usersRolesService, only()).userHasApplicationForCompetition(user.getId(), competition.getId());
    }

    @Test
    public void usersWithApplicationForCompetitionCanCheckMaxFundingLevelOverridden_compAdminUser() {
        CompetitionResource competition = newCompetitionResource().build();
        UserResource user = newUserResource().withRoleGlobal(COMP_ADMIN).build();

        assertTrue(rules.internalAdminAndUsersWithApplicationForCompetitionCanCheckMaxFundingLevelOverridden
                (competition, user));

        verify(usersRolesService, never()).userHasApplicationForCompetition(isA(Long.class), isA(Long.class));
    }

    @Test
    public void usersWithApplicationForCompetitionCanCheckMaxFundingLevelOverridden_projectFinanceUser() {
        CompetitionResource competition = newCompetitionResource().build();
        UserResource user = newUserResource().withRoleGlobal(PROJECT_FINANCE).build();

        assertTrue(rules.internalAdminAndUsersWithApplicationForCompetitionCanCheckMaxFundingLevelOverridden
                (competition, user));

        verify(usersRolesService, never()).userHasApplicationForCompetition(isA(Long.class), isA(Long.class));
    }
}
