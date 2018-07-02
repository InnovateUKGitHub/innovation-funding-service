package org.innovateuk.ifs.invite.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.invite.transactional.ProjectInviteService;
import org.innovateuk.ifs.invite.transactional.ProjectInviteServiceImpl;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.InviteProjectResourceBuilder.newInviteProjectResource;
import static org.innovateuk.ifs.user.resource.Role.SYSTEM_REGISTRATION_USER;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testing how the secured methods in ProjectInviteService interact with Spring Security
 */
public class ProjectInviteServiceSecurityTest extends BaseServiceSecurityTest<ProjectInviteService> {

    private static final int ARRAY_SIZE_FOR_POST_FILTER_TESTS = 2;
    private ProjectInvitePermissionRules projectInvitePermissionRules;


    @Before
    public void lookupPermissionRules() {
        projectInvitePermissionRules = getMockPermissionRulesBean(ProjectInvitePermissionRules.class);

    }

    @Test
    public void testAcceptProjectInviteOnlyAllowedForSystemRegistrar() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.acceptProjectInvite("hash", 1L),
                SYSTEM_REGISTRATION_USER);
    }

    @Test
    public void testCheckUserExistingByInviteHashOnlyAllowedForSystemRegistrar() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.checkUserExistsForInvite("hash"),
                SYSTEM_REGISTRATION_USER);
    }

    @Test
    public void testGetInviteByHashOnlyAllowedForSystemRegistrar() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getInviteByHash("hash"), SYSTEM_REGISTRATION_USER);
    }

    @Test
    public void testGetUserByInviteHashOnlyAllowedForSystemRegistrar() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getUserByInviteHash("hash"),
                SYSTEM_REGISTRATION_USER);
    }

    @Test
    public void testSaveFinanceContact() {
        final InviteProjectResource invite = newInviteProjectResource().build();
        assertAccessDenied(
                () -> classUnderTest.saveProjectInvite(invite),
                () -> verify(projectInvitePermissionRules)
                        .partnersOnProjectCanSaveInvite(any(InviteProjectResource.class), any(UserResource.class))
        );
    }

    @Test
    public void testGetInvitesByProject() {
        long projectId = 1L;

        when(classUnderTestMock.getInvitesByProject(projectId))
                .thenReturn(serviceSuccess(newInviteProjectResource().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS)));

        ServiceResult<List<InviteProjectResource>> invitesByProject = classUnderTest.getInvitesByProject(projectId);

        verify(projectInvitePermissionRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS))
                .partnersOnProjectCanViewInvite(any(InviteProjectResource.class), any(UserResource.class));

        assertTrue(invitesByProject.getSuccess().isEmpty());
    }

    @Override
    protected Class<? extends ProjectInviteService> getClassUnderTest() {
        return ProjectInviteServiceImpl.class;
    }
}
