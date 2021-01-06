package org.innovateuk.ifs.application.security;


import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class FormInputResponsePermissionRulesTest extends BasePermissionRulesTest<FormInputResponsePermissionRules> {

    @Mock
    private ApplicationSecurityHelper applicationSecurityHelper;

    @Override
    protected FormInputResponsePermissionRules supplyPermissionRulesUnderTest() {
        return new FormInputResponsePermissionRules();
    }

    @Test
    public void userCanSeeResponseIfTheyCanViewApplication() {
        long applicationId = 1L;
        FormInputResponseResource formInputResponseResource = newFormInputResponseResource().withApplication(applicationId).build();
        UserResource canView = newUserResource().build();
        UserResource cantView = newUserResource().build();

        when(applicationSecurityHelper.canViewApplication(applicationId, canView)).thenReturn(true);
        when(applicationSecurityHelper.canViewApplication(applicationId, cantView)).thenReturn(false);

        assertTrue(rules.userCanSeeResponseIfTheyCanViewApplication(formInputResponseResource, canView));
        assertFalse(rules.userCanSeeResponseIfTheyCanViewApplication(formInputResponseResource, cantView));
    }
}