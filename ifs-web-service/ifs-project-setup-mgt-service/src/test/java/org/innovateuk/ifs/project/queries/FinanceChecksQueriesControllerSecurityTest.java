package org.innovateuk.ifs.project.queries;


import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.project.ProjectSetupSectionsPermissionRules;
import org.innovateuk.ifs.project.queries.controller.FinanceChecksQueriesController;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import java.util.function.Consumer;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;

public class FinanceChecksQueriesControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<FinanceChecksQueriesController> {

    @Override
    protected Class<? extends FinanceChecksQueriesController> getClassUnderTest() {
        return FinanceChecksQueriesController.class;
    }

    @Override
    protected Consumer<ProjectSetupSectionsPermissionRules> getVerification() {
        return permissionRules -> permissionRules.internalCanAccessFinanceChecksSection(eq(1L), isA(UserResource.class));
    }

    @Test
    public void testCancelNewForm() {
        assertSecured(() -> classUnderTest.cancelNewForm(1L, 2L, "", null, null));
    }

    @Test
    public void testDownloadAttachment() {
        assertSecured(() -> classUnderTest.downloadAttachment(1L, 2L, 3L, null, null));
    }

    @Test
    public void testSaveQuery() {
        assertSecured(() -> classUnderTest.saveQuery(1L, 2L, "", null, null, null, null, null));
    }

    @Test
    public void testSaveResponse() {
        assertSecured(() -> classUnderTest.saveResponse(null, 1L, 2L, 3L, "", null, null, null, null));
    }

    @Test
    public void testSaveQueryAttachment() {
        assertSecured(() -> classUnderTest.saveNewQueryAttachment(null, 1L, 2L, "", null, null, null, null));
    }

    @Test
    public void testSaveResponseAttachment() {
        assertSecured(() -> classUnderTest.saveNewResponseAttachment(null, 1L, 2L, 3L, "", null, null, null, null));
    }

    @Test
    public void testShowPage() {
        assertSecured(() -> classUnderTest.showPage(1L, 2L, "", null, null, Boolean.FALSE, Boolean.FALSE, null, null, null, Boolean.FALSE, null, null));
    }

    @Test
    public void testViewNewQuery() {
        assertSecured(() -> classUnderTest.viewNewQuery(1L, 2L, "", null, null, null));
    }

    @Test
    public void testViewNewResponse() {
        assertSecured(() -> classUnderTest.viewNewResponse(1L, 2L, 3L, "", null, null, null));
    }
}
