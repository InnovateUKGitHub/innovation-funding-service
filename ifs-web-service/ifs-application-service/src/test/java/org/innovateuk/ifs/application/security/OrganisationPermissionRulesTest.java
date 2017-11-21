package org.innovateuk.ifs.application.team.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.resource.ApplicationCompositeId;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class OrganisationPermissionRulesTest extends BasePermissionRulesTest<OrganisationPermissionRules> {

    @Test
    public void viewAddOrganisationPageWhenNotLoggedInAsLeadAndApplicationNotSubmitted() {

        long loggedInUserId = 7L;
        long leadApplicantUserId = 2L;
        ApplicationCompositeId applicationId = ApplicationCompositeId.id(14L);

        UserResource loggedInUser = setUpMocking(loggedInUserId, leadApplicantUserId, applicationId.id(), ApplicationState.OPEN);
        assertFalse(rules.viewAddOrganisationPage(applicationId, loggedInUser));
    }

    @Test
    public void viewAddOrganisationPageWhenNotLoggedInAsLeadAndApplicationSubmitted() {

        long loggedInUserId = 7L;
        long leadApplicantUserId = 2L;
        ApplicationCompositeId applicationId = ApplicationCompositeId.id(14L);

        UserResource loggedInUser = setUpMocking(loggedInUserId, leadApplicantUserId, applicationId.id(), ApplicationState.SUBMITTED);
        assertFalse(rules.viewAddOrganisationPage(applicationId, loggedInUser));
    }

    @Test
    public void viewAddOrganisationPageWhenLoggedInAsLeadAndApplicationSubmitted() {

        long loggedInUserId = 2L;
        long leadApplicantUserId = 2L;
        ApplicationCompositeId applicationId = ApplicationCompositeId.id(14L);

        UserResource loggedInUser = setUpMocking(loggedInUserId, leadApplicantUserId, applicationId.id(), ApplicationState.SUBMITTED);
        assertFalse(rules.viewAddOrganisationPage(applicationId, loggedInUser));
    }

    @Test
    public void viewAddOrganisationPageWhenLoggedInAsLeadAndApplicationNotSubmitted() {

        long loggedInUserId = 2L;
        long leadApplicantUserId = 2L;
        ApplicationCompositeId applicationId = ApplicationCompositeId.id(14L);

        UserResource loggedInUser = setUpMocking(loggedInUserId, leadApplicantUserId, applicationId.id(), ApplicationState.OPEN);
        assertTrue(rules.viewAddOrganisationPage(applicationId, loggedInUser));
    }

    @Test
    public void addNewOrganisationWhenNotLoggedInAsLeadAndApplicationNotSubmitted() {

        long loggedInUserId = 7L;
        long leadApplicantUserId = 2L;
        ApplicationCompositeId applicationId = ApplicationCompositeId.id(14L);

        UserResource loggedInUser = setUpMocking(loggedInUserId, leadApplicantUserId, applicationId.id(), ApplicationState.OPEN);
        assertFalse(rules.addNewOrganisation(applicationId, loggedInUser));
    }

    @Test
    public void addNewOrganisationWhenNotLoggedInAsLeadAndApplicationSubmitted() {

        long loggedInUserId = 7L;
        long leadApplicantUserId = 2L;
        ApplicationCompositeId applicationId = ApplicationCompositeId.id(14L);

        UserResource loggedInUser = setUpMocking(loggedInUserId, leadApplicantUserId, applicationId.id(), ApplicationState.SUBMITTED);
        assertFalse(rules.addNewOrganisation(applicationId, loggedInUser));
    }

    @Test
    public void addNewOrganisationWhenLoggedInAsLeadAndApplicationSubmitted() {

        long loggedInUserId = 2L;
        long leadApplicantUserId = 2L;
        ApplicationCompositeId applicationId = ApplicationCompositeId.id(14L);

        UserResource loggedInUser = setUpMocking(loggedInUserId, leadApplicantUserId, applicationId.id(), ApplicationState.SUBMITTED);
        assertFalse(rules.addNewOrganisation(applicationId, loggedInUser));
    }

    @Test
    public void addNewOrganisationWhenLoggedInAsLeadAndApplicationNotSubmitted() {

        long loggedInUserId = 2L;
        long leadApplicantUserId = 2L;
        ApplicationCompositeId applicationId = ApplicationCompositeId.id(14L);

        UserResource loggedInUser = setUpMocking(loggedInUserId, leadApplicantUserId, applicationId.id(), ApplicationState.OPEN);
        assertTrue(rules.addNewOrganisation(applicationId, loggedInUser));
    }

    private UserResource setUpMocking(long loggedInUserId, long leadApplicantUserId, long applicationId, ApplicationState applicationState) {
        ApplicationResource applicationResource = ApplicationResourceBuilder.newApplicationResource()
                .withApplicationState(applicationState)
                .build();
        when(applicationServiceMock.getById(applicationId)).thenReturn(applicationResource);

        ProcessRoleResource processRoleResource = ProcessRoleResourceBuilder.newProcessRoleResource()
                .withUserId(leadApplicantUserId)
                .build();
        when(userServiceMock.getLeadApplicantProcessRoleOrNull(applicationResource)).thenReturn(processRoleResource);

        UserResource loggedInUser = new UserResource();
        loggedInUser.setId(loggedInUserId);
        return loggedInUser;
    }

    @Override
    protected OrganisationPermissionRules supplyPermissionRulesUnderTest() {
        return new OrganisationPermissionRules();
    }
}

