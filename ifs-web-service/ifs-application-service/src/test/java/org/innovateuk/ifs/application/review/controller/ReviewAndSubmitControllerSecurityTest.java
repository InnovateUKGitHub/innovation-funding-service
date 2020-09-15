package org.innovateuk.ifs.application.review.controller;

import org.innovateuk.ifs.security.BaseControllerSecurityTest;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ReviewAndSubmitControllerSecurityTest extends BaseControllerSecurityTest<ReviewAndSubmitController> {

    @Override
    protected Class<? extends ReviewAndSubmitController> getClassUnderTest() {
        return ReviewAndSubmitController.class;
    }

    @Test
    public void applicationTrack() {
        List<Role> roles = new ArrayList<>();
        roles.add(Role.APPLICANT);
        roles.add(Role.KNOWLEDGE_TRANSFER_ADVISER);

        assertRolesCanPerform(() -> classUnderTest.applicationTrack(null, 0L, null), roles);
    }
}
