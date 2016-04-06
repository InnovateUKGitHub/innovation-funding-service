package com.worth.ifs.assessment.security;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.assessment.dto.Feedback;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.InjectMocks;

import static com.worth.ifs.BuilderAmendFunctions.id;
import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.application.builder.ResponseBuilder.newResponse;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.domain.UserRoleType.APPLICANT;
import static com.worth.ifs.user.domain.UserRoleType.ASSESSOR;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for permissions checking around the management of Assessor Feedback, and who can and can't perform
 * actions upon it.
 */
public class FeedbackRulesTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private FeedbackRules rules = new FeedbackRules();

    @Test
    public void test_assessorCanReadTheirOwnFeedback() {

        User user = newUser().with(id(123L)).build();
        UserResource userResource = newUserResource().with(id(123L)).build();
        Role assessorRole = newRole().with(id(456L)).withType(ASSESSOR).build();
        ProcessRole assessorProcessRole = newProcessRole().with(id(789L)).withUser(user).withRole(assessorRole).build();
        Application application = newApplication().with(id(654L)).build();
        Response response = newResponse().with(id(987L)).withApplication(application).build();

        Feedback feedback = new Feedback();
        feedback.setResponseId(response.getId()).setAssessorUserId(userResource.getId());

        when(responseRepositoryMock.findOne(response.getId())).thenReturn(response);
        when(roleRepositoryMock.findByName(ASSESSOR.getName())).thenReturn(singletonList(assessorRole));
        when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationId(userResource.getId(), assessorRole, application.getId())).thenReturn(singletonList(assessorProcessRole));

        assertTrue(rules.assessorCanReadTheirOwnFeedback(feedback, userResource));
    }

    @Test
    public void test_assessorCanReadTheirOwnFeedback_doesntBelongToThisUser() {

        long differentUserId = 999L;
        User user = newUser().with(id(123L)).build();
        UserResource userResource = newUserResource().with(id(123L)).build();

        Role assessorRole = newRole().with(id(456L)).withType(ASSESSOR).build();
        ProcessRole assessorProcessRole = newProcessRole().with(id(789L)).withUser(user).withRole(assessorRole).build();
        Application application = newApplication().with(id(654L)).build();

        //
        // In this test the Response belongs to another User (another ProcessRole)
        //
        Response response = newResponse().with(id(987L)).withApplication(application).build();

        Feedback feedback = new Feedback();
        feedback.setResponseId(response.getId()).setAssessorUserId(differentUserId);

        when(responseRepositoryMock.findOne(response.getId())).thenReturn(response);
        when(roleRepositoryMock.findByName(ASSESSOR.getName())).thenReturn(singletonList(assessorRole));
        when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationId(userResource.getId(), assessorRole, application.getId())).thenReturn(singletonList(assessorProcessRole));

        assertFalse(rules.assessorCanReadTheirOwnFeedback(feedback, userResource));

        verify(responseRepositoryMock, never()).findOne(response.getId());
        verify(roleRepositoryMock, never()).findByName(ASSESSOR.getName());
        verify(processRoleRepositoryMock, never()).findByUserIdAndRoleAndApplicationId(userResource.getId(), assessorRole, application.getId());
    }

    @Test
    public void test_assessorCanUpdateTheirOwnFeedback() {

        UserResource userResource = newUserResource().with(id(123L)).build();
        User user = newUser().with(id(123L)).build();

        Role assessorRole = newRole().with(id(456L)).withType(ASSESSOR).build();
        ProcessRole assessorProcessRole = newProcessRole().with(id(789L)).withUser(user).withRole(assessorRole).build();
        Application application = newApplication().with(id(654L)).build();
        Response response = newResponse().with(id(987L)).withApplication(application).withUpdatedBy(assessorProcessRole).build();

        Feedback feedback = new Feedback();
        feedback.setResponseId(response.getId()).setAssessorUserId(userResource.getId());

        when(responseRepositoryMock.findOne(response.getId())).thenReturn(response);
        when(roleRepositoryMock.findByName(ASSESSOR.getName())).thenReturn(singletonList(assessorRole));
        when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationId(userResource.getId(), assessorRole, application.getId())).thenReturn(singletonList(assessorProcessRole));

        assertTrue(rules.assessorCanUpdateTheirOwnFeedback(feedback, userResource));

        verify(responseRepositoryMock).findOne(response.getId());
        verify(roleRepositoryMock).findByName(ASSESSOR.getName());
        verify(processRoleRepositoryMock).findByUserIdAndRoleAndApplicationId(userResource.getId(), assessorRole, application.getId());
    }

    @Test
    public void test_assessorCanUpdateTheirOwnFeedback_doesntBelongToThisUser() {

        long differentUserId = 9999L;
        UserResource userResource = newUserResource().with(id(123L)).build();
        User user = newUser().with(id(123L)).build();

        Role assessorRole = newRole().with(id(456L)).withType(ASSESSOR).build();
        ProcessRole assessorProcessRole = newProcessRole().with(id(789L)).withUser(user).withRole(assessorRole).build();
        Application application = newApplication().with(id(654L)).build();

        //
        // In this test the Response belongs to another User
        //
        Response response = newResponse().with(id(987L)).withApplication(application).build();

        Feedback feedback = new Feedback();
        feedback.setResponseId(response.getId()).setAssessorUserId(differentUserId);

        assertFalse(rules.assessorCanUpdateTheirOwnFeedback(feedback, userResource));

        verify(responseRepositoryMock, never()).findOne(response.getId());
        verify(roleRepositoryMock, never()).findByName(ASSESSOR.getName());
        verify(processRoleRepositoryMock, never()).findByUserIdAndRoleAndApplicationId(userResource.getId(), assessorRole, application.getId());
    }

    @Test
    public void test_assessorUpdateTheirOwnFeedback_wrongProcessRoleType_shouldNeverHappen() {

        User user = newUser().build();
        UserResource userResource = newUserResource().build();

        Role applicantRole = newRole().withType(APPLICANT).build();
        ProcessRole applicantProcessRole = newProcessRole().withUser(user).withRole(applicantRole).build();

        Feedback feedback = new Feedback();
        feedback.setAssessorUserId(applicantProcessRole.getId());

        when(processRoleRepositoryMock.findOne(applicantProcessRole.getId())).thenReturn(applicantProcessRole);

        assertFalse(rules.assessorCanUpdateTheirOwnFeedback(feedback, userResource));
    }
}
