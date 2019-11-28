package org.innovateuk.ifs.finance.security;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.UsersRolesService;
import org.junit.Test;
import org.mockito.Mock;

public class GrantClaimMaximumPermissionRulesTest extends BasePermissionRulesTest<GrantClaimMaximumPermissionRules> {

    @Mock
    private UsersRolesService usersRolesService;

    @Mock
    private ProjectUserRepository projectUserRepository;

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

        assertTrue(rules.usersWithApplicationForCompetitionCanCheckMaxFundingLevelOverridden(competition, user));

        verify(usersRolesService, only()).userHasApplicationForCompetition(user.getId(), competition.getId());
    }

    @Test
    public void usersWithApplicationForCompetitionCanCheckMaxFundingLevelOverridden_noApplicationForCompetition() {
        CompetitionResource competition = newCompetitionResource().build();
        UserResource user = newUserResource().build();

        when(usersRolesService.userHasApplicationForCompetition(user.getId(), competition.getId()))
            .thenReturn(serviceSuccess(false));

        assertFalse(rules.usersWithApplicationForCompetitionCanCheckMaxFundingLevelOverridden(competition, user));

        verify(usersRolesService, only()).userHasApplicationForCompetition(user.getId(), competition.getId());
    }

    @Test
    public void internalAdminCanCheckMaxFundingLevelOverridden_compAdminUser() {
        CompetitionResource competition = newCompetitionResource().build();
        UserResource user = newUserResource().withRoleGlobal(COMP_ADMIN).build();

        assertTrue(rules.internalAdminCanCheckMaxFundingLevelOverridden(competition, user));

        verify(usersRolesService, never()).userHasApplicationForCompetition(isA(Long.class), isA(Long.class));
    }

    @Test
    public void internalAdminCanCheckMaxFundingLevelOverridden_projectFinanceUser() {
        CompetitionResource competition = newCompetitionResource().build();
        UserResource user = newUserResource().withRoleGlobal(PROJECT_FINANCE).build();

        assertTrue(rules.internalAdminCanCheckMaxFundingLevelOverridden(competition, user));

        verify(usersRolesService, never()).userHasApplicationForCompetition(isA(Long.class), isA(Long.class));
    }

    @Test
    public void userInAProjectCanCheckMaxFundingLevelOverridden(){
        CompetitionResource competition = newCompetitionResource().build();
        UserResource user = newUserResource().build();

        when(projectUserRepository.existsByProjectApplicationCompetitionIdAndUserId(competition.getId(), user.getId()))
            .thenReturn(true);

        assertTrue(rules.userInAProjectCanCheckMaxFundingLevelOverridden(competition, user));
    }

    @Test
    public void userInAProjectCanCheckMaxFundingLevelOverridden_noProject(){
        CompetitionResource competition = newCompetitionResource().build();
        UserResource user = newUserResource().build();

        when(projectUserRepository.existsByProjectApplicationCompetitionIdAndUserId(competition.getId(), user.getId()))
            .thenReturn(false);

        assertFalse(rules.userInAProjectCanCheckMaxFundingLevelOverridden(competition, user));
    }
}
