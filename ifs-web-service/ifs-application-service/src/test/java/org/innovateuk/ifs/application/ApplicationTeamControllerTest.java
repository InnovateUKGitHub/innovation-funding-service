package org.innovateuk.ifs.application;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.populator.ApplicationTeamModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationStatus;
import org.innovateuk.ifs.application.viewmodel.ApplicationTeamApplicantRowViewModel;
import org.innovateuk.ifs.application.viewmodel.ApplicationTeamOrganisationRowViewModel;
import org.innovateuk.ifs.application.viewmodel.ApplicationTeamViewModel;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.resource.ApplicationStatus.CREATED;
import static org.innovateuk.ifs.application.resource.ApplicationStatus.OPEN;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleToMap;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationTeamControllerTest extends BaseControllerMockMVCTest<ApplicationTeamController> {

    @Spy
    @InjectMocks
    private ApplicationTeamModelPopulator applicationTeamModelPopulator;

    @Override
    protected ApplicationTeamController supplyControllerUnderTest() {
        return new ApplicationTeamController();
    }

    @Test
    public void getApplicationTeam_loggedInUserIsLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        Map<String, InviteOrganisationResource> inviteOrganisationsMap = setupOrganisationInvitesWithInviteForLeadOrg(
                applicationResource.getId(), usersMap, organisationsMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        Long orgIdEmpire = organisationsMap.get("Empire Ltd").getId();
        Long orgIdLudlow = organisationsMap.get("Ludlow").getId();
        Long orgIdEggs = organisationsMap.get("EGGS").getId();
        Long inviteOrgIdEmpire = inviteOrganisationsMap.get("Empire Ltd").getId();
        Long inviteOrgIdLudlow = inviteOrganisationsMap.get("Ludlow").getId();
        Long inviteOrgIdEggs = inviteOrganisationsMap.get("EGGS").getId();

        List<ApplicationTeamOrganisationRowViewModel> expectedOrganisations = asList(
                new ApplicationTeamOrganisationRowViewModel(orgIdEmpire, inviteOrgIdEmpire, "Empire Ltd", true, asList(
                        new ApplicationTeamApplicantRowViewModel("Steve Smith", "steve.smith@empire.com", true, false),
                        new ApplicationTeamApplicantRowViewModel("Paul Davidson", "paul.davidson@empire.com", false, false)
                ), true),
                new ApplicationTeamOrganisationRowViewModel(orgIdEggs, inviteOrgIdEggs, "EGGS", false, singletonList(
                        new ApplicationTeamApplicantRowViewModel("Paul Tom", "paul.tom@egg.com", false, false)
                ), true),
                new ApplicationTeamOrganisationRowViewModel(orgIdLudlow, inviteOrgIdLudlow, "Ludlow", false, asList(
                        new ApplicationTeamApplicantRowViewModel("Jessica Doe", "jessica.doe@ludlow.com", false, false),
                        new ApplicationTeamApplicantRowViewModel("Ryan Dell", "ryan.dell@ludlow.com", false, true)
                ), true)
        );

        ApplicationTeamViewModel expectedViewModel = new ApplicationTeamViewModel(
                applicationResource.getId(),
                "Application name",
                expectedOrganisations,
                true,
                false
        );

        setLoggedInUser(leadApplicant);
        mockMvc.perform(get("/application/{applicationId}/team", applicationResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("application-team/team"));

        InOrder inOrder = inOrder(applicationService, inviteRestService, userService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(userService).getLeadApplicantProcessRoleOrNull(applicationResource);
        inOrder.verify(userService).findById(leadApplicant.getId());
        inOrder.verify(applicationService).getLeadOrganisation(applicationResource.getId());
        inOrder.verify(inviteRestService).getInvitesByApplication(applicationResource.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getApplicationTeam_loggedInUserIsNonLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        Map<String, InviteOrganisationResource> inviteOrganisationsMap = setupOrganisationInvitesWithInviteForLeadOrg(
                applicationResource.getId(), usersMap, organisationsMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        Long orgIdEmpire = organisationsMap.get("Empire Ltd").getId();
        Long orgIdLudlow = organisationsMap.get("Ludlow").getId();
        Long orgIdEggs = organisationsMap.get("EGGS").getId();
        Long inviteOrgIdEmpire = inviteOrganisationsMap.get("Empire Ltd").getId();
        Long inviteOrgIdLudlow = inviteOrganisationsMap.get("Ludlow").getId();
        Long inviteOrgIdEggs = inviteOrganisationsMap.get("EGGS").getId();

        List<ApplicationTeamOrganisationRowViewModel> expectedOrganisations = asList(
                new ApplicationTeamOrganisationRowViewModel(orgIdEmpire, inviteOrgIdEmpire, "Empire Ltd", true, asList(
                        new ApplicationTeamApplicantRowViewModel("Steve Smith", "steve.smith@empire.com", true, false),
                        new ApplicationTeamApplicantRowViewModel("Paul Davidson", "paul.davidson@empire.com", false, false)
                ), false),
                new ApplicationTeamOrganisationRowViewModel(orgIdEggs, inviteOrgIdEggs, "EGGS", false, singletonList(
                        new ApplicationTeamApplicantRowViewModel("Paul Tom", "paul.tom@egg.com", false, false)
                ), false),
                new ApplicationTeamOrganisationRowViewModel(orgIdLudlow, inviteOrgIdLudlow, "Ludlow", false, asList(
                        new ApplicationTeamApplicantRowViewModel("Jessica Doe", "jessica.doe@ludlow.com", false, false),
                        new ApplicationTeamApplicantRowViewModel("Ryan Dell", "ryan.dell@ludlow.com", false, true)
                ), true)
        );

        ApplicationTeamViewModel expectedViewModel = new ApplicationTeamViewModel(
                applicationResource.getId(),
                "Application name",
                expectedOrganisations,
                false,
                false
        );

        setLoggedInUser(usersMap.get("jessica.doe@ludlow.com"));
        mockMvc.perform(get("/application/{applicationId}/team", applicationResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("application-team/team"));

        InOrder inOrder = inOrder(applicationService, inviteRestService, userService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(userService).getLeadApplicantProcessRoleOrNull(applicationResource);
        inOrder.verify(userService).findById(leadApplicant.getId());
        inOrder.verify(applicationService).getLeadOrganisation(applicationResource.getId());
        inOrder.verify(inviteRestService).getInvitesByApplication(applicationResource.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getApplicationTeam_leadOrgHasNoInvites() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        Map<String, InviteOrganisationResource> inviteOrganisationsMap = setupOrganisationInvitesWithoutInvitesForLeadOrg(
                applicationResource.getId(), usersMap, organisationsMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        Long orgIdEmpire = organisationsMap.get("Empire Ltd").getId();
        Long orgIdLudlow = organisationsMap.get("Ludlow").getId();
        Long orgIdEggs = organisationsMap.get("EGGS").getId();
        // No InviteOrganisation exists for Empire Ltd
        Long inviteOrgIdEmpire = null;
        Long inviteOrgIdLudlow = inviteOrganisationsMap.get("Ludlow").getId();
        Long inviteOrgIdEggs = inviteOrganisationsMap.get("EGGS").getId();

        List<ApplicationTeamOrganisationRowViewModel> expectedOrganisations = asList(
                new ApplicationTeamOrganisationRowViewModel(orgIdEmpire, inviteOrgIdEmpire, "Empire Ltd", true, singletonList(
                        new ApplicationTeamApplicantRowViewModel("Steve Smith", "steve.smith@empire.com", true, false)
                ), false),
                new ApplicationTeamOrganisationRowViewModel(orgIdEggs, inviteOrgIdEggs, "EGGS", false, singletonList(
                        new ApplicationTeamApplicantRowViewModel("Paul Tom", "paul.tom@egg.com", false, false)
                ), false),
                new ApplicationTeamOrganisationRowViewModel(orgIdLudlow, inviteOrgIdLudlow, "Ludlow", false, asList(
                        new ApplicationTeamApplicantRowViewModel("Jessica Doe", "jessica.doe@ludlow.com", false, false),
                        new ApplicationTeamApplicantRowViewModel("Ryan Dell", "ryan.dell@ludlow.com", false, true)
                ), true)
        );

        ApplicationTeamViewModel expectedViewModel = new ApplicationTeamViewModel(
                applicationResource.getId(),
                "Application name",
                expectedOrganisations,
                false,
                false
        );

        setLoggedInUser(usersMap.get("jessica.doe@ludlow.com"));
        mockMvc.perform(get("/application/{applicationId}/team", applicationResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("application-team/team"));

        InOrder inOrder = inOrder(applicationService, inviteRestService, userService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(userService).getLeadApplicantProcessRoleOrNull(applicationResource);
        inOrder.verify(userService).findById(leadApplicant.getId());
        inOrder.verify(applicationService).getLeadOrganisation(applicationResource.getId());
        inOrder.verify(inviteRestService).getInvitesByApplication(applicationResource.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getApplicationTeam_organisationUnconfirmed() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        Map<String, InviteOrganisationResource> inviteOrganisationsMap = setupOrganisationInvitesWithAnUnconfirmedOrganisation(
                applicationResource.getId(), usersMap, organisationsMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        Long orgIdEmpire = organisationsMap.get("Empire Ltd").getId();
        // No Organisation exists for Ludlow
        Long orgIdLudlow = null;
        Long orgIdEggs = organisationsMap.get("EGGS").getId();
        Long inviteOrgIdEmpire = inviteOrganisationsMap.get("Empire Ltd").getId();
        Long inviteOrgIdLudlow = inviteOrganisationsMap.get("Ludlow").getId();
        Long inviteOrgIdEggs = inviteOrganisationsMap.get("EGGS").getId();

        List<ApplicationTeamOrganisationRowViewModel> expectedOrganisations = asList(
                new ApplicationTeamOrganisationRowViewModel(orgIdEmpire, inviteOrgIdEmpire, "Empire Ltd", true, asList(
                        new ApplicationTeamApplicantRowViewModel("Steve Smith", "steve.smith@empire.com", true, false),
                        new ApplicationTeamApplicantRowViewModel("Paul Davidson", "paul.davidson@empire.com", false, false)
                ), true),
                new ApplicationTeamOrganisationRowViewModel(orgIdEggs, inviteOrgIdEggs, "EGGS", false, singletonList(
                        new ApplicationTeamApplicantRowViewModel("Paul Tom", "paul.tom@egg.com", false, false)
                ), true),
                new ApplicationTeamOrganisationRowViewModel(orgIdLudlow, inviteOrgIdLudlow, "Ludlow", false, asList(
                        new ApplicationTeamApplicantRowViewModel("Jessica Doe", "jessica.doe@ludlow.com", false, true),
                        new ApplicationTeamApplicantRowViewModel("Ryan Dell", "ryan.dell@ludlow.com", false, true)
                ), true)
        );

        ApplicationTeamViewModel expectedViewModel = new ApplicationTeamViewModel(
                applicationResource.getId(),
                "Application name",
                expectedOrganisations,
                true,
                false
        );

        setLoggedInUser(leadApplicant);
        mockMvc.perform(get("/application/{applicationId}/team", applicationResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("application-team/team"));

        InOrder inOrder = inOrder(applicationService, inviteRestService, userService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(userService).getLeadApplicantProcessRoleOrNull(applicationResource);
        inOrder.verify(userService).findById(leadApplicant.getId());
        inOrder.verify(applicationService).getLeadOrganisation(applicationResource.getId());
        inOrder.verify(inviteRestService).getInvitesByApplication(applicationResource.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getApplicationTeam_leadApplicantHasOptionToBeginTheApplication() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap, CREATED);
        Map<String, UserResource> usersMap = setupUserResources();
        setupOrganisationInvitesWithInviteForLeadOrg(applicationResource.getId(), usersMap, organisationsMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        setLoggedInUser(leadApplicant);
        MvcResult result = mockMvc.perform(get("/application/{applicationId}/team", applicationResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("application-team/team"))
                .andReturn();

        ApplicationTeamViewModel model = (ApplicationTeamViewModel) result.getModelAndView().getModel().get("model");
        assertTrue(model.isApplicationCanBegin());

        InOrder inOrder = inOrder(applicationService, inviteRestService, userService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(userService).getLeadApplicantProcessRoleOrNull(applicationResource);
        inOrder.verify(userService).findById(leadApplicant.getId());
        inOrder.verify(applicationService).getLeadOrganisation(applicationResource.getId());
        inOrder.verify(inviteRestService).getInvitesByApplication(applicationResource.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getApplicationTeam_nonLeadApplicantHasNoOptionToBeginTheApplication() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap, CREATED);
        Map<String, UserResource> usersMap = setupUserResources();
        setupOrganisationInvitesWithInviteForLeadOrg(applicationResource.getId(), usersMap, organisationsMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        setLoggedInUser(usersMap.get("jessica.doe@ludlow.com"));
        MvcResult result = mockMvc.perform(get("/application/{applicationId}/team", applicationResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("application-team/team"))
                .andReturn();

        ApplicationTeamViewModel model = (ApplicationTeamViewModel) result.getModelAndView().getModel().get("model");
        assertFalse(model.isApplicationCanBegin());

        InOrder inOrder = inOrder(applicationService, inviteRestService, userService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(userService).getLeadApplicantProcessRoleOrNull(applicationResource);
        inOrder.verify(userService).findById(leadApplicant.getId());
        inOrder.verify(applicationService).getLeadOrganisation(applicationResource.getId());
        inOrder.verify(inviteRestService).getInvitesByApplication(applicationResource.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void beginApplication_leadApplicantCanBeginACreatedApplication() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap, CREATED);
        Map<String, UserResource> usersMap = setupUserResources();
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        when(applicationService.updateStatus(applicationResource.getId(), OPEN)).thenReturn(serviceSuccess());

        setLoggedInUser(leadApplicant);
        mockMvc.perform(get("/application/{applicationId}/begin", applicationResource.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/application/%s", applicationResource.getId())));

        InOrder inOrder = inOrder(applicationService, inviteRestService, userService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(userService).getLeadApplicantProcessRoleOrNull(applicationResource);
        inOrder.verify(applicationService).updateStatus(applicationResource.getId(), OPEN);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void beginApplication_leadApplicantCannotBeginAnOpenApplication() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        // Assert the request is redirected to the application page without attempting to change the status

        setLoggedInUser(leadApplicant);
        mockMvc.perform(get("/application/{applicationId}/begin", applicationResource.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/application/%s", applicationResource.getId())));

        InOrder inOrder = inOrder(applicationService, inviteRestService, userService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(userService).getLeadApplicantProcessRoleOrNull(applicationResource);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void beginApplication_nonLeadApplicantCannotBeginTheApplication() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap, CREATED);
        Map<String, UserResource> usersMap = setupUserResources();
        setupLeadApplicant(applicationResource, usersMap);

        setLoggedInUser(usersMap.get("jessica.doe@ludlow.com"));
        mockMvc.perform(get("/application/{applicationId}/begin", applicationResource.getId()))
                .andExpect(status().isForbidden());

        InOrder inOrder = inOrder(applicationService, inviteRestService, userService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(userService).getLeadApplicantProcessRoleOrNull(applicationResource);
        inOrder.verifyNoMoreInteractions();
    }

    private ApplicationResource setupApplicationResource(Map<String, OrganisationResource> organisationsMap) {
        return setupApplicationResource(organisationsMap, OPEN);
    }

    private ApplicationResource setupApplicationResource(Map<String, OrganisationResource> organisationsMap, ApplicationStatus applicationStatus) {
        ApplicationResource applicationResource = newApplicationResource()
                .withName("Application name")
                .withApplicationStatus(applicationStatus)
                .build();

        when(applicationService.getById(applicationResource.getId())).thenReturn(applicationResource);
        when(applicationService.getLeadOrganisation(applicationResource.getId())).thenReturn(organisationsMap.get("Empire Ltd"));
        return applicationResource;
    }

    private Map<String, UserResource> setupUserResources() {
        List<UserResource> userResources = newUserResource()
                .withFirstName("Jessica", "Paul", "Steve", "Paul")
                .withLastName("Doe", "Tom", "Smith", "Davidson")
                .withEmail("jessica.doe@ludlow.com", "paul.tom@egg.com", "steve.smith@empire.com", "paul.davidson@empire.com")
                .build(4);

        return simpleToMap(userResources, UserResource::getEmail);
    }

    private UserResource setupLeadApplicant(ApplicationResource applicationResource, Map<String, UserResource> usersMap) {
        UserResource leadApplicant = usersMap.get("steve.smith@empire.com");
        when(userService.getLeadApplicantProcessRoleOrNull(applicationResource)).thenReturn(newProcessRoleResource()
                .withUser(leadApplicant)
                .build());
        when(userService.findById(leadApplicant.getId())).thenReturn(leadApplicant);
        return leadApplicant;
    }

    private Map<String, OrganisationResource> setupOrganisationResources() {
        List<OrganisationResource> organisationResources = newOrganisationResource()
                .withName("Ludlow", "EGGS", "Empire Ltd")
                .build(3);

        return simpleToMap(organisationResources, OrganisationResource::getName);
    }

    private Map<String, InviteOrganisationResource> setupOrganisationInvitesWithInviteForLeadOrg(long applicationId,
                                                                                                 Map<String, UserResource> usersMap,
                                                                                                 Map<String, OrganisationResource> organisationsMap) {
        UserResource user1 = usersMap.get("jessica.doe@ludlow.com");
        UserResource user2 = usersMap.get("paul.tom@egg.com");
        UserResource user3 = usersMap.get("paul.davidson@empire.com");

        List<ApplicationInviteResource> invitesOrg1 = newApplicationInviteResource()
                .withUsers(user1.getId(), null)
                .withNameConfirmed(user1.getName(), null)
                .withName("Jess Doe", "Ryan Dell")
                .withEmail(user1.getEmail(), "ryan.dell@ludlow.com")
                .withStatus(OPENED, SENT)
                .build(2);

        List<ApplicationInviteResource> invitesOrg2 = newApplicationInviteResource()
                .withUsers(user2.getId())
                .withNameConfirmed(user2.getName())
                .withName(user2.getName())
                .withEmail(user2.getEmail())
                .withStatus(OPENED)
                .build(1);

        List<ApplicationInviteResource> invitesOrg3 = newApplicationInviteResource()
                .withUsers(user3.getId())
                .withNameConfirmed(user3.getName())
                .withName(user3.getName())
                .withEmail(user3.getEmail())
                .withStatus(OPENED)
                .build(1);

        OrganisationResource org1 = organisationsMap.get("Ludlow");
        OrganisationResource org2 = organisationsMap.get("EGGS");
        OrganisationResource org3 = organisationsMap.get("Empire Ltd");

        List<InviteOrganisationResource> inviteOrganisationResources = newInviteOrganisationResource()
                .withOrganisation(org1.getId(), org2.getId(), org3.getId())
                .withOrganisationName(org1.getName(), org2.getName(), org3.getName())
                .withOrganisationNameConfirmed(org1.getName(), org2.getName(), org3.getName())
                .withInviteResources(invitesOrg1, invitesOrg2, invitesOrg3)
                .build(3);

        when(inviteRestService.getInvitesByApplication(applicationId)).thenReturn(restSuccess(inviteOrganisationResources));
        return simpleToMap(inviteOrganisationResources, InviteOrganisationResource::getOrganisationName);
    }

    private Map<String, InviteOrganisationResource> setupOrganisationInvitesWithoutInvitesForLeadOrg(long applicationId,
                                                                                                     Map<String, UserResource> usersMap,
                                                                                                     Map<String, OrganisationResource> organisationsMap) {
        UserResource user1 = usersMap.get("jessica.doe@ludlow.com");
        UserResource user2 = usersMap.get("paul.tom@egg.com");

        List<ApplicationInviteResource> invitesOrg1 = newApplicationInviteResource()
                .withUsers(user1.getId(), null)
                .withNameConfirmed(user1.getName(), null)
                .withName("Jess Doe", "Ryan Dell")
                .withEmail(user1.getEmail(), "ryan.dell@ludlow.com")
                .withStatus(OPENED, SENT)
                .build(2);

        List<ApplicationInviteResource> invitesOrg2 = newApplicationInviteResource()
                .withUsers(user2.getId())
                .withNameConfirmed(user2.getName())
                .withName(user2.getName())
                .withEmail(user2.getEmail())
                .withStatus(OPENED)
                .build(1);

        OrganisationResource org1 = organisationsMap.get("Ludlow");
        OrganisationResource org2 = organisationsMap.get("EGGS");

        List<InviteOrganisationResource> inviteOrganisationResources = newInviteOrganisationResource()
                .withOrganisation(org1.getId(), org2.getId())
                .withOrganisationName(org1.getName(), org2.getName())
                .withOrganisationNameConfirmed(org1.getName(), org2.getName())
                .withInviteResources(invitesOrg1, invitesOrg2)
                .build(2);

        when(inviteRestService.getInvitesByApplication(applicationId)).thenReturn(restSuccess(inviteOrganisationResources));
        return simpleToMap(inviteOrganisationResources, InviteOrganisationResource::getOrganisationName);
    }

    private Map<String, InviteOrganisationResource> setupOrganisationInvitesWithAnUnconfirmedOrganisation(long applicationId,
                                                                                                          Map<String, UserResource> usersMap,
                                                                                                          Map<String, OrganisationResource> organisationsMap) {
        UserResource user1 = usersMap.get("jessica.doe@ludlow.com");
        UserResource user2 = usersMap.get("paul.tom@egg.com");
        UserResource user3 = usersMap.get("paul.davidson@empire.com");

        List<ApplicationInviteResource> invitesOrg1 = newApplicationInviteResource()
                .withUsers(user1.getId(), null)
                .withNameConfirmed(user1.getName(), null)
                .withName("Jess Doe", "Ryan Dell")
                .withEmail(user1.getEmail(), "ryan.dell@ludlow.com")
                .withStatus(SENT, SENT)
                .build(2);

        List<ApplicationInviteResource> invitesOrg2 = newApplicationInviteResource()
                .withUsers(user2.getId())
                .withNameConfirmed(user2.getName())
                .withName(user2.getName())
                .withEmail(user2.getEmail())
                .withStatus(OPENED)
                .build(1);

        List<ApplicationInviteResource> invitesOrg3 = newApplicationInviteResource()
                .withUsers(user3.getId())
                .withNameConfirmed(user3.getName())
                .withName(user3.getName())
                .withEmail(user3.getEmail())
                .withStatus(OPENED)
                .build(1);

        OrganisationResource org2 = organisationsMap.get("EGGS");
        OrganisationResource org3 = organisationsMap.get("Empire Ltd");

        List<InviteOrganisationResource> inviteOrganisationResources = newInviteOrganisationResource()
                .withOrganisation(null, org2.getId(), org3.getId())
                .withOrganisationName("Ludlow", org2.getName(), org3.getName())
                .withOrganisationNameConfirmed(null, org2.getName(), org3.getName())
                .withInviteResources(invitesOrg1, invitesOrg2, invitesOrg3)
                .build(3);

        when(inviteRestService.getInvitesByApplication(applicationId)).thenReturn(restSuccess(inviteOrganisationResources));
        return simpleToMap(inviteOrganisationResources, InviteOrganisationResource::getOrganisationName);
    }
}