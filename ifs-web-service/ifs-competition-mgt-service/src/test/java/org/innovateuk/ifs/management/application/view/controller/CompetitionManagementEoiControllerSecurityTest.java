package org.innovateuk.ifs.management.application.view.controller;

import org.innovateuk.ifs.security.BaseControllerSecurityTest;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CompetitionManagementEoiControllerSecurityTest extends BaseControllerSecurityTest<CompetitionManagementEoiController> {

    @Override
    protected Class<? extends CompetitionManagementEoiController> getClassUnderTest() {
        return CompetitionManagementEoiController.class;
    }

    @Test
    public void AcademicCostsController() {
        List<Role> roles = new ArrayList<>(Role.internalRoles());
        roles.add(Role.COMP_ADMIN);
        roles.add(Role.SUPPORT);
        roles.add(Role.INNOVATION_LEAD);
        roles.add(Role.STAKEHOLDER);
        roles.add(Role.SUPER_ADMIN_USER);
        roles.add(Role.AUDITOR);
        roles.add(Role.IFS_ADMINISTRATOR);
        roles.add(Role.PROJECT_FINANCE);

        assertRolesCanPerform(() -> classUnderTest.downloadEOIEvidenceFile(null), roles);
    }
}
