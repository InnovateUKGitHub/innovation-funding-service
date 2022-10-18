package org.innovateuk.ifs.application.forms.questions.team.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.resource.ApplicationCompositeId;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.innovateuk.ifs.application.resource.ApplicationState.OPENED;
import static org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED;
import static org.innovateuk.ifs.competition.resource.CollaborationLevel.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrganisationPermissionRulesTest extends BasePermissionRulesTest<OrganisationPermissionRules> {

    @Mock
    private ApplicationService applicationServiceMock;

    @Mock
    private UserService userServiceMock;

    @Test
    public void viewAddOrganisationPageWhenNotLoggedInAsLeadAndApplicationNotSubmitted() {
        long loggedInUserId = 7L;
        long leadApplicantUserId = 2L;
        ApplicationCompositeId applicationId = ApplicationCompositeId.id(14L);

        UserResource loggedInUser = setUpMocking(loggedInUserId, leadApplicantUserId, applicationId.id(),
                OPENED, COLLABORATIVE);
        assertFalse(rules.viewAddOrganisationPage(applicationId, loggedInUser));
    }

    @Test
    public void viewAddOrganisationPageWhenNotLoggedInAsLeadAndApplicationSubmitted() {
        long loggedInUserId = 7L;
        long leadApplicantUserId = 2L;
        ApplicationCompositeId applicationId = ApplicationCompositeId.id(14L);

        UserResource loggedInUser = setUpMocking(loggedInUserId, leadApplicantUserId, applicationId.id(),
                SUBMITTED, COLLABORATIVE);
        assertFalse(rules.viewAddOrganisationPage(applicationId, loggedInUser));
    }

    @Test
    public void viewAddOrganisationPageWhenLoggedInAsLeadAndApplicationSubmitted() {
        long loggedInUserId = 2L;
        long leadApplicantUserId = 2L;
        ApplicationCompositeId applicationId = ApplicationCompositeId.id(14L);

        UserResource loggedInUser = setUpMocking(loggedInUserId, leadApplicantUserId, applicationId.id(),
                SUBMITTED, COLLABORATIVE);
        assertFalse(rules.viewAddOrganisationPage(applicationId, loggedInUser));
    }

    @Test
    public void viewAddOrganisationPageWhenLoggedInAsLeadAndApplicationNotSubmitted() {
        long loggedInUserId = 2L;
        long leadApplicantUserId = 2L;
        ApplicationCompositeId applicationId = ApplicationCompositeId.id(14L);

        UserResource loggedInUser = setUpMocking(loggedInUserId, leadApplicantUserId, applicationId.id(),
                OPENED, COLLABORATIVE);
        assertTrue(rules.viewAddOrganisationPage(applicationId, loggedInUser));
    }

    @Test
    public void viewAddOrganisationPageWhenCollaborationLevelIsSingle() {
        long loggedInUserId = 2L;
        long leadApplicantUserId = 2L;
        ApplicationCompositeId applicationId = ApplicationCompositeId.id(14L);

        UserResource loggedInUser = setUpMocking(loggedInUserId, leadApplicantUserId, applicationId.id(),
                OPENED, SINGLE);
        assertFalse(rules.viewAddOrganisationPage(applicationId, loggedInUser));
    }

    @Test
    public void viewAddOrganisationPageWhenCollaborationLevelIsSingleOrCollaborative() {
        long loggedInUserId = 2L;
        long leadApplicantUserId = 2L;
        ApplicationCompositeId applicationId = ApplicationCompositeId.id(14L);

        UserResource loggedInUser = setUpMocking(loggedInUserId, leadApplicantUserId, applicationId.id(),
                OPENED, SINGLE_OR_COLLABORATIVE);
        assertTrue(rules.viewAddOrganisationPage(applicationId, loggedInUser));
    }

    @Test
    public void viewAddOrganisationPageWhenCollaborationLevelIsCollaborative() {
        long loggedInUserId = 2L;
        long leadApplicantUserId = 2L;
        ApplicationCompositeId applicationId = ApplicationCompositeId.id(14L);

        UserResource loggedInUser = setUpMocking(loggedInUserId, leadApplicantUserId, applicationId.id(),
                OPENED, COLLABORATIVE);
        assertTrue(rules.viewAddOrganisationPage(applicationId, loggedInUser));
    }

    @Test
    public void addNewOrganisationWhenNotLoggedInAsLeadAndApplicationNotSubmitted() {
        long loggedInUserId = 7L;
        long leadApplicantUserId = 2L;
        ApplicationCompositeId applicationId = ApplicationCompositeId.id(14L);

        UserResource loggedInUser = setUpMocking(loggedInUserId, leadApplicantUserId, applicationId.id(),
                OPENED, COLLABORATIVE);
        assertFalse(rules.addNewOrganisation(applicationId, loggedInUser));
    }

    @Test
    public void addNewOrganisationWhenNotLoggedInAsLeadAndApplicationSubmitted() {
        long loggedInUserId = 7L;
        long leadApplicantUserId = 2L;
        ApplicationCompositeId applicationId = ApplicationCompositeId.id(14L);

        UserResource loggedInUser = setUpMocking(loggedInUserId, leadApplicantUserId, applicationId.id(),
                SUBMITTED, COLLABORATIVE);
        assertFalse(rules.addNewOrganisation(applicationId, loggedInUser));
    }

    @Test
    public void addNewOrganisationWhenLoggedInAsLeadAndApplicationSubmitted() {
        long loggedInUserId = 2L;
        long leadApplicantUserId = 2L;
        ApplicationCompositeId applicationId = ApplicationCompositeId.id(14L);

        UserResource loggedInUser = setUpMocking(loggedInUserId, leadApplicantUserId, applicationId.id(),
                SUBMITTED, COLLABORATIVE);
        assertFalse(rules.addNewOrganisation(applicationId, loggedInUser));
    }

    @Test
    public void addNewOrganisationWhenLoggedInAsLeadAndApplicationNotSubmitted() {
        long loggedInUserId = 2L;
        long leadApplicantUserId = 2L;
        ApplicationCompositeId applicationId = ApplicationCompositeId.id(14L);

        UserResource loggedInUser = setUpMocking(loggedInUserId, leadApplicantUserId, applicationId.id(),
                OPENED, COLLABORATIVE);
        assertTrue(rules.addNewOrganisation(applicationId, loggedInUser));
    }

    private UserResource setUpMocking(long loggedInUserId,
                                      long leadApplicantUserId,
                                      long applicationId,
                                      ApplicationState applicationState,
                                      CollaborationLevel collaborationLevel) {
        ApplicationResource applicationResource = ApplicationResourceBuilder.newApplicationResource()
                .withId(applicationId)
                .withApplicationState(applicationState)
                .withCollaborationLevel(collaborationLevel)
                .build();
        when(applicationServiceMock.getById(applicationId)).thenReturn(applicationResource);

        ProcessRoleResource processRoleResource = ProcessRoleResourceBuilder.newProcessRoleResource()
                .withUserId(leadApplicantUserId)
                .build();
        when(userServiceMock.getLeadApplicantProcessRole(applicationResource.getId())).thenReturn
                (processRoleResource);

        UserResource loggedInUser = new UserResource();
        loggedInUser.setId(loggedInUserId);
        return loggedInUser;
    }

    @Override
    protected OrganisationPermissionRules supplyPermissionRulesUnderTest() {
        return new OrganisationPermissionRules();
    }
}

