package org.innovateuk.ifs.application.overheads;

import org.innovateuk.ifs.security.BaseControllerSecurityTest;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;

public class OverheadFileDownloadControllerSecurityTest extends BaseControllerSecurityTest<OverheadFileDownloadController> {

    @Override
    protected Class<? extends OverheadFileDownloadController> getClassUnderTest() {
        return OverheadFileDownloadController.class;
    }

    @Test
    public void downloadQuestionFile() {
        assertRolesCanPerform(() -> classUnderTest.downloadQuestionFile(1L),
                Role.APPLICANT,
                Role.PROJECT_FINANCE,
                Role.COMP_ADMIN,
                Role.SUPPORT,
                Role.IFS_ADMINISTRATOR,
                Role.INNOVATION_LEAD);
    }
}
