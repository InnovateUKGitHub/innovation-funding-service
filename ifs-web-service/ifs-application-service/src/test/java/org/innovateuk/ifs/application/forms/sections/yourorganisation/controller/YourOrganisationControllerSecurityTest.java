package org.innovateuk.ifs.application.forms.sections.yourorganisation.controller;

import org.innovateuk.ifs.security.BaseControllerSecurityTest;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class YourOrganisationControllerSecurityTest extends BaseControllerSecurityTest<YourOrganisationController> {

    @Override
    protected Class<? extends YourOrganisationController> getClassUnderTest() {
        return YourOrganisationController.class;
    }

    @Test
    public void viewPage() {
        List<Role> roles = new ArrayList<>(Role.internalRoles());
        roles.add(Role.APPLICANT);
        roles.add(Role.STAKEHOLDER);
        roles.add(Role.EXTERNAL_FINANCE);
        roles.add(Role.KNOWLEDGE_TRANSFER_ADVISER);

        assertRolesCanPerform(() -> classUnderTest.viewPage(0L, 0L, 0L, 0L), roles);
    }
}
