package com.worth.ifs.invite.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.controller.ApplicationController;
import com.worth.ifs.application.controller.QuestionController;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.constant.InviteStatus;
import com.worth.ifs.invite.domain.ApplicationInvite;
import com.worth.ifs.invite.repository.ApplicationInviteRepository;
import com.worth.ifs.invite.resource.ApplicationInviteResource;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResultsResource;
import com.worth.ifs.user.controller.UserController;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.mapper.UserMapper;
import com.worth.ifs.user.resource.UserResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.worth.ifs.commons.security.SecuritySetter.swapOutForUser;
import static org.junit.Assert.*;

@Rollback
public class ApplicationInviteControllerIntegrationTest extends BaseControllerIntegrationTest<ApplicationInviteController> {

    public static final long APPLICATION_ID = 1L;

    private QuestionController questionController;
    private Long leadApplicantProcessRole;
    private Long leadApplicantId;
    private ApplicationController applicationController;
    private InviteOrganisationController inviteOrganisationController;
    private UserController userController;

    @Autowired
    ApplicationInviteRepository applicationInviteRepository;

    @Autowired
    UserMapper userMapper;
    private UserResource userResource;

    @Autowired
    @Override
    protected void setControllerUnderTest(ApplicationInviteController controller) {
        this.controller = controller;
    }
    @Autowired
    protected void setApplicationController(ApplicationController controller) {
        this.applicationController = controller;
    }

    @Autowired
    protected void setInviteOrganisationController(InviteOrganisationController controller) {
        this.inviteOrganisationController = controller;
    }

    @Autowired
    public void setUserController(UserController userController) {
        this.userController = userController;
    }

    @Before
    public void setUp() {
        leadApplicantId = 1L;
        leadApplicantProcessRole = 1L;
        List<ProcessRole> proccessRoles = new ArrayList<>();
        proccessRoles.add(
                new ProcessRole(
                        leadApplicantProcessRole,
                        null,
                        new Application(
                                APPLICATION_ID,
                                "",
                                new ApplicationStatus(
                                        ApplicationStatusConstants.CREATED.getId(),
                                        ApplicationStatusConstants.CREATED.getName()
                                )
                        ),
                        null,
                        null
                )
        );
        User user = new User(leadApplicantId, "steve", "smith", "steve.smith@empire.com", "", proccessRoles, "123abc");
        proccessRoles.get(0).setUser(user);
        userResource = userMapper.mapToResource(user);
        swapOutForUser(userResource);

        assertTrue(applicationController.getApplicationById(APPLICATION_ID).isSuccess());
        ApplicationResource application = applicationController.getApplicationById(APPLICATION_ID).getSuccessObject();
        LOG.info(String.format("Existing application id: %s", application.getId()));
        LOG.info(String.format("Existing application name: %s", application.getName()));
    }

    @Test
    public void testDisplayInvites() throws Exception{
        String testEmail = "jessica.doe@ludlow.co.uk";
        String testName = "Jessica Istesting";

        int inviteSize = controller.getInvitesByApplication(APPLICATION_ID).getSuccessObject().iterator().next().getInviteResources().size();

        RestResult<Set<InviteOrganisationResource>> invitesResult = this.controller.getInvitesByApplication(APPLICATION_ID);
        Assert.isTrue(invitesResult.isSuccess());

        // Create and save the new invite.
        List<ApplicationInviteResource> newInvites = createInviteResource(invitesResult, testName, testEmail, APPLICATION_ID);
        RestResult<UserResource> userResult = userController.findByEmail(testEmail);
        Assert.isTrue(userResult.isSuccess());
        UserResource user = userResult.getSuccessObject();
        RestResult<InviteResultsResource> inviteResults = controller.saveInvites(newInvites);
        Assert.isTrue(inviteResults.isSuccess());

        // Needed because test is run in one transaction
        flushAndClearSession();

        // Check if the invite is created and we have a hash
        assertEquals(inviteSize + 1, controller.getInvitesByApplication(APPLICATION_ID).getSuccessObject().iterator().next().getInviteResources().size());

        ApplicationInvite inviteCreated = getCreatedInvite(testEmail, APPLICATION_ID);
        assertNotNull(inviteCreated.getHash());

        // Accept the invite with for the user
        loginSystemRegistrationUser();
        RestResult<Void> resultSet = controller.acceptInvite(inviteCreated.getHash(), user.getId());
        Assert.isTrue(resultSet.isSuccess());
        swapOutForUser(userResource);

        // Check if invite is accepted
        inviteCreated = getCreatedInvite(testEmail, APPLICATION_ID);
        assertEquals(InviteStatus.OPENED, inviteCreated.getStatus());

        // Check nameConfirmed is name of the userAccount
        invitesResult = controller.getInvitesByApplication(APPLICATION_ID);
        Assert.isTrue(invitesResult.isSuccess());
        assertEquals(user.getName(), getMatchingInviteResource(invitesResult, testEmail).getNameConfirmed());
    }

