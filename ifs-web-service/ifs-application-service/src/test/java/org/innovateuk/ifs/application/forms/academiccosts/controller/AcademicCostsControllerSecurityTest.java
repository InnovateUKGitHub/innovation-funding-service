package org.innovateuk.ifs.application.forms.academiccosts.controller;

import org.innovateuk.ifs.security.BaseControllerSecurityTest;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AcademicCostsControllerSecurityTest extends BaseControllerSecurityTest<AcademicCostsController> {

    @Override
    protected Class<? extends AcademicCostsController> getClassUnderTest() {
        return AcademicCostsController.class;
    }

    @Test
    public void AcademicCostsController() {
        List<Role> roles = new ArrayList<>(Role.internalRoles());
        roles.add(Role.APPLICANT);
        roles.add(Role.SUPPORT);
        roles.add(Role.INNOVATION_LEAD);
        roles.add(Role.IFS_ADMINISTRATOR);
        roles.add(Role.COMP_ADMIN);
        roles.add(Role.PROJECT_FINANCE);
        roles.add(Role.STAKEHOLDER);
        roles.add(Role.KNOWLEDGE_TRANSFER_ADVISER);
        roles.add(Role.ASSESSOR);
        roles.add(Role.SYSTEM_MAINTAINER);

        assertRolesCanPerform(() -> classUnderTest.viewAcademicCosts(null, null, 0L, 0L, 0L, null), roles);
    }
}
