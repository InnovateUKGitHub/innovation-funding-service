package org.innovateuk.ifs.application.forms.sections.yourprojectlocation.controller;

import org.innovateuk.ifs.security.BaseControllerSecurityTest;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.user.resource.Role.APPLICANT;
import static org.innovateuk.ifs.user.resource.Role.STAKEHOLDER;
import static org.innovateuk.ifs.user.resource.Role.internalRoles;

public class YourProjectLocationControllerSecurityTest extends BaseControllerSecurityTest<YourProjectLocationController> {

    @Override
    protected Class<? extends YourProjectLocationController> getClassUnderTest() {
        return YourProjectLocationController.class;
    }

    @Test
    public void testViewPage() {
        List<Role> roles = new ArrayList<>(internalRoles());
        roles.add(APPLICANT);
        roles.add(STAKEHOLDER);

        assertRolesCanPerform(() -> classUnderTest.viewPage(0L, 0L, 0L, null, null), roles);
    }

    @Test
    public void testUpdate() {
        assertRolesCanPerform(() -> classUnderTest.update(0L, 0L, null), APPLICANT);
    }

    @Test
    public void testAutosave() {
        assertRolesCanPerform(() -> classUnderTest.autosave(0L, 0L, null), APPLICANT);
    }

    @Test
    public void testMarkAsComplete() {
        assertRolesCanPerform(() -> classUnderTest.markAsComplete(0L, 0L, 0L,
                null, null, null, null, null), APPLICANT);
    }

    @Test
    public void testMarkAsIncomplete() {
        assertRolesCanPerform(() -> classUnderTest.markAsIncomplete(0L, 0L, 0L,
                null), APPLICANT);
    }
}
