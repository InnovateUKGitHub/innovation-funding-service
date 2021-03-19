package org.innovateuk.ifs.application.forms.questions.terms.controller;

import org.innovateuk.ifs.security.BaseControllerSecurityTest;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ApplicationTermsControllerSecurityTest extends BaseControllerSecurityTest<ApplicationTermsController> {

    @Override
    protected Class<? extends ApplicationTermsController> getClassUnderTest() {
        return ApplicationTermsController.class;
    }

    @Test
    public void getTerms() {
        List<Role> roles = new ArrayList<>();
        roles.add(Role.APPLICANT);
        roles.add(Role.PROJECT_FINANCE);
        roles.add(Role.IFS_ADMINISTRATOR);
        roles.add(Role.COMP_ADMIN);
        roles.add(Role.SUPPORT);
        roles.add(Role.INNOVATION_LEAD);
        roles.add(Role.MONITORING_OFFICER);
        roles.add(Role.ASSESSOR);
        roles.add(Role.STAKEHOLDER);
        roles.add(Role.EXTERNAL_FINANCE);
        roles.add(Role.KNOWLEDGE_TRANSFER_ADVISER);
        roles.add(Role.SUPPORTER);
        roles.add(Role.SYSTEM_MAINTAINER);

        assertRolesCanPerform(() -> classUnderTest.getTerms(0L, 0L, 0L,  null, null, null, false), roles);
    }
}
