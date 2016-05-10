package com.worth.ifs.invite.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.controller.ApplicationController;
import com.worth.ifs.application.controller.QuestionController;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.constant.InviteStatusConstants;
import com.worth.ifs.invite.domain.Invite;
import com.worth.ifs.invite.repository.InviteRepository;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.invite.resource.InviteResultsResource;
import com.worth.ifs.user.controller.UserController;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.mapper.UserMapper;
import com.worth.ifs.user.resource.UserResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.worth.ifs.security.SecuritySetter.swapOutForUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Rollback
public class InviteControllerIntegrationTest extends BaseControllerIntegrationTest<InviteController> {

    public static final long APPLICATION_ID = 1L;

    private QuestionController questionController;
    private Long leadApplicantProcessRole;
    private Long leadApplicantId;
    private ApplicationController applicationController;
    private InviteOrganisationController inviteOrganisationController;
    private UserController userController;

    @Autowired
    InviteRepository inviteRepository;

    @Autowired
    UserMapper userMapper;
    private UserResource userResource;

    @Autowired
    @Override
    protected void setControllerUnderTest(InviteController controller) {
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

        assertEquals(1, inviteRepository.findByApplicationId(APPLICATION_ID).size());
        assertEquals(1, controller.getInvitesByApplication(APPLICATION_ID).getSuccessObject().iterator().next().getInviteResources().size());

        RestResult<Set<InviteOrganisationResource>> invitesResult = this.controller.getInvitesByApplication(APPLICATION_ID);
        Assert.isTrue(invitesResult.isSuccess());

        // Create and save the new invite.
        List<InviteResource> newInvites = createInviteResource(invitesResult, testName, testEmail, APPLICATION_ID);
        RestResult<UserResource> userResult = userController.findByEmail(testEmail);
        Assert.isTrue(userResult.isSuccess());
        UserResource user = userResult.getSuccessObject();
        RestResult<InviteResultsResource> inviteResults = controller.saveInvites(newInvites);
        Assert.isTrue(inviteResults.isSuccess());

        // Check if the invite is created.
        assertEquals(2, inviteRepository.findByApplicationId(APPLICATION_ID).size());
        assertEquals(2, controller.getInvitesByApplication(APPLICATION_ID).getSuccessObject().iterator().next().getInviteResources().size());


//        Invite inviteCreated = getCreatedInvite(testEmail, APPLICATION_ID);
//        assertNotNull(inviteCreated.getHash());
//
//        loginSystemRegistrationUser();
//
//        RestResult<Void> resultSet = controller.acceptInvite(inviteCreated.getHash(), user.getId());
//        Assert.isTrue(resultSet.isSuccess());
//        assertEquals(2, inviteRepository.findByApplicationId(APPLICATION_ID).size());
//        assertEquals(2, controller.getInvitesByApplication(APPLICATION_ID).getSuccessObject().iterator().next().getInviteResources().size());
//
//        swapOutForUser(userResource);
//
//        invitesResult = controller.getInvitesByApplication(APPLICATION_ID);
//        Assert.isTrue(invitesResult.isSuccess());
//        Set<InviteOrganisationResource> invitesOrganisations = invitesResult.getSuccessObject();
//        invitesOrganisations.iterator();
//
//        inviteCreated = getCreatedInvite(testEmail, APPLICATION_ID);
//        assertEquals(InviteStatusConstants.ACCEPTED, inviteCreated.getStatus());


        //check nameconfirmed
    }

    private List<InviteResource> createInviteResource(RestResult<Set<InviteOrganisationResource>> invitesResult, String userName, String userMail, long applicationId) {
        Set<InviteOrganisationResource> invitesExisting = invitesResult.getSuccessObject();
        InviteOrganisationResource inviteOrganisation = invitesExisting.iterator().next();

        InviteResource inviteResource = new InviteResource(userName, userMail, applicationId);
        inviteResource.setInviteOrganisation(inviteOrganisation.getId());

        List<InviteResource> newInvites = new ArrayList();
        newInvites.add(inviteResource);

        return newInvites;
    }

    private Invite getCreatedInvite(String userEmail, long applicationId) {
        Invite inviteFound = null;

        List<Invite> invites = inviteRepository.findByApplicationId(applicationId);
        invites.get(0).getHash();
        for (Invite invite:invites) {
            if(invite.getEmail() == userEmail) {
                inviteFound = invite;
                break;
            }
        }

        return inviteFound;
    }

    @After
    public void tearDown() {
        swapOutForUser(null);
    }

//    @Test
//    public void addInvites(){
//        List<InviteResource> inviteResources = new ArrayList<>();
//        InviteResource invite1 = new InviteResource("Nico", "nico@email.com", APPLICATION_ID);
//        invite1.setInviteOrganisationName("Worth");
//        InviteResource invite2 = new InviteResource("Brent", "brent@email.com", APPLICATION_ID);
//        invite2.setInviteOrganisationName("Worth");
//        inviteResources.add(invite1);
//        inviteResources.add(invite2);
////        HttpStatus savedStatus = controller.saveInvites(inviteResources).getStatusCode();
//
//        InviteOrganisationResource inviteOrganisation = new InviteOrganisationResource();
//        inviteOrganisation.setId(50L);
//        inviteOrganisation.setOrganisationName("Worth");
//        inviteOrganisation.setInviteResources(inviteResources);
//        inviteOrganisationController.put(inviteOrganisation);
//
//        RestResult savedStatus = controller.createApplicationInvites(inviteOrganisation);
//        LOG.info(String.format("Status of save: %s", savedStatus.getStatusCode().toString()));
//
//        RestResult<Iterable<InviteOrganisationResource>> inviteOrganisationResult = inviteOrganisationController.findAll();
////        RestResult<InviteOrganisationResource> inviteOrganisationResult = inviteOrganisationController.findById(50L);
//        LOG.info("StatusCode: " + inviteOrganisationResult.getStatusCode());
//        Iterable<InviteOrganisationResource> tmp1 = inviteOrganisationResult.getSuccessObject();
//
//        ArrayList<InviteOrganisationResource> tmp2 = Lists.newArrayList(tmp1);
//        LOG.info("StatusCode: "+ tmp2.size());
//
//        RestResult<Set<InviteOrganisationResource>> savedInviteResult = controller.getInvitesByApplication(APPLICATION_ID);
//        LOG.info(String.format("Status of get invites: %s", savedInviteResult.getStatusCode().toString()));
//        assertTrue(savedInviteResult.isSuccess());
//        Set<InviteOrganisationResource> invitesMap = savedInviteResult.getSuccessObject();
//        assertEquals(1, invitesMap.size());
//        assertEquals(2, invitesMap.iterator().next().getInviteResources().size());
//    }

}