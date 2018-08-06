package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder.newAssessorProfileResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AssessorPermissionRulesTest extends BasePermissionRulesTest<AssessorPermissionRules> {

    private long assessorId;
    private UserResource assessorUser;
    private UserResource otherUser;
    private UserResource adminUser;

    @Override
    protected AssessorPermissionRules supplyPermissionRulesUnderTest() {
        return new AssessorPermissionRules();
    }

    @Before
    public void setup() {
        assessorId = 1L;
        assessorUser = newUserResource()
                .withId(assessorId)
                .build();
        otherUser = newUserResource().build();
        adminUser = newUserResource()
                .withRoleGlobal(Role.COMP_ADMIN)
                .build();
    }

    @Test
    public void assessorsCanReadTheirOwnProfile() {
        AssessorProfileResource assessorProfileResource = newAssessorProfileResource()
                .withUser(assessorUser)
                .build();
        assertTrue(rules.userCanReadAssessorProfile(assessorProfileResource, assessorUser));
    }

    @Test
    public void usersCannotReadOthersProfiles() {
        AssessorProfileResource assessorProfileResource = newAssessorProfileResource()
                .withUser(assessorUser)
                .build();
        assertFalse(rules.userCanReadAssessorProfile(assessorProfileResource, otherUser));
    }

    @Test
    public void compAdminUsersCanReadAllProfiles() {
        AssessorProfileResource assessorProfileResource = newAssessorProfileResource()
                .withUser(assessorUser)
                .build();
        assertTrue(rules.userCanReadAssessorProfile(assessorProfileResource, adminUser));
    }

}
