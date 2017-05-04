package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


public class ApplicationPermissionRulesTest extends BasePermissionRulesTest<ApplicationPermissionRules> {

    @Test
    public void isLeadApplicantWhenLoggedInAsLead() {

        long loggedInUserId = 2L;
        long leadApplicantUserId = 2L;
        long applicationId = 14L;

        UserResource loggedInUser = setUpIsLeadApplicantMocking(loggedInUserId, leadApplicantUserId, applicationId);
        assertTrue(rules.isLeadApplicant(applicationId, loggedInUser));
    }

    @Test
    public void isLeadApplicantWhenLoggedInAsNonLead() {

        long loggedInUserId = 7L;
        long leadApplicantUserId = 2L;
        long applicationId = 14L;

        UserResource loggedInUser = setUpIsLeadApplicantMocking(loggedInUserId, leadApplicantUserId, applicationId);
        assertFalse(rules.isLeadApplicant(applicationId, loggedInUser));
    }

    private UserResource setUpIsLeadApplicantMocking(long loggedInUserId, long leadApplicantUserId, long applicationId) {
        ApplicationResource applicationResource = ApplicationResourceBuilder.newApplicationResource().build();
        when(applicationServiceMock.getById(applicationId)).thenReturn(applicationResource);

        ProcessRoleResource processRoleResource = ProcessRoleResourceBuilder.newProcessRoleResource()
                .withUserId(leadApplicantUserId)
                .build();
        when(userServiceMock.getLeadApplicantProcessRoleOrNull(applicationResource)).thenReturn(processRoleResource);

        UserResource loggedInUser = new UserResource();
        loggedInUser.setId(loggedInUserId);
        return loggedInUser;
    }

    @Test
    public void applicationNotYetSubmittedWhenApplicationIsSubmitted() {

        long applicationId = 14L;

        setUpApplicationNotYetSubmittedMocking(applicationId, ApplicationState.SUBMITTED);

        assertFalse(rules.applicationNotYetSubmitted(applicationId, new UserResource()));
    }

    @Test
    public void applicationNotYetSubmittedWhenApplicationIsRejected() {

        long applicationId = 14L;

        setUpApplicationNotYetSubmittedMocking(applicationId, ApplicationState.REJECTED);

        assertFalse(rules.applicationNotYetSubmitted(applicationId, new UserResource()));
    }

    @Test
    public void applicationNotYetSubmittedWhenApplicationIsApproved() {

        long applicationId = 14L;

        setUpApplicationNotYetSubmittedMocking(applicationId, ApplicationState.APPROVED);

        assertFalse(rules.applicationNotYetSubmitted(applicationId, new UserResource()));
    }

    @Test
    public void applicationNotYetSubmittedWhenApplicationIsNotSubmitted() {

        long applicationId = 14L;

        setUpApplicationNotYetSubmittedMocking(applicationId, ApplicationState.OPEN);

        assertTrue(rules.applicationNotYetSubmitted(applicationId, new UserResource()));
    }

    private void setUpApplicationNotYetSubmittedMocking(long applicationId, ApplicationState applicationState) {
        ApplicationResource applicationResource = ApplicationResourceBuilder.newApplicationResource()
                .withApplicationState(applicationState)
                .build();
        when(applicationServiceMock.getById(applicationId)).thenReturn(applicationResource);
    }

    @Override
    protected ApplicationPermissionRules supplyPermissionRulesUnderTest() {
        return new ApplicationPermissionRules();
    }
}