    @Test
    public void testRemovingInvites() throws Exception{
        String testEmail = "jessica.doe@ludlow.co.uk";
        String testName = "Jessica Istesting";

        RestResult<Set<InviteOrganisationResource>> invitesResult = this.controller.getInvitesByApplication(APPLICATION_ID);
        Assert.isTrue(invitesResult.isSuccess());

        // Create and save the new invite.
        List<ApplicationInviteResource> newInvites = createInviteResource(invitesResult, testName, testEmail, APPLICATION_ID);
        RestResult<UserResource> userResult = userController.findByEmail(testEmail);
        Assert.isTrue(userResult.isSuccess());
        UserResource user = userResult.getSuccessObject();
        RestResult<InviteResultsResource> inviteResults = controller.saveInvites(newInvites);
        Assert.isTrue(inviteResults.isSuccess());

        // Needed because test is running in one transaction
        flushAndClearSession();

        Set<InviteOrganisationResource> invitesBeforeRemove = controller.getInvitesByApplication(APPLICATION_ID).getSuccessObject();
        List<ApplicationInviteResource> invites = invitesBeforeRemove.iterator().next().getInviteResources();

        Optional<ApplicationInviteResource> inviteToRemove = invites
                .stream()
                .filter(applicationInviteResource -> applicationInviteResource.getEmail().equals(testEmail)).findFirst();
        assertTrue(inviteToRemove.isPresent());
        RestResult<Void> result = controller.removeApplicationInvite(inviteToRemove.get().getId());
        Assert.isTrue(result.isSuccess());

        // Needed because test is running in one transaction
        flushAndClearSession();

        Set<InviteOrganisationResource> invitesAfterRemove = controller.getInvitesByApplication(APPLICATION_ID).getSuccessObject();
        assertTrue(!invitesAfterRemove.isEmpty());
        assertEquals(invites.size() - 1, invitesAfterRemove.iterator().next().getInviteResources().size());
    }

    private List<ApplicationInviteResource> createInviteResource(RestResult<Set<InviteOrganisationResource>> invitesResult, String userName, String userMail, long applicationId) {
        Set<InviteOrganisationResource> invitesExisting = invitesResult.getSuccessObject();
        InviteOrganisationResource inviteOrganisation = invitesExisting.iterator().next();

        ApplicationInviteResource inviteResource = new ApplicationInviteResource(userName, userMail, applicationId);
        inviteResource.setInviteOrganisation(inviteOrganisation.getId());

        List<ApplicationInviteResource> newInvites = new ArrayList();
        newInvites.add(inviteResource);

        return newInvites;
    }

    private ApplicationInvite getCreatedInvite(String userEmail, long applicationId) {
        ApplicationInvite inviteMatching = null;

        List<ApplicationInvite> invites = applicationInviteRepository.findByApplicationId(applicationId);
        invites.get(0).getHash();
        for (ApplicationInvite invite : invites) {
            if(invite.getEmail().equals(userEmail)) {
                inviteMatching = invite;
                break;
            }
        }

        return inviteMatching;
    }


    private ApplicationInviteResource getMatchingInviteResource(RestResult<Set<InviteOrganisationResource>> invitesResult, String userEmail) {
        Set<InviteOrganisationResource> invitesOrganisations = invitesResult.getSuccessObject();
        List<ApplicationInviteResource> inviteResources = invitesOrganisations.iterator().next().getInviteResources();
        ApplicationInviteResource inviteResourceMatiching = null;

        for (ApplicationInviteResource inviteResource : inviteResources) {
            if(inviteResource.getEmail().equals(userEmail)) {
                inviteResourceMatiching = inviteResource;
            }
        }

        return inviteResourceMatiching;
    }

    @After
    public void tearDown() {
        swapOutForUser(null);
    }
}