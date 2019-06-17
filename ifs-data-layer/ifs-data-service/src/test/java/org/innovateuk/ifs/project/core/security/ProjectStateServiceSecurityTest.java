package org.innovateuk.ifs.project.core.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.project.core.transactional.ProjectStateService;
import org.innovateuk.ifs.project.core.transactional.ProjectStateServiceImpl;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;

/**
 * Testing how the secured methods in ProjectStateService interact with Spring Security
 */
public class ProjectStateServiceSecurityTest extends BaseServiceSecurityTest<ProjectStateService> {

    @Test
    public void testWithdrawProject() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.withdrawProject(123L),
                Role.IFS_ADMINISTRATOR);
    }

    @Test
    public void handleProjectOffline() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.handleProjectOffline(123L),
                Role.IFS_ADMINISTRATOR);
    }

    @Test
    public void completeProjectOffline() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.completeProjectOffline(123L),
                Role.IFS_ADMINISTRATOR);
    }

    @Override
    protected Class<? extends ProjectStateService> getClassUnderTest() {
        return ProjectStateServiceImpl.class;
    }
}

