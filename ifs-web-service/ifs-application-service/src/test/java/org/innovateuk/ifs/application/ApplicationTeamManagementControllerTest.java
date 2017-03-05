package org.innovateuk.ifs.application;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.constant.ApplicationStatusConstants;
import org.innovateuk.ifs.application.form.ApplicantInviteForm;
import org.innovateuk.ifs.application.form.ApplicationTeamUpdateForm;
import org.innovateuk.ifs.application.populator.ApplicationTeamManagementModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.viewmodel.ApplicationTeamManagementApplicantRowViewModel;
import org.innovateuk.ifs.application.viewmodel.ApplicationTeamManagementViewModel;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.resource.InviteResultsResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.constant.ApplicationStatusConstants.OPEN;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleToMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationTeamManagementControllerTest extends BaseControllerMockMVCTest<ApplicationTeamManagementController> {

    @Spy
    @InjectMocks
    private ApplicationTeamManagementModelPopulator applicationTeamManagementModelPopulator;

    @Override
    protected ApplicationTeamManagementController supplyControllerUnderTest() {
        return new ApplicationTeamManagementController();
    }

    @Test
    public void getUpdateLeadTeam_loggedInUserIsLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        List<InviteOrganisationResource> inviteOrganisationList = setupOrganisationInvitesWithInviteForLeadOrg(applicationResource.getId(), usersMap, organisationsMap);
        Long applicationInviteId = inviteOrganisationList.get(2).getInviteResources().get(0).getId();
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        OrganisationResource expectedOrganisation = organisationsMap.get("Empire Ltd");
        List<ApplicationTeamManagementApplicantRowViewModel> expectedApplicants = asList(
                new ApplicationTeamManagementApplicantRowViewModel("Steve Smith", "steve.smith@empire.com", true, false, false),
                new ApplicationTeamManagementApplicantRowViewModel(applicationInviteId,"Paul Davidson", "paul.davidson@empire.com", false, false, true));

        ApplicationTeamManagementViewModel expectedViewModel = new ApplicationTeamManagementViewModel(
                applicationResource.getId(),
                "Application name",
                expectedOrganisation.getId(),
                expectedOrganisation.getName(),
                true,
                true,
                expectedApplicants);

        setLoggedInUser(leadApplicant);
        MvcResult mockResult = mockMvc.perform(get("/application/{applicationId}/team/update?organisation={organisationId}", applicationResource.getId(), expectedOrganisation.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("application-team/edit-org"))
                .andReturn();

        ApplicationTeamManagementViewModel viewModelResult = (ApplicationTeamManagementViewModel) mockResult.getModelAndView().getModel().get("model");

        InOrder inOrder = inOrder(applicationService, inviteRestService, userService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(applicationService).getLeadOrganisation(applicationResource.getId());
        inOrder.verify(inviteRestService).getInvitesByApplication(applicationResource.getId());
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(userService).getLeadApplicantProcessRoleOrNull(applicationResource);
        inOrder.verify(userService).findById(leadApplicant.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getUpdateNonLeadTeam_loggedInUserIsLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        List<InviteOrganisationResource> inviteOrganisationList = setupOrganisationInvitesWithInviteForLeadOrg(applicationResource.getId(), usersMap, organisationsMap);
        Long applicationInviteId1 = inviteOrganisationList.get(0).getInviteResources().get(0).getId();
        Long applicationInviteId2 = inviteOrganisationList.get(0).getInviteResources().get(1).getId();
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        OrganisationResource expectedOrganisation = organisationsMap.get("Ludlow");
        List<ApplicationTeamManagementApplicantRowViewModel> expectedApplicants = asList(
                new ApplicationTeamManagementApplicantRowViewModel(applicationInviteId1,"Jessica Doe", "jessica.doe@ludlow.com", false, false, true),
                new ApplicationTeamManagementApplicantRowViewModel(applicationInviteId2,"Ryan Dell", "ryan.dell@ludlow.com", false, true, true));

        ApplicationTeamManagementViewModel expectedViewModel = new ApplicationTeamManagementViewModel(
                applicationResource.getId(),
                "Application name",
                expectedOrganisation.getId(),
                expectedOrganisation.getName(),
                false,
                true,
                expectedApplicants);

        setLoggedInUser(leadApplicant);
        MvcResult mockResult = mockMvc.perform(get("/application/{applicationId}/team/update?organisation={organisationId}", applicationResource.getId(), expectedOrganisation.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("application-team/edit-org"))
                .andReturn();

        ApplicationTeamManagementViewModel viewModelResult = (ApplicationTeamManagementViewModel) mockResult.getModelAndView().getModel().get("model");

        InOrder inOrder = inOrder(applicationService, inviteRestService, userService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(applicationService).getLeadOrganisation(applicationResource.getId());
        inOrder.verify(inviteRestService).getInvitesByApplication(applicationResource.getId());
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(userService).getLeadApplicantProcessRoleOrNull(applicationResource);
        inOrder.verify(userService).findById(leadApplicant.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getUpdateNonLeadTeam_loggedInUserIsNonLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        List<InviteOrganisationResource> inviteOrganisationList = setupOrganisationInvitesWithInviteForLeadOrg(applicationResource.getId(), usersMap, organisationsMap);
        Long applicationInviteId1 = inviteOrganisationList.get(0).getInviteResources().get(0).getId();
        Long applicationInviteId2 = inviteOrganisationList.get(0).getInviteResources().get(1).getId();
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        OrganisationResource expectedOrganisation = organisationsMap.get("Ludlow");
        List<ApplicationTeamManagementApplicantRowViewModel> expectedApplicants = asList(
                new ApplicationTeamManagementApplicantRowViewModel(applicationInviteId1,"Jessica Doe", "jessica.doe@ludlow.com", false, false, false),
                new ApplicationTeamManagementApplicantRowViewModel(applicationInviteId2,"Ryan Dell", "ryan.dell@ludlow.com", false, true, true));

        ApplicationTeamManagementViewModel expectedViewModel = new ApplicationTeamManagementViewModel(
                applicationResource.getId(),
                "Application name",
                expectedOrganisation.getId(),
                expectedOrganisation.getName(),
                false,
                false,
                expectedApplicants);

        setLoggedInUser(usersMap.get("jessica.doe@ludlow.com"));
        MvcResult mockResult = mockMvc.perform(get("/application/{applicationId}/team/update?organisation={organisationId}", applicationResource.getId(), expectedOrganisation.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("application-team/edit-org"))
                .andReturn();

        ApplicationTeamManagementViewModel viewModelResult = (ApplicationTeamManagementViewModel) mockResult.getModelAndView().getModel().get("model");

        InOrder inOrder = inOrder(applicationService, inviteRestService, userService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(applicationService).getLeadOrganisation(applicationResource.getId());
        inOrder.verify(inviteRestService).getInvitesByApplication(applicationResource.getId());
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(userService).getLeadApplicantProcessRoleOrNull(applicationResource);
        inOrder.verify(userService).findById(leadApplicant.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getUpdateNewTeam_loggedInUserIsLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        List<InviteOrganisationResource> inviteOrganisationList = setupOrganisationInvitesWithInviteForLeadOrg(applicationResource.getId(), usersMap, organisationsMap);
        Long applicationInviteId1 = inviteOrganisationList.get(0).getInviteResources().get(0).getId();
        Long applicationInviteId2 = inviteOrganisationList.get(0).getInviteResources().get(1).getId();
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        OrganisationResource expectedOrganisation = organisationsMap.get("Ludlow");
        List<ApplicationTeamManagementApplicantRowViewModel> expectedApplicants = asList(
                new ApplicationTeamManagementApplicantRowViewModel(applicationInviteId1, "Jessica Doe", "jessica.doe@ludlow.com", false, false, true),
                new ApplicationTeamManagementApplicantRowViewModel(applicationInviteId2,"Ryan Dell", "ryan.dell@ludlow.com", false, true, true));

        ApplicationTeamManagementViewModel expectedViewModel = new ApplicationTeamManagementViewModel(
                applicationResource.getId(),
                "Application name",
                expectedOrganisation.getId(),
                expectedOrganisation.getName(),
                false,
                true,
                expectedApplicants);

        setLoggedInUser(leadApplicant);
        MvcResult mockResult = mockMvc.perform(get("/application/{applicationId}/team/update?organisationName={organisationName}", applicationResource.getId(), expectedOrganisation.getName()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("application-team/edit-org"))
                .andReturn();

        ApplicationTeamManagementViewModel viewModelResult = (ApplicationTeamManagementViewModel) mockResult.getModelAndView().getModel().get("model");

        InOrder inOrder = inOrder(applicationService, inviteRestService, userService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(applicationService).getLeadOrganisation(applicationResource.getId());
        inOrder.verify(inviteRestService).getInvitesByApplication(applicationResource.getId());
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(userService).getLeadApplicantProcessRoleOrNull(applicationResource);
        inOrder.verify(userService).findById(leadApplicant.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitUpdateLeadOrganisation_loggedInUserIsLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        setupOrganisationInvitesWithInviteForLeadOrg(applicationResource.getId(), usersMap, organisationsMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        OrganisationResource organisation = organisationsMap.get("Empire Ltd");

        ApplicationTeamUpdateForm expectedForm = new ApplicationTeamUpdateForm();
        List<ApplicantInviteForm> applicants = asList(
                new ApplicantInviteForm("Fred Brown", "fred.brown@empire.com")
        );
        expectedForm.setApplicants(applicants);

        ApplicationInviteResource applicationInvite = new ApplicationInviteResource(applicants.get(0).getName(), applicants.get(0).getEmail(), applicationResource.getId());
        InviteResultsResource inviteResultsResource = new InviteResultsResource();
        inviteResultsResource.setInvitesSendSuccess(1);
        when(inviteRestService.createInvitesByOrganisation(organisation.getId(), asList(applicationInvite))).thenReturn(restSuccess(inviteResultsResource));

        setLoggedInUser(leadApplicant);
        MvcResult mockResult = mockMvc.perform(post("/application/{applicationId}/team/update?organisation={organisationId}", applicationResource.getId(), organisation.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("applicants[0].name", applicants.get(0).getName())
                .param("applicants[0].email", applicants.get(0).getEmail()))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/application/%s/team", applicationResource.getId())))
                .andReturn();

        InOrder inOrder = inOrder(applicationService, inviteRestService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(inviteRestService).createInvitesByOrganisation(organisation.getId(), asList(applicationInvite));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitUpdateNewOrganisation_loggedInUserIsLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        setupOrganisationInvitesWithInviteForLeadOrg(applicationResource.getId(), usersMap, organisationsMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        OrganisationResource organisation = organisationsMap.get("Org X");

        ApplicationTeamUpdateForm expectedForm = new ApplicationTeamUpdateForm();
        List<ApplicantInviteForm> applicants = asList(
                new ApplicantInviteForm("Fred Brown", "fred.brown@orgx.com")
        );
        expectedForm.setApplicants(applicants);

        ApplicationInviteResource applicationInvite = new ApplicationInviteResource(applicants.get(0).getName(), applicants.get(0).getEmail(), applicationResource.getId());
        InviteResultsResource inviteResultsResource = new InviteResultsResource();
        inviteResultsResource.setInvitesSendSuccess(1);
        when(inviteRestService.createInvitesByInviteOrganisation(organisation.getName(), asList(applicationInvite))).thenReturn(restSuccess(inviteResultsResource));

        setLoggedInUser(leadApplicant);
        MvcResult mockResult = mockMvc.perform(post("/application/{applicationId}/team/update?organisationName={organisationName}", applicationResource.getId(), organisation.getName())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("applicants[0].name", applicants.get(0).getName())
                .param("applicants[0].email", applicants.get(0).getEmail()))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/application/%s/team", applicationResource.getId())))
                .andReturn();

        InOrder inOrder = inOrder(applicationService, inviteRestService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(inviteRestService).createInvitesByInviteOrganisation(organisation.getName(), asList(applicationInvite));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitUpdateNonLeadOrganisation_loggedInUserIsLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        setupOrganisationInvitesWithInviteForLeadOrg(applicationResource.getId(), usersMap, organisationsMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        OrganisationResource organisation = organisationsMap.get("Ludlow");

        ApplicationTeamUpdateForm expectedForm = new ApplicationTeamUpdateForm();
        List<ApplicantInviteForm> applicants = asList(
                new ApplicantInviteForm("Joe Smith", "joe.smith@empire.com")
        );
        expectedForm.setApplicants(applicants);

        ApplicationInviteResource applicationInvite = new ApplicationInviteResource(applicants.get(0).getName(), applicants.get(0).getEmail(), applicationResource.getId());
        InviteResultsResource inviteResultsResource = new InviteResultsResource();
        inviteResultsResource.setInvitesSendSuccess(1);
        when(inviteRestService.createInvitesByOrganisation(organisation.getId(), asList(applicationInvite))).thenReturn(restSuccess(inviteResultsResource));

        setLoggedInUser(leadApplicant);
        MvcResult mockResult = mockMvc.perform(post("/application/{applicationId}/team/update?organisation={organisationId}", applicationResource.getId(), organisation.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("applicants[0].name", applicants.get(0).getName())
                .param("applicants[0].email", applicants.get(0).getEmail()))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/application/%s/team", applicationResource.getId())))
                .andReturn();

        InOrder inOrder = inOrder(applicationService, inviteRestService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(inviteRestService).createInvitesByOrganisation(organisation.getId(), asList(applicationInvite));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitUpdateNonLeadOrganisation_loggedInUserIsNonLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        setupOrganisationInvitesWithInviteForLeadOrg(applicationResource.getId(), usersMap, organisationsMap);

        OrganisationResource organisation = organisationsMap.get("Ludlow");

        ApplicationTeamUpdateForm expectedForm = new ApplicationTeamUpdateForm();
        List<ApplicantInviteForm> applicants = asList(
                new ApplicantInviteForm("Joe Smith", "joe.smith@empire.com")
        );
        expectedForm.setApplicants(applicants);

        ApplicationInviteResource applicationInvite = new ApplicationInviteResource(applicants.get(0).getName(), applicants.get(0).getEmail(), applicationResource.getId());
        InviteResultsResource inviteResultsResource = new InviteResultsResource();
        inviteResultsResource.setInvitesSendSuccess(1);
        when(inviteRestService.createInvitesByOrganisation(organisation.getId(), asList(applicationInvite))).thenReturn(restSuccess(inviteResultsResource));

        setLoggedInUser(usersMap.get("jessica.doe@ludlow.com"));
        MvcResult mockResult = mockMvc.perform(post("/application/{applicationId}/team/update?organisation={organisationId}", applicationResource.getId(), organisation.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("applicants[0].name", applicants.get(0).getName())
                .param("applicants[0].email", applicants.get(0).getEmail()))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/application/%s/team", applicationResource.getId())))
                .andReturn();

        InOrder inOrder = inOrder(applicationService, inviteRestService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(inviteRestService).createInvitesByOrganisation(organisation.getId(), asList(applicationInvite));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitUpdateLeadOrganisation_loggedInUserIsLead_invalid() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        setupOrganisationInvitesWithInviteForLeadOrg(applicationResource.getId(), usersMap, organisationsMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        OrganisationResource organisation = organisationsMap.get("Empire Ltd");
        List<ApplicantInviteForm> applicants = asList(
                new ApplicantInviteForm("Joe Smith", "joe.smith@empire.com")
        );

        setLoggedInUser(leadApplicant);
        MvcResult result = mockMvc.perform(post("/application/{applicationId}/team/update?organisation={organisationId}", applicationResource.getId(), organisation.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("applicants[0].name", applicants.get(0).getName()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "applicants[0].email"))
                .andExpect(view().name("application-team/edit-org"))
                .andReturn();

        ApplicationTeamUpdateForm form = (ApplicationTeamUpdateForm) result.getModelAndView().getModel().get("form");

        BindingResult bindingResult = form.getBindingResult();
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("applicants[0].email"));
        assertEquals("Please enter an email address.", bindingResult.getFieldError("applicants[0].email").getDefaultMessage());

        InOrder inOrder = inOrder(applicationService, userService);
        inOrder.verify(applicationService, times(3)).getById(applicationResource.getId());
        inOrder.verify(userService).getLeadApplicantProcessRoleOrNull(applicationResource);
        inOrder.verify(userService).findById(leadApplicant.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void addApplicantLeadOrganisation_loggedInUserIsLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        setupOrganisationInvitesWithInviteForLeadOrg(applicationResource.getId(), usersMap, organisationsMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        OrganisationResource organisation = organisationsMap.get("Empire Ltd");

        ApplicationTeamUpdateForm expectedForm = new ApplicationTeamUpdateForm();
        List<ApplicantInviteForm> applicants = asList(
                new ApplicantInviteForm("Fred Brown", "fred.brown@empire.com")
        );
        expectedForm.setApplicants(applicants);

        ApplicationInviteResource applicationInvite = new ApplicationInviteResource(applicants.get(0).getName(), applicants.get(0).getEmail(), applicationResource.getId());
        InviteResultsResource inviteResultsResource = new InviteResultsResource();
        inviteResultsResource.setInvitesSendSuccess(1);
        when(inviteRestService.createInvitesByOrganisation(organisation.getId(), asList(applicationInvite))).thenReturn(restSuccess(inviteResultsResource));

        setLoggedInUser(leadApplicant);
        MvcResult mockResult = mockMvc.perform(post("/application/{applicationId}/team/update/?organisation={organisationId}&addApplicant=true", applicationResource.getId(), organisation.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("applicants[0].name", applicants.get(0).getName())
                .param("applicants[0].email", applicants.get(0).getEmail()))
                .andExpect(status().isOk())
                .andExpect(view().name("application-team/edit-org"))
                .andReturn();

        InOrder inOrder = inOrder(applicationService);
        inOrder.verify(applicationService, times(3)).getById(applicationResource.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void addApplicantNewOrganisation_loggedInUserIsLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        setupOrganisationInvitesWithInviteForLeadOrg(applicationResource.getId(), usersMap, organisationsMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        OrganisationResource organisation = organisationsMap.get("Org X");

        ApplicationTeamUpdateForm expectedForm = new ApplicationTeamUpdateForm();
        List<ApplicantInviteForm> applicants = asList(
                new ApplicantInviteForm("Fred Brown", "fred.brown@orgx.com")
        );
        expectedForm.setApplicants(applicants);

        ApplicationInviteResource applicationInvite = new ApplicationInviteResource(applicants.get(0).getName(), applicants.get(0).getEmail(), applicationResource.getId());
        InviteResultsResource inviteResultsResource = new InviteResultsResource();
        inviteResultsResource.setInvitesSendSuccess(1);
        when(inviteRestService.createInvitesByOrganisation(organisation.getId(), asList(applicationInvite))).thenReturn(restSuccess(inviteResultsResource));

        setLoggedInUser(leadApplicant);
        MvcResult mockResult = mockMvc.perform(post("/application/{applicationId}/team/update/?organisationName={organisationName}&addApplicant=true", applicationResource.getId(), organisation.getName())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("applicants[0].name", applicants.get(0).getName())
                .param("applicants[0].email", applicants.get(0).getEmail()))
                .andExpect(status().isOk())
                .andExpect(view().name("application-team/edit-org"))
                .andReturn();

        InOrder inOrder = inOrder(applicationService);
        inOrder.verify(applicationService, times(3)).getById(applicationResource.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void removeApplicantLeadOrganisation_loggedInUserIsLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        setupOrganisationInvitesWithInviteForLeadOrg(applicationResource.getId(), usersMap, organisationsMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        OrganisationResource organisation = organisationsMap.get("Empire Ltd");

        ApplicationTeamUpdateForm expectedForm = new ApplicationTeamUpdateForm();
        List<ApplicantInviteForm> applicants = asList(
                new ApplicantInviteForm("Fred Brown", "fred.brown@empire.com")
        );
        expectedForm.setApplicants(applicants);

        ApplicationInviteResource applicationInvite = new ApplicationInviteResource(applicants.get(0).getName(), applicants.get(0).getEmail(), applicationResource.getId());
        InviteResultsResource inviteResultsResource = new InviteResultsResource();
        inviteResultsResource.setInvitesSendSuccess(1);
        when(inviteRestService.createInvitesByOrganisation(organisation.getId(), asList(applicationInvite))).thenReturn(restSuccess(inviteResultsResource));

        setLoggedInUser(leadApplicant);
        MvcResult mockResult = mockMvc.perform(post("/application/{applicationId}/team/update/?organisation={organisationId}&removeApplicant=0", applicationResource.getId(), organisation.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("applicants[0].name", applicants.get(0).getName())
                .param("applicants[0].email", applicants.get(0).getEmail()))
                .andExpect(status().isOk())
                .andExpect(view().name("application-team/edit-org"))
                .andReturn();

        InOrder inOrder = inOrder(applicationService);
        inOrder.verify(applicationService, times(3)).getById(applicationResource.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void removeApplicantNewOrganisation_loggedInUserIsLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        setupOrganisationInvitesWithInviteForLeadOrg(applicationResource.getId(), usersMap, organisationsMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        OrganisationResource organisation = organisationsMap.get("Org X");

        ApplicationTeamUpdateForm expectedForm = new ApplicationTeamUpdateForm();
        List<ApplicantInviteForm> applicants = asList(
                new ApplicantInviteForm("Fred Brown", "fred.brown@orgx.com")
        );
        expectedForm.setApplicants(applicants);

        ApplicationInviteResource applicationInvite = new ApplicationInviteResource(applicants.get(0).getName(), applicants.get(0).getEmail(), applicationResource.getId());
        InviteResultsResource inviteResultsResource = new InviteResultsResource();
        inviteResultsResource.setInvitesSendSuccess(1);
        when(inviteRestService.createInvitesByOrganisation(organisation.getId(), asList(applicationInvite))).thenReturn(restSuccess(inviteResultsResource));

        setLoggedInUser(leadApplicant);
        MvcResult mockResult = mockMvc.perform(post("/application/{applicationId}/team/update/?organisationName={organisationName}&removeApplicant=0", applicationResource.getId(), organisation.getName())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("applicants[0].name", applicants.get(0).getName())
                .param("applicants[0].email", applicants.get(0).getEmail()))
                .andExpect(status().isOk())
                .andExpect(view().name("application-team/edit-org"))
                .andReturn();

        InOrder inOrder = inOrder(applicationService);
        inOrder.verify(applicationService, times(3)).getById(applicationResource.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void markApplicantLeadOrganisation_loggedInUserIsLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        List<InviteOrganisationResource> inviteOrganisationList = setupOrganisationInvitesWithInviteForLeadOrg(applicationResource.getId(), usersMap, organisationsMap);
        Long applicationInviteId = inviteOrganisationList.get(2).getInviteResources().get(0).getId();
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);
        OrganisationResource organisation = organisationsMap.get("Empire Ltd");

        ApplicationTeamUpdateForm expectedForm = new ApplicationTeamUpdateForm();
        List<ApplicantInviteForm> applicants = asList(
                new ApplicantInviteForm("Fred Brown", "fred.brown@empire.com")
        );
        expectedForm.setApplicants(applicants);

        setLoggedInUser(leadApplicant);
        MvcResult mockResult = mockMvc.perform(post("/application/{applicationId}/team/update/?organisation={organisationId}&markForRemoval={applicationInviteId}", applicationResource.getId(), organisation.getId(), applicationInviteId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("applicants[0].name", applicants.get(0).getName())
                .param("applicants[0].email", applicants.get(0).getEmail()))
                .andExpect(status().isOk())
                .andExpect(view().name("application-team/edit-org"))
                .andReturn();

        InOrder inOrder = inOrder(applicationService);
        inOrder.verify(applicationService, times(3)).getById(applicationResource.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void markApplicantNewOrganisation_loggedInUserIsLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        List<InviteOrganisationResource> inviteOrganisationList = setupOrganisationInvitesWithAnUnconfirmedOrganisation(applicationResource.getId(), usersMap, organisationsMap);
        Long applicationInviteId = inviteOrganisationList.get(0).getInviteResources().get(0).getId();
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);
        OrganisationResource organisation = organisationsMap.get("Ludlow");

        ApplicationTeamUpdateForm expectedForm = new ApplicationTeamUpdateForm();
        List<ApplicantInviteForm> applicants = asList(
                new ApplicantInviteForm("Fred Brown", "fred.brown@ludlow.com")
        );
        expectedForm.setApplicants(applicants);

        setLoggedInUser(leadApplicant);
        MvcResult mockResult = mockMvc.perform(post("/application/{applicationId}/team/update/?organisationName={organisationName}&markForRemoval={applicationInviteId}", applicationResource.getId(), organisation.getName(), applicationInviteId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("applicants[0].name", applicants.get(0).getName())
                .param("applicants[0].email", applicants.get(0).getEmail()))
                .andExpect(status().isOk())
                .andExpect(view().name("application-team/edit-org"))
                .andReturn();

        InOrder inOrder = inOrder(applicationService);
        inOrder.verify(applicationService, times(3)).getById(applicationResource.getId());
        inOrder.verifyNoMoreInteractions();
    }

    private ApplicationResource setupApplicationResource(Map<String, OrganisationResource> organisationsMap) {
        return setupApplicationResource(organisationsMap, OPEN);
    }

    private ApplicationResource setupApplicationResource(Map<String, OrganisationResource> organisationsMap, ApplicationStatusConstants applicationStatus) {
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
                .withName("Ludlow", "EGGS", "Empire Ltd", "Org X")
                .build(4);

        return simpleToMap(organisationResources, OrganisationResource::getName);
    }

    private List<InviteOrganisationResource> setupOrganisationInvitesWithInviteForLeadOrg(long applicationId,
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
        OrganisationResource org4 = organisationsMap.get("Org X");

        List<InviteOrganisationResource> inviteOrganisationResources = newInviteOrganisationResource()
                .withOrganisation(org1.getId(), org2.getId(), org3.getId(), org4.getId())
                .withOrganisationName(org1.getName(), org2.getName(), org3.getName(), org4.getName())
                .withOrganisationNameConfirmed(org1.getName(), org2.getName(), org3.getName(), org4.getName())
                .withInviteResources(invitesOrg1, invitesOrg2, invitesOrg3, null)
                .build(4);

        when(inviteRestService.getInvitesByApplication(applicationId)).thenReturn(restSuccess(inviteOrganisationResources));
        return inviteOrganisationResources;
    }

    private List<InviteOrganisationResource> setupOrganisationInvitesWithoutInvitesForLeadOrg(long applicationId,
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
        return inviteOrganisationResources;
    }

    private List<InviteOrganisationResource> setupOrganisationInvitesWithAnUnconfirmedOrganisation(long applicationId,
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
        return inviteOrganisationResources;
    }
}