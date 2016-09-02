package com.worth.ifs.assessment.security;

import com.worth.ifs.BasePermissionRulesTest;
import org.junit.Before;
import org.junit.Ignore;

@Ignore("TODO")
public class CompetitionParticipantPermissionRulesTest extends BasePermissionRulesTest<CompetitionParticipantPermissionRules> {

    @Before
    public void setUp() throws Exception {

    }

    @Override
    protected CompetitionParticipantPermissionRules supplyPermissionRulesUnderTest() {
        return new CompetitionParticipantPermissionRules();
    }
}
