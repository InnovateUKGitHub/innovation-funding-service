package org.innovateuk.ifs.invite.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.invite.transactional.AcceptApplicationInviteService;
import org.innovateuk.ifs.invite.transactional.AcceptApplicationInviteServiceImpl;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;

public class AcceptApplicationInviteServiceSecurityTest extends BaseServiceSecurityTest<AcceptApplicationInviteService> {

    @Override
    protected Class<? extends AcceptApplicationInviteService> getClassUnderTest() {
        return AcceptApplicationInviteServiceImpl.class;
    }

    @Test
    public void acceptInvite() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.acceptInvite("abcdef", 1L),
                UserRoleType.SYSTEM_REGISTRATION_USER
        );
    }
}
