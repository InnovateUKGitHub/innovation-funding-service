package org.innovateuk.ifs.form.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.security.QuestionStatusRules;
import org.innovateuk.ifs.form.builder.SectionResourceBuilder;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test the {@link QuestionStatusRules}
 */
public class SectionPermissionRulesTest extends BasePermissionRulesTest<SectionPermissionRules> {

    @Mock
    private ProcessRoleRepository processRoleRepository;

    @Override
    protected SectionPermissionRules supplyPermissionRulesUnderTest() {
        return new SectionPermissionRules();
    }

    @Test
    public void testUserCanReadSection() {
        SectionResource section = SectionResourceBuilder.newSectionResource().build();
        UserResource user = UserResourceBuilder.newUserResource().build();

        assertTrue(rules.userCanReadSection(section, user));
    }

    @Test
    public void testUserCanUpdateSection() {
        SectionResource section = SectionResourceBuilder.newSectionResource().build();
        UserResource user = UserResourceBuilder.newUserResource().build();

        assertFalse(rules.userCanUpdateSection(section, user));
    }

}
