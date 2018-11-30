package org.innovateuk.ifs.application.forms.sections.yourprojectlocation.controller;

import org.innovateuk.ifs.security.BaseControllerSecurityTest;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;

import static org.innovateuk.ifs.user.resource.Role.APPLICANT;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;

public class YourProjectLocationControllerSecurityTest extends BaseControllerSecurityTest<YourProjectLocationController> {

    @Override
    protected Class<? extends YourProjectLocationController> getClassUnderTest() {
        return YourProjectLocationController.class;
    }

    @Test
    public void testViewPage() {
        assertRolesCanPerform(() -> classUnderTest.viewPage(0L, 0L, 0L, null, null),
                combineLists(APPLICANT, Role.internalRoles()));
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
