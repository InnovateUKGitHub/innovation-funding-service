package org.innovateuk.ifs.invite.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.invite.transactional.AcceptApplicationInviteService;
import org.innovateuk.ifs.invite.transactional.AcceptApplicationInviteServiceImpl;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;

import java.util.Optional;

public class AcceptApplicationInviteServiceSecurityTest extends
        BaseServiceSecurityTest<AcceptApplicationInviteService> {

    @Override
    protected Class<? extends AcceptApplicationInviteService> getClassUnderTest() {
        return AcceptApplicationInviteServiceImpl.class;
    }

    @Test
    public void acceptInvite() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.acceptInvite("abcdef", 1L, Optional.empty()),
                Role.SYSTEM_REGISTRATION_USER
        );
    }
}
