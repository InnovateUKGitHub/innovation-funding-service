package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.junit.Before;
import org.junit.Ignore;

@Ignore("TODO")
public class CompetitionInvitePermissionRulesTest extends BasePermissionRulesTest<CompetitionInvitePermissionRules> {

    @Before
    public void setUp() throws Exception {

    }

    @Override
    protected CompetitionInvitePermissionRules supplyPermissionRulesUnderTest() {
        return new CompetitionInvitePermissionRules();
    }
}
