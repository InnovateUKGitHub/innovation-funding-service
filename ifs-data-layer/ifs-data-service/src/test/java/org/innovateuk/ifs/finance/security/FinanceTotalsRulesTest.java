package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.security.ApplicationPermissionRules;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Mockito.when;

public class FinanceTotalsRulesTest extends BasePermissionRulesTest<ApplicationPermissionRules> {
    @Mock
    private ProcessRoleRepository processRoleRepository;

    @Override
    protected ApplicationPermissionRules supplyPermissionRulesUnderTest() {
        return new ApplicationPermissionRules();
    }

    @Test
    public void leadApplicantCanUpdateTotalsForAnApplication() {
        UserResource userResource = newUserResource().withId(1L).build();
        Application application = newApplication().withId(1L).build();

        when(processRoleRepository.existsByUserIdAndApplicationIdAndRoleName(userResource.getId(),
                application.getId(),
                UserRoleType.LEADAPPLICANT.getName())).thenReturn(true);

        //boolean result = rules.leadApplicantAndInternalUsersCanUpdateTotalsForAnApplication(application, userResource);

        //assertTrue(result);
    }

    @Test
    public void leadApplicantCanUpdateTotalsForAnApplication_returnsFalseWhenNoProcessRoleFound() {
        Application application = newApplication().withId(1L).build();

        //boolean result = rules.leadApplicantAndInternalUsersCanUpdateTotalsForAnApplication(application, newUserResource().withId(2L)
        //        .build());

        //assertFalse(result);
    }

    @Test
    public void internalUserCanUpdateTotalsForAnApplication() {
        Application application = newApplication().withId(1L).build();

        /*boolean result = rules.leadApplicantAndInternalUsersCanUpdateTotalsForAnApplication(application,
                newUserResource().withRolesGlobal(
                        newRoleResource()
                                .withName(UserRoleType.SYSTEM_REGISTRATION_USER.getName())
                                .build(1)
                ).build());

        assertTrue(result);*/
    }

    @Test
    public void internalUserCanUpdateTotalsForAnApplication_returnsFalseWhenNoProcessRoleFound() {
        /*Application application = newApplication().withId(1L).build();

        boolean result = rules.leadApplicantAndInternalUsersCanUpdateTotalsForAnApplication(application,
                newUserResource().withRolesGlobal(
                        newRoleResource()
                                .withName(UserRoleType.COLLABORATOR.getName())
                                .build(1)
                ).build());

        assertFalse(result);*/
    }
}