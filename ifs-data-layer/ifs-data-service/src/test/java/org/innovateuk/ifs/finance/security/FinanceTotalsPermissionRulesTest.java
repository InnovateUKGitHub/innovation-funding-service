package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class FinanceTotalsPermissionRulesTest extends BasePermissionRulesTest<FinanceTotalsPermissionRules> {

    @Override
    protected FinanceTotalsPermissionRules supplyPermissionRulesUnderTest() {
        return new FinanceTotalsPermissionRules();
    }

    @Test
    public void leadApplicantCanUpdateTotalsForAnApplication() {
        UserResource userResource = newUserResource().withId(1L).build();
        ApplicationResource application = newApplicationResource().withId(1L).build();

        when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(userResource.getId(),
                application.getId(),
                LEADAPPLICANT)).thenReturn(true);

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
                newUserResource().withRolesGlobal(singletonList(SYSTEM_MAINTAINER)
                ).build());

        assertTrue(result);
    }

    @Test
    public void internalUserCanUpdateTotalsForAnApplication_returnsFalseWhenNoProcessRoleFound() {
        ApplicationResource application = newApplicationResource().withId(1L).build();

        boolean result = rules.leadApplicantAndInternalUsersCanUpdateTotalsForAnApplication(application,
                newUserResource().withRolesGlobal(singletonList(COLLABORATOR)
                ).build());

        assertFalse(result);
    }
}