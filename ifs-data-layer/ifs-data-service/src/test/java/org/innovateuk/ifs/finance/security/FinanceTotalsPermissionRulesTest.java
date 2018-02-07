package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class FinanceTotalsPermissionRulesTest extends BasePermissionRulesTest<FinanceTotalsPermissionRules> {
    @Mock
    private ProcessRoleRepository processRoleRepository;

    @Override
    protected FinanceTotalsPermissionRules supplyPermissionRulesUnderTest() {
        return new FinanceTotalsPermissionRules();
    }

    @Test
    public void leadApplicantCanUpdateTotalsForAnApplication() {
        UserResource userResource = newUserResource().withId(1L).build();
        ApplicationResource application = newApplicationResource().withId(1L).build();

        when(processRoleRepository.existsByUserIdAndApplicationIdAndRoleName(userResource.getId(),
                application.getId(),
                UserRoleType.LEADAPPLICANT.getName())).thenReturn(true);

        boolean result = rules.leadApplicantAndInternalUsersCanUpdateTotalsForAnApplication(application, userResource);

        assertTrue(result);
    }

    @Test
    public void leadApplicantCanUpdateTotalsForAnApplication_returnsFalseWhenNoProcessRoleFound() {
        ApplicationResource application = newApplicationResource().withId(1L).build();

        boolean result = rules.leadApplicantAndInternalUsersCanUpdateTotalsForAnApplication(application, newUserResource().withId(2L).build());

        assertFalse(result);
    }

    @Test
    public void internalUserCanUpdateTotalsForAnApplication() {
        ApplicationResource application = newApplicationResource().withId(1L).build();

        boolean result = rules.leadApplicantAndInternalUsersCanUpdateTotalsForAnApplication(application,
                newUserResource().withRolesGlobal(
                        newRoleResource()
                                .withName(UserRoleType.SYSTEM_REGISTRATION_USER.getName())
                                .build(1)
                ).build());

        assertTrue(result);
    }

    @Test
    public void internalUserCanUpdateTotalsForAnApplication_returnsFalseWhenNoProcessRoleFound() {
        ApplicationResource application = newApplicationResource().withId(1L).build();

        boolean result = rules.leadApplicantAndInternalUsersCanUpdateTotalsForAnApplication(application,
                newUserResource().withRolesGlobal(
                        newRoleResource()
                                .withName(UserRoleType.COLLABORATOR.getName())
                                .build(1)
                ).build());

        assertFalse(result);
    }
}