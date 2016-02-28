package com.worth.ifs.application.security;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserRoleType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.domain.UserRoleType.LEADAPPLICANT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ApplicationRulesTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private ApplicationRules applicationRules;

    private ApplicationResource application1;
    private ApplicationResource application2;
    private ProcessRole processRole1;
    private ProcessRole processRole2;
    private User user1;
    private User user2;

    private long applicationId = 456L;
    private long processRoleId = 789L;
    private Role leadApplicantRole = newRole().withType(LEADAPPLICANT).build();
    private Role collaboratorRole = newRole().withType(UserRoleType.COLLABORATOR).build();
    private Role applicantRole = newRole().withType(UserRoleType.APPLICANT).build();
    private Role assessorRole = newRole().withType(UserRoleType.ASSESSOR).build();

    @Before
    public void setup(){
        user1 = newUser().build();
        user2 = newUser().build();
        processRole1 = newProcessRole().withRole(leadApplicantRole).build();
        processRole2 = newProcessRole().withRole(applicantRole).build();
        application1 = newApplicationResource().withProcessRoles(processRole1).build();
        application2 = newApplicationResource().withProcessRoles(processRole2).build();
        processRole1.setApplication(newApplication().withProcessRoles(processRole1).build());
        processRole2.setApplication(newApplication().withProcessRoles(processRole2).build());
    }

    /*@Test
    public void applicantCanSeeConnectedApplicationResourceTest() {

        assertTrue(applicationRules.applicantCanSeeConnectedApplicationResource(application, user));
    }

    @Test
    public void onlyLeadApplicantCanChangeApplicationResourceTest(){
        assertTrue(applicationRules.onlyLeadApplicantCanChangeApplicationResource(application, user);
    }

    @Test
    public void userIsConnectedToApplicationResourceTest(){
        assertTrue(applicationRules.userIsConnectedToApplicationResource(application, user));
    }

    @Test
    public void userIsLeadApplicantOnApplicationResourceTest(){
        assertTrue(applicationRules.userIsLeadApplicantOnApplicationResource(application, user));
    }*/

    @Test
    public void applicationExistsTest(){
        ApplicationResource noId = newApplicationResource().build();
        noId.setId(null);

        when(applicationRepositoryMock.exists(application1.getId())).thenReturn(true);
        when(applicationRepositoryMock.exists(application2.getId())).thenReturn(false);
        when(applicationRepositoryMock.exists(null)).thenReturn(false);

        assertTrue("applicationExists should return true when called with existing application", applicationRules.applicationExists(application1));
        assertFalse("applicationExists should return false when called with non-existing application", applicationRules.applicationExists(application2));
        assertFalse("applicationExists should return false when called with application without an id", applicationRules.applicationExists(noId));
    }
}
