package org.innovateuk.ifs.application.util;


import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.commons.error.exception.ForbiddenActionException;
import org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


public class ApplicationUtilTest extends BaseUnitTest {

    @InjectMocks
    private ApplicationUtil applicationUtil;

    @Test
    public void checkIfApplicationAlreadySubmittedWhenApplicationIsSubmitted() throws Exception {

        ApplicationResource applicationResource = ApplicationResourceBuilder.newApplicationResource()
                .withApplicationState(ApplicationState.SUBMITTED)
                .build();

        try {
            applicationUtil.checkIfApplicationAlreadySubmitted(applicationResource);
        } catch (Exception e) {
            // We expect an exception to be thrown
            Assert.assertTrue(e instanceof ForbiddenActionException);

            // This exception flow is the only expected flow, so return from here and assertFalse if no exception
            return;
        }
        // Should not reach here - we must get an exception
        assertFalse(true);
    }

    @Test
    public void checkIfApplicationAlreadySubmittedWhenApplicationIsRejected() throws Exception {

        ApplicationResource applicationResource = ApplicationResourceBuilder.newApplicationResource()
                .withApplicationState(ApplicationState.REJECTED)
                .build();

        try {
            applicationUtil.checkIfApplicationAlreadySubmitted(applicationResource);
        } catch (Exception e) {
            // We expect an exception to be thrown
            Assert.assertTrue(e instanceof ForbiddenActionException);

            // This exception flow is the only expected flow, so return from here and assertFalse if no exception
            return;
        }
        // Should not reach here - we must get an exception
        assertFalse(true);
    }

    @Test
    public void checkIfApplicationAlreadySubmittedWhenApplicationIsApproved() throws Exception {

        ApplicationResource applicationResource = ApplicationResourceBuilder.newApplicationResource()
                .withApplicationState(ApplicationState.APPROVED)
                .build();

        try {
            applicationUtil.checkIfApplicationAlreadySubmitted(applicationResource);
        } catch (Exception e) {
            // We expect an exception to be thrown
            Assert.assertTrue(e instanceof ForbiddenActionException);

            // This exception flow is the only expected flow, so return from here and assertFalse if no exception
            return;
        }
        // Should not reach here - we must get an exception
        assertFalse(true);
    }

    @Test
    public void checkIfApplicationAlreadySubmittedWhenApplicationIsNotSubmitted() throws Exception {

        ApplicationResource applicationResource = ApplicationResourceBuilder.newApplicationResource()
                .withApplicationState(ApplicationState.OPEN)
                .build();

        applicationUtil.checkIfApplicationAlreadySubmitted(applicationResource);

        // We don't expect a ForbiddenActionException for our scenario, so if we get here, then pass this test case
        assertTrue(true);
    }

    @Test
    public void checkUserIsLeadApplicantWhenIsLoggedInNonLead() throws Exception {

        long loggedInUserId = 7L;
        long leadApplicantUserId = 2L;
        ApplicationResource applicationResource = ApplicationResourceBuilder.newApplicationResource().build();

        ProcessRoleResource processRoleResource = ProcessRoleResourceBuilder.newProcessRoleResource()
                .withUserId(leadApplicantUserId)
                .build();
        when(userService.getLeadApplicantProcessRoleOrNull(applicationResource)).thenReturn(processRoleResource);

        try {
            applicationUtil.checkUserIsLeadApplicant(applicationResource, loggedInUserId);
        } catch (Exception e) {
            // We expect an exception to be thrown
            Assert.assertTrue(e instanceof ForbiddenActionException);

            // This exception flow is the only expected flow, so return from here and assertFalse if no exception
            return;
        }
        // Should not reach here - we must get an exception
        assertFalse(true);
    }

    @Test
    public void checkUserIsLeadApplicantWhenIsLoggedInAsLead() throws Exception {

        long loggedInUserId = 2L;
        long leadApplicantUserId = 2L;
        ApplicationResource applicationResource = ApplicationResourceBuilder.newApplicationResource().build();

        ProcessRoleResource processRoleResource = ProcessRoleResourceBuilder.newProcessRoleResource()
                .withUserId(leadApplicantUserId)
                .build();
        when(userService.getLeadApplicantProcessRoleOrNull(applicationResource)).thenReturn(processRoleResource);

        applicationUtil.checkUserIsLeadApplicant(applicationResource, loggedInUserId);

        // We don't expect a ForbiddenActionException for our scenario, so if we get here, then pass this test case
        assertTrue(true);
    }
}
