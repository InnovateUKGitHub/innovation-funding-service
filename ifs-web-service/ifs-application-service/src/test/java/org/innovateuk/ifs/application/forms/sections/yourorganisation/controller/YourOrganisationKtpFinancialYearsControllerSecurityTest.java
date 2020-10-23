package org.innovateuk.ifs.application.forms.sections.yourorganisation.controller;

import org.innovateuk.ifs.application.overview.controller.BaseApplicationControllerSecurityTest;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class YourOrganisationKtpFinancialYearsControllerSecurityTest extends BaseApplicationControllerSecurityTest<YourOrganisationKtpFinancialYearsController> {

    @Override
    protected Class<? extends YourOrganisationKtpFinancialYearsController> getClassUnderTest() {
        return YourOrganisationKtpFinancialYearsController.class;
    }

    @Test
    public void viewPage() {
        List<Role> roles = new ArrayList<>(Role.internalRoles());
        roles.add(Role.APPLICANT);
        roles.add(Role.STAKEHOLDER);
        roles.add(Role.EXTERNAL_FINANCE);
        roles.add(Role.KNOWLEDGE_TRANSFER_ADVISER);
        roles.add(Role.SUPPORTER);

        assertRolesCanPerform(() -> classUnderTest.viewPage(0L, 0L, 0L, 0L, null, null), roles);
    }
}
