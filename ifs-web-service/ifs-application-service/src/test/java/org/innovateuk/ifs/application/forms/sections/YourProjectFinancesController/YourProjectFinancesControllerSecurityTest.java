package org.innovateuk.ifs.application.forms.sections.YourProjectFinancesController;

import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.controller.YourProjectCostsController;
import org.innovateuk.ifs.application.forms.sections.yourprojectfinances.controller.YourProjectFinancesController;
import org.innovateuk.ifs.security.BaseControllerSecurityTest;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class YourProjectFinancesControllerSecurityTest extends BaseControllerSecurityTest<YourProjectFinancesController> {

    @Override
    protected Class<? extends YourProjectFinancesController> getClassUnderTest() {
        return YourProjectFinancesController.class;
    }

    @Test
    public void viewFinancesOverview() {
        List<Role> roles = new ArrayList<>(Role.internalRoles());
        roles.add(Role.APPLICANT);
        roles.add(Role.STAKEHOLDER);
        roles.add(Role.EXTERNAL_FINANCE);
        roles.add(Role.KNOWLEDGE_TRANSFER_ADVISER);
        roles.add(Role.COFUNDER);

        assertRolesCanPerform(() -> classUnderTest.viewFinancesOverview(0L, 0L, 0L, null, null), roles);
    }
}
