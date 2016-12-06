package com.worth.ifs.application.security;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.application.builder.ApplicationResourceBuilder;
import com.worth.ifs.application.builder.SectionResourceBuilder;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.user.builder.ProcessRoleBuilder;
import com.worth.ifs.user.builder.UserResourceBuilder;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Test
    public void testOnlyMemberOfProjectTeamCanMarkSection() {
        ApplicationResource application = ApplicationResourceBuilder.newApplicationResource().build();
        UserResource leadApplicant = UserResourceBuilder.newUserResource().build();
        UserResource nonProjectTeamMember = UserResourceBuilder.newUserResource().build();

        when(processRoleRepository.findByUserIdAndApplicationId(leadApplicant.getId(), application.getId()))
                .thenReturn(ProcessRoleBuilder.newProcessRole().withRole(UserRoleType.LEADAPPLICANT).build());
        when(processRoleRepository.findByUserIdAndApplicationId(nonProjectTeamMember.getId(), application.getId()))
                .thenReturn(null);

        assertTrue(rules.onlyMemberOfProjectTeamCanMarkSectionAsComplete(application, leadApplicant));
        assertFalse(rules.onlyMemberOfProjectTeamCanMarkSectionAsComplete(application, nonProjectTeamMember));

        assertTrue(rules.onlyMemberOfProjectTeamCanMarkSectionAsInComplete(application, leadApplicant));
        assertFalse(rules.onlyMemberOfProjectTeamCanMarkSectionAsInComplete(application, nonProjectTeamMember));
    }
}
