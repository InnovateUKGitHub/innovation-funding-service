package com.worth.ifs.application.security;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.application.builder.ApplicationStatusResourceBuilder;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.ApplicationStatusResource;
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
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class ApplicationRulesTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private ApplicationRules applicationRules;

    private ApplicationStatusResource applicationStatusOpen;
    private ApplicationResource applicationResource1;
    private ApplicationResource applicationResource2;
    private Application application1;
    private Application application2;
    private ProcessRole processRole1;
    private ProcessRole processRole2;
    private User user1;
    private User user2;

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
        applicationStatusOpen = ApplicationStatusResourceBuilder.newApplicationStatusResource().withName(ApplicationStatusConstants.OPEN).build();
        applicationResource1 = newApplicationResource().withProcessRoles(processRole1).withApplicationStatus(applicationStatusOpen).build();
        applicationResource2 = newApplicationResource().withProcessRoles(processRole2).build();
        application1 = newApplication().withId(applicationResource1.getId()).withProcessRoles(processRole1).build();
        application2 = newApplication().withId(applicationResource2.getId()).withProcessRoles(processRole2).build();
        processRole1.setApplication(application1);
        processRole2.setApplication(application2);

        when(applicationRepositoryMock.exists(applicationResource1.getId())).thenReturn(true);
        when(applicationRepositoryMock.exists(applicationResource2.getId())).thenReturn(true);
        when(applicationRepositoryMock.exists(null)).thenReturn(false);

        when(roleRepositoryMock.findByName(anyString())).thenReturn(singletonList(leadApplicantRole));

        when(processRoleRepositoryMock.findByUserAndApplicationId(user1, applicationResource1.getId())).thenReturn(singletonList(processRole1));
        when(processRoleRepositoryMock.findByUserAndApplicationId(user1, applicationResource2.getId())).thenReturn(emptyList());
        when(processRoleRepositoryMock.findByUserAndApplicationId(user2, applicationResource1.getId())).thenReturn(emptyList());
        when(processRoleRepositoryMock.findByUserAndApplicationId(user2, applicationResource2.getId())).thenReturn(singletonList(processRole1));

        when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationId(user1.getId(), leadApplicantRole, applicationResource1.getId())).thenReturn(singletonList(processRole1));
        when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationId(user1.getId(), leadApplicantRole, applicationResource2.getId())).thenReturn(emptyList());
        when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationId(user2.getId(), leadApplicantRole, applicationResource1.getId())).thenReturn(emptyList());
        when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationId(user2.getId(), leadApplicantRole, applicationResource2.getId())).thenReturn(emptyList());
    }

    @Test
    public void applicantCanSeeConnectedApplicationResourceTest() {
        assertTrue(applicationRules.applicantCanSeeConnectedApplicationResource(applicationResource1, user1));
        assertTrue(applicationRules.applicantCanSeeConnectedApplicationResource(applicationResource2, user2));
    }

    @Test
    public void applicantCannotSeeUnconnectedApplicationResourceTest() {
        assertFalse(applicationRules.applicantCanSeeConnectedApplicationResource(applicationResource1, user2));
        assertFalse(applicationRules.applicantCanSeeConnectedApplicationResource(applicationResource2, user1));
    }

    @Test
    public void onlyLeadApplicantCanChangeApplicationResourceTest(){
        assertTrue(applicationRules.onlyLeadApplicantCanChangeApplicationResource(applicationResource1, user1));
        assertFalse(applicationRules.onlyLeadApplicantCanChangeApplicationResource(applicationResource1, user2));
    }

    @Test
    public void userIsConnectedToApplicationResourceTest(){
        assertTrue(applicationRules.userIsConnectedToApplicationResource(applicationResource1, user1));
    }

    @Test
    public void userIsLeadApplicantOnApplicationResourceTest(){
        assertTrue(applicationRules.userIsLeadApplicantOnApplicationResource(applicationResource1, user1));
    }

    @Test
    public void applicationExistsTest(){
        ApplicationResource noId = newApplicationResource().build();

        assertTrue("applicationExists should return true when called with existing application", applicationRules.applicationExists(applicationResource1));
        assertFalse("applicationExists should return false when called with non-existing application", applicationRules.applicationExists(noId));
        noId.setId(null);
        assertFalse("applicationExists should return false when called with application without an id", applicationRules.applicationExists(noId));
    }
}
