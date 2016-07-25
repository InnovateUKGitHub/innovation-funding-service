package com.worth.ifs.assessment.security;

import com.worth.ifs.BasePermissionRulesTest;
import org.junit.Before;
import org.junit.Ignore;

@Ignore("TODO")
public class AssessorFormInputResponsePermissionRulesTest extends BasePermissionRulesTest<AssessorFormInputResponsePermissionRules> {

    @Before
    public void setUp() throws Exception {

    }

    @Override
    protected AssessorFormInputResponsePermissionRules supplyPermissionRulesUnderTest() {
        return new AssessorFormInputResponsePermissionRules();
    }
}