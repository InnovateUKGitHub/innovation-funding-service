package org.innovateuk.ifs.project.grantofferletter.workflow;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.grantofferletter.configuration.workflow.GrantOfferLetterWorkflowHandler;
import org.innovateuk.ifs.project.grantofferletter.domain.GOLProcess;
import org.innovateuk.ifs.project.grantofferletter.repository.GrantOfferLetterProcessRepository;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterEvent;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_MANAGER;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterEvent.*;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState.*;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource.stateInformationForNonPartnersView;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource.stateInformationForPartnersView;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class GrantOfferLetterWorkflowHandlerTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private GrantOfferLetterWorkflowHandler golWorkflowHandler;

    @Mock
    private GrantOfferLetterProcessRepository golProcessRepositoryMock;

    @Test
    public void testGetGrantOfferLetterStateAsPartner() {

        User partnerUser = newUser().build();
        User projectManagerUser = newUser().build();

        Project project = newProject().
                withProjectUsers(newProjectUser().
                        withUser(partnerUser, projectManagerUser).
                        withRole(PROJECT_PARTNER, PROJECT_MANAGER).
                        build(2)).
                build();

        GrantOfferLetterStateResource expectedStateInformation = stateInformationForPartnersView(APPROVED, SIGNED_GOL_APPROVED);
        assertGetGrantOfferLetterStateForUser(project, partnerUser, APPROVED, SIGNED_GOL_APPROVED, expectedStateInformation);
    }

    @Test
    public void testGetGrantOfferLetterStateAsPartnerWhenRejected() {

        User partnerUser = newUser().build();
        User projectManagerUser = newUser().build();

        Project project = newProject().
                withProjectUsers(newProjectUser().
                        withUser(partnerUser, projectManagerUser).
                        withRole(PROJECT_PARTNER, PROJECT_MANAGER).
                        build(2)).
                build();

        GrantOfferLetterStateResource expectedStateInformation = stateInformationForPartnersView(READY_TO_APPROVE, GOL_SIGNED);
        assertGetGrantOfferLetterStateForUser(project, partnerUser, SENT, SIGNED_GOL_REJECTED, expectedStateInformation);
    }

    @Test
    public void testGetGrantOfferLetterStateAsProjectManager() {

        User partnerUser = newUser().build();
        User projectManagerUser = newUser().build();

        Project project = newProject().
                withProjectUsers(newProjectUser().
                        withUser(partnerUser, projectManagerUser).
                        withRole(PROJECT_PARTNER, PROJECT_MANAGER).
                        build(2)).
                build();

        GrantOfferLetterStateResource expectedStateInformation = stateInformationForNonPartnersView(APPROVED, SIGNED_GOL_APPROVED);
        assertGetGrantOfferLetterStateForUser(project, projectManagerUser, APPROVED, SIGNED_GOL_APPROVED, expectedStateInformation);
    }

    @Test
    public void testGetGrantOfferLetterStateAsProjectManagerWhenRejected() {

        User partnerUser = newUser().build();
        User projectManagerUser = newUser().build();

        Project project = newProject().
                withProjectUsers(newProjectUser().
                        withUser(partnerUser, projectManagerUser).
                        withRole(PROJECT_PARTNER, PROJECT_MANAGER).
                        build(2)).
                build();

        GrantOfferLetterStateResource expectedStateInformation = stateInformationForNonPartnersView(SENT, SIGNED_GOL_REJECTED);
        assertGetGrantOfferLetterStateForUser(project, projectManagerUser, SENT, SIGNED_GOL_REJECTED, expectedStateInformation);
    }

    @Test
    public void testGetGrantOfferLetterStateAsInternalUser() {

        User partnerUser = newUser().build();
        User projectManagerUser = newUser().build();
        User internalUser = newUser().build();

        Project project = newProject().
                withProjectUsers(newProjectUser().
                        withUser(partnerUser, projectManagerUser).
                        withRole(PROJECT_PARTNER, PROJECT_MANAGER).
                        build(2)).
                build();

        GrantOfferLetterStateResource expectedStateInformation = stateInformationForNonPartnersView(APPROVED, SIGNED_GOL_APPROVED);
        assertGetGrantOfferLetterStateForUser(project, internalUser, APPROVED, SIGNED_GOL_APPROVED, expectedStateInformation);
    }

    @Test
    public void testGetGrantOfferLetterStateAsInternalUserWhenRejected() {

        User partnerUser = newUser().build();
        User projectManagerUser = newUser().build();
        User internalUser = newUser().build();

        Project project = newProject().
                withProjectUsers(newProjectUser().
                        withUser(partnerUser, projectManagerUser).
                        withRole(PROJECT_PARTNER, PROJECT_MANAGER).
                        build(2)).
                build();

        GrantOfferLetterStateResource expectedStateInformation = stateInformationForNonPartnersView(SENT, SIGNED_GOL_REJECTED);
        assertGetGrantOfferLetterStateForUser(project, internalUser, SENT, SIGNED_GOL_REJECTED, expectedStateInformation);
    }

    private void assertGetGrantOfferLetterStateForUser(Project project, User currentUser, GrantOfferLetterState state, GrantOfferLetterEvent lastEvent, GrantOfferLetterStateResource expectedStateInformation) {

        GOLProcess golProcess = new GOLProcess(newProjectUser().build(), project, new ActivityState(ActivityType.PROJECT_SETUP_GRANT_OFFER_LETTER, state.getBackingState()));
        golProcess.setProcessEvent(lastEvent.getType());

        when(authenticationHelperMock.getCurrentlyLoggedInUser()).thenReturn(ServiceResult.serviceSuccess(currentUser));
        when(golProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(golProcess);

        ServiceResult<GrantOfferLetterStateResource> result = golWorkflowHandler.getExtendedState(project);

        assertTrue(result.isSuccess());
        assertEquals(expectedStateInformation, result.getSuccess());
    }
}
