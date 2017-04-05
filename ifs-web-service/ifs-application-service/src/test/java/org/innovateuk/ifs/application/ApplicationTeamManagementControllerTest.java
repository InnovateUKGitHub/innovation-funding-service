package org.innovateuk.ifs.application;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.constant.ApplicationStatusConstants;
import org.innovateuk.ifs.application.form.ApplicantInviteForm;
import org.innovateuk.ifs.application.form.ApplicationTeamUpdateForm;
import org.innovateuk.ifs.application.populator.ApplicationTeamManagementModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.viewmodel.ApplicationTeamManagementApplicantRowViewModel;
import org.innovateuk.ifs.application.viewmodel.ApplicationTeamManagementViewModel;
import org.innovateuk.ifs.commons.error.Error;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.constant.ApplicationStatusConstants.OPEN;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;
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
    public void getUpdateLeadOrganisation_loggedInUserIsLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        InviteOrganisationResource inviteOrganisationsResource = setupOrganisationInviteWithInviteForLeadOrg(applicationResource.getId(), usersMap, organisationsMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        OrganisationResource expectedOrganisation = organisationsMap.get("Empire Ltd");
        Long applicationInviteId = inviteOrganisationsResource.getInviteResources().get(0).getId();

        List<ApplicationTeamManagementApplicantRowViewModel> expectedApplicants = asList(
                new ApplicationTeamManagementApplicantRowViewModel("Steve Smith", "steve.smith@empire.com", true, false, false),
                new ApplicationTeamManagementApplicantRowViewModel(applicationInviteId, "Paul Davidson", "paul.davidson@empire.com", false, false, true));

        ApplicationTeamUpdateForm expectedForm = new ApplicationTeamUpdateForm();
        expectedForm.setExistingApplicants(asList(expectedApplicants.get(0).getEmail(), expectedApplicants.get(1).getEmail()));

        ApplicationTeamManagementViewModel expectedViewModel = new ApplicationTeamManagementViewModel(
                applicationResource.getId(),
                "Application name",
                expectedOrganisation.getId(),
                inviteOrganisationsResource.getId(),
                expectedOrganisation.getName(),
                true,
                true,
                expectedApplicants);

        setLoggedInUser(leadApplicant);
        mockMvc.perform(get("/application/{applicationId}/team/update", applicationResource.getId())
                .param("organisation", expectedOrganisation.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(view().name("application-team/edit-org"));

        InOrder inOrder = inOrder(applicationService, inviteOrganisationRestService, userService, inviteRestService);
        inOrder.verify(applicationService).getLeadOrganisation(applicationResource.getId());
        inOrder.verify(inviteOrganisationRestService).getByOrganisationIdWithInvitesForApplication(expectedOrganisation.getId(), applicationResource.getId());
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(userService).getLeadApplicantProcessRoleOrNull(applicationResource);
        inOrder.verify(userService).findById(leadApplicant.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getUpdateNonLeadOrganisation_loggedInUserIsLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        InviteOrganisationResource inviteOrganisationResource = setupOrganisationInviteForNonLeadOrg(applicationResource.getId(), usersMap, organisationsMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        OrganisationResource expectedOrganisation = organisationsMap.get("Ludlow");
        Long applicationInviteId1 = inviteOrganisationResource.getInviteResources().get(0).getId();
        Long applicationInviteId2 = inviteOrganisationResource.getInviteResources().get(1).getId();

        List<ApplicationTeamManagementApplicantRowViewModel> expectedApplicants = asList(
                new ApplicationTeamManagementApplicantRowViewModel(applicationInviteId1, "Jessica Doe", "jessica.doe@ludlow.com", false, false, true),
                new ApplicationTeamManagementApplicantRowViewModel(applicationInviteId2, "Ryan Dell", "ryan.dell@ludlow.com", false, true, true));

        ApplicationTeamUpdateForm expectedForm = new ApplicationTeamUpdateForm();
        expectedForm.setExistingApplicants(asList(expectedApplicants.get(0).getEmail(), expectedApplicants.get(1).getEmail()));

        ApplicationTeamManagementViewModel expectedViewModel = new ApplicationTeamManagementViewModel(
                applicationResource.getId(),
                "Application name",
                expectedOrganisation.getId(),
                inviteOrganisationResource.getId(),
                expectedOrganisation.getName(),
                false,
                true,
                expectedApplicants);

        setLoggedInUser(leadApplicant);
        mockMvc.perform(get("/application/{applicationId}/team/update", applicationResource.getId())
                .param("organisation", expectedOrganisation.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(view().name("application-team/edit-org"));

        InOrder inOrder = inOrder(applicationService, inviteOrganisationRestService, userService, inviteRestService);
        inOrder.verify(applicationService).getLeadOrganisation(applicationResource.getId());
        inOrder.verify(inviteOrganisationRestService).getByOrganisationIdWithInvitesForApplication(expectedOrganisation.getId(), applicationResource.getId());
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(userService).getLeadApplicantProcessRoleOrNull(applicationResource);
        inOrder.verify(userService).findById(leadApplicant.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getUpdateNonLeadOrganisation_loggedInUserIsNonLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        InviteOrganisationResource inviteOrganisationResource = setupOrganisationInviteForNonLeadOrg(applicationResource.getId(), usersMap, organisationsMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        OrganisationResource expectedOrganisation = organisationsMap.get("Ludlow");
        Long applicationInviteId1 = inviteOrganisationResource.getInviteResources().get(0).getId();
        Long applicationInviteId2 = inviteOrganisationResource.getInviteResources().get(1).getId();

        List<ApplicationTeamManagementApplicantRowViewModel> expectedApplicants = asList(
                new ApplicationTeamManagementApplicantRowViewModel(applicationInviteId1, "Jessica Doe", "jessica.doe@ludlow.com", false, false, false),
                new ApplicationTeamManagementApplicantRowViewModel(applicationInviteId2, "Ryan Dell", "ryan.dell@ludlow.com", false, true, false));

        ApplicationTeamUpdateForm expectedForm = new ApplicationTeamUpdateForm();
        expectedForm.setExistingApplicants(asList(expectedApplicants.get(0).getEmail(), expectedApplicants.get(1).getEmail()));

        ApplicationTeamManagementViewModel expectedViewModel = new ApplicationTeamManagementViewModel(
                applicationResource.getId(),
                "Application name",
                expectedOrganisation.getId(),
                inviteOrganisationResource.getId(),
                expectedOrganisation.getName(),
                false,
                false,
                expectedApplicants);

        setLoggedInUser(usersMap.get("jessica.doe@ludlow.com"));
        mockMvc.perform(get("/application/{applicationId}/team/update", applicationResource.getId())
                .param("organisation", expectedOrganisation.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(view().name("application-team/edit-org"));

        InOrder inOrder = inOrder(applicationService, inviteOrganisationRestService, userService, inviteRestService);
        inOrder.verify(applicationService).getLeadOrganisation(applicationResource.getId());
        inOrder.verify(inviteOrganisationRestService).getByOrganisationIdWithInvitesForApplication(expectedOrganisation.getId(), applicationResource.getId());
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(userService).getLeadApplicantProcessRoleOrNull(applicationResource);
        inOrder.verify(userService).findById(leadApplicant.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getUpdateNewOrganisation_loggedInUserIsLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        InviteOrganisationResource inviteOrganisationResource = setupOrganisationInviteWithAnUnconfirmedOrganisation(usersMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        Long applicationInviteId1 = inviteOrganisationResource.getInviteResources().get(0).getId();
        Long applicationInviteId2 = inviteOrganisationResource.getInviteResources().get(1).getId();

        List<ApplicationTeamManagementApplicantRowViewModel> expectedApplicants = asList(
                new ApplicationTeamManagementApplicantRowViewModel(applicationInviteId1, "Jessica Doe", "jessica.doe@ludlow.com", false, true, true),
                new ApplicationTeamManagementApplicantRowViewModel(applicationInviteId2, "Ryan Dell", "ryan.dell@ludlow.com", false, true, true));

        ApplicationTeamUpdateForm expectedForm = new ApplicationTeamUpdateForm();
        expectedForm.setExistingApplicants(asList(expectedApplicants.get(0).getEmail(), expectedApplicants.get(1).getEmail()));

        ApplicationTeamManagementViewModel expectedViewModel = new ApplicationTeamManagementViewModel(
                applicationResource.getId(),
                "Application name",
                null,
                inviteOrganisationResource.getId(),
                inviteOrganisationResource.getOrganisationName(),
                false,
                true,
                expectedApplicants);

        setLoggedInUser(leadApplicant);
        mockMvc.perform(get("/application/{applicationId}/team/update", applicationResource.getId())
                .param("inviteOrganisation", inviteOrganisationResource.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(view().name("application-team/edit-org"));

        InOrder inOrder = inOrder(applicationService, inviteOrganisationRestService, userService, inviteRestService);
        inOrder.verify(applicationService).getLeadOrganisation(applicationResource.getId());
        inOrder.verify(inviteOrganisationRestService).getById(inviteOrganisationResource.getId());
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
        setupOrganisationInviteWithInviteForLeadOrg(applicationResource.getId(), usersMap, organisationsMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);
        OrganisationResource organisation = organisationsMap.get("Empire Ltd");

        ApplicationTeamUpdateForm expectedForm = new ApplicationTeamUpdateForm();
        List<ApplicantInviteForm> applicants = asList(
                new ApplicantInviteForm("Fred Brown", "fred.brown@empire.com"),
                new ApplicantInviteForm("Joe Smith", "joe.smith@empire.com")
        );
        expectedForm.setApplicants(applicants);
        expectedForm.setExistingApplicants(singletonList(leadApplicant.getEmail()));

        List<ApplicationInviteResource> applicationInvites = asList(
                new ApplicationInviteResource(applicants.get(0).getName(), applicants.get(0).getEmail(), applicationResource.getId()),
                new ApplicationInviteResource(applicants.get(1).getName(), applicants.get(1).getEmail(), applicationResource.getId())
        );
        InviteResultsResource inviteResultsResource = new InviteResultsResource();
        inviteResultsResource.setInvitesSendSuccess(1);
        when(inviteRestService.createInvitesByOrganisation(organisation.getId(), applicationInvites)).thenReturn(restSuccess(inviteResultsResource));

        setLoggedInUser(leadApplicant);
        mockMvc.perform(post("/application/{applicationId}/team/update", applicationResource.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("organisation", organisation.getId().toString())
                .param("updateOrganisation", "")
                .param("applicants[0].name", applicants.get(0).getName())
                .param("applicants[0].email", applicants.get(0).getEmail())
                .param("applicants[1].name", applicants.get(1).getName())
                .param("applicants[1].email", applicants.get(1).getEmail())
                .param("existingApplicants[0]", leadApplicant.getEmail()))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/application/%s/team", applicationResource.getId())));

        InOrder inOrder = inOrder(applicationService, inviteOrganisationRestService, userService, inviteRestService);
        inOrder.verify(inviteRestService).createInvitesByOrganisation(organisation.getId(), applicationInvites);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitUpdateNewOrganisation_loggedInUserIsLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        InviteOrganisationResource inviteOrganisationResource = setupOrganisationInviteWithAnUnconfirmedOrganisation(usersMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);
        UserResource existingApplicant = usersMap.get("jessica.doe@ludlow.com");

        ApplicationTeamUpdateForm expectedForm = new ApplicationTeamUpdateForm();
        List<ApplicantInviteForm> applicants = singletonList(
                new ApplicantInviteForm("Fred Brown", "fred.brown@ludlow.com")
        );
        expectedForm.setApplicants(applicants);
        expectedForm.setExistingApplicants(singletonList(existingApplicant.getEmail()));

        ApplicationInviteResource applicationInvite = newApplicationInviteResource()
                .withId((Long) null)
                .withName(applicants.get(0).getName())
                .withEmail(applicants.get(0).getEmail())
                .withApplication(applicationResource.getId())
                .withInviteOrganisation(inviteOrganisationResource.getId())
                .build();

        InviteResultsResource inviteResultsResource = new InviteResultsResource();
        inviteResultsResource.setInvitesSendSuccess(1);
        when(inviteRestService.saveInvites(singletonList(applicationInvite))).thenReturn(restSuccess(inviteResultsResource));

        setLoggedInUser(leadApplicant);
        mockMvc.perform(post("/application/{applicationId}/team/update", applicationResource.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("inviteOrganisation", inviteOrganisationResource.getId().toString())
                .param("updateOrganisation", "")
                .param("applicants[0].name", applicants.get(0).getName())
                .param("applicants[0].email", applicants.get(0).getEmail())
                .param("existingApplicants[0]", existingApplicant.getEmail()))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/application/%s/team", applicationResource.getId())));

        InOrder inOrder = inOrder(applicationService, inviteOrganisationRestService, userService, inviteRestService);
        inOrder.verify(inviteRestService).saveInvites(singletonList(applicationInvite));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitUpdateNonLeadOrganisation_loggedInUserIsLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        setupOrganisationInviteWithInviteForLeadOrg(applicationResource.getId(), usersMap, organisationsMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);
        UserResource existingApplicant = usersMap.get("jessica.doe@ludlow.com");

        OrganisationResource organisation = organisationsMap.get("Ludlow");

        ApplicationTeamUpdateForm expectedForm = new ApplicationTeamUpdateForm();
        List<ApplicantInviteForm> applicants = singletonList(
                new ApplicantInviteForm("Joe Smith", "joe.smith@empire.com")
        );
        expectedForm.setApplicants(applicants);
        expectedForm.setExistingApplicants(singletonList(existingApplicant.getEmail()));

        ApplicationInviteResource applicationInvite = newApplicationInviteResource()
                .withId((Long) null)
                .withName(applicants.get(0).getName())
                .withEmail(applicants.get(0).getEmail())
                .withApplication(applicationResource.getId())
                .build();
        InviteResultsResource inviteResultsResource = new InviteResultsResource();
        inviteResultsResource.setInvitesSendSuccess(1);
        when(inviteRestService.createInvitesByOrganisation(organisation.getId(), singletonList(applicationInvite))).thenReturn(restSuccess(inviteResultsResource));

        setLoggedInUser(leadApplicant);
        mockMvc.perform(post("/application/{applicationId}/team/update", applicationResource.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("organisation", organisation.getId().toString())
                .param("updateOrganisation", "")
                .param("applicants[0].name", applicants.get(0).getName())
                .param("applicants[0].email", applicants.get(0).getEmail())
                .param("existingApplicants[0]", existingApplicant.getEmail()))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/application/%s/team", applicationResource.getId())));

        InOrder inOrder = inOrder(applicationService, inviteOrganisationRestService, userService, inviteRestService);
        inOrder.verify(inviteRestService).createInvitesByOrganisation(organisation.getId(), singletonList(applicationInvite));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitUpdateNonLeadOrganisation_loggedInUserIsNonLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        setupOrganisationInviteWithInviteForLeadOrg(applicationResource.getId(), usersMap, organisationsMap);
        UserResource existingApplicant = usersMap.get("jessica.doe@ludlow.com");

        OrganisationResource organisation = organisationsMap.get("Ludlow");

        ApplicationTeamUpdateForm expectedForm = new ApplicationTeamUpdateForm();
        List<ApplicantInviteForm> applicants = singletonList(
                new ApplicantInviteForm("Joe Smith", "joe.smith@empire.com")
        );
        expectedForm.setApplicants(applicants);
        expectedForm.setExistingApplicants(singletonList(existingApplicant.getEmail()));

        ApplicationInviteResource applicationInvite = newApplicationInviteResource()
                .withId((Long) null)
                .withName(applicants.get(0).getName())
                .withEmail(applicants.get(0).getEmail())
                .withApplication(applicationResource.getId())
                .build();
        InviteResultsResource inviteResultsResource = new InviteResultsResource();
        inviteResultsResource.setInvitesSendSuccess(1);
        when(inviteRestService.createInvitesByOrganisation(organisation.getId(), singletonList(applicationInvite))).thenReturn(restSuccess(inviteResultsResource));

        setLoggedInUser(usersMap.get("jessica.doe@ludlow.com"));
        mockMvc.perform(post("/application/{applicationId}/team/update", applicationResource.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("organisation", organisation.getId().toString())
                .param("updateOrganisation", "")
                .param("applicants[0].name", applicants.get(0).getName())
                .param("applicants[0].email", applicants.get(0).getEmail())
                .param("existingApplicants[0]", existingApplicant.getEmail()))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/application/%s/team", applicationResource.getId())));

        InOrder inOrder = inOrder(applicationService, inviteOrganisationRestService, userService, inviteRestService);
        inOrder.verify(inviteRestService).createInvitesByOrganisation(organisation.getId(), singletonList(applicationInvite));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitUpdateLeadOrganisation_loggedInUserIsLead_invalidForm() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        setupOrganisationInviteWithInviteForLeadOrg(applicationResource.getId(), usersMap, organisationsMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        OrganisationResource organisation = organisationsMap.get("Empire Ltd");
        List<ApplicantInviteForm> applicants = singletonList(
                new ApplicantInviteForm("Joe Smith", "joe.smith@empire.com")
        );

        setLoggedInUser(leadApplicant);
        MvcResult result = mockMvc.perform(post("/application/{applicationId}/team/update", applicationResource.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("organisation", organisation.getId().toString())
                .param("updateOrganisation", "")
                .param("applicants[0].name", applicants.get(0).getName())
                .param("existingApplicants[0]", leadApplicant.getEmail()))
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

        InOrder inOrder = inOrder(applicationService, inviteOrganisationRestService, userService, inviteRestService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(userService).getLeadApplicantProcessRoleOrNull(applicationResource);
        inOrder.verify(userService).findById(leadApplicant.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitUpdateLeadOrganisation_duplicateApplicantWebTier() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        setupOrganisationInviteWithInviteForLeadOrg(applicationResource.getId(), usersMap, organisationsMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        OrganisationResource organisation = organisationsMap.get("Empire Ltd");
        List<ApplicantInviteForm> applicants = singletonList(
                new ApplicantInviteForm("Steve Smith", "steve.smith@empire.com")
        );

        setLoggedInUser(leadApplicant);
        MvcResult result = mockMvc.perform(post("/application/{applicationId}/team/update", applicationResource.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("organisation", organisation.getId().toString())
                .param("updateOrganisation", "")
                .param("applicants[0].name", applicants.get(0).getName())
                .param("applicants[0].email", applicants.get(0).getEmail())
                .param("existingApplicants[0]", leadApplicant.getEmail()))
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
        assertEquals("You have used this email address for another applicant.", bindingResult.getFieldError("applicants[0].email").getDefaultMessage());

        InOrder inOrder = inOrder(applicationService, inviteOrganisationRestService, userService, inviteRestService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(userService).getLeadApplicantProcessRoleOrNull(applicationResource);
        inOrder.verify(userService).findById(leadApplicant.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitUpdateLeadOrganisation_duplicateApplicantDataTier() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        setupOrganisationInviteWithInviteForLeadOrg(applicationResource.getId(), usersMap, organisationsMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        OrganisationResource organisation = organisationsMap.get("Empire Ltd");
        List<ApplicantInviteForm> applicants = singletonList(
                new ApplicantInviteForm("Steve Smith", "steve.smith@empire.com")
        );

        ApplicationInviteResource applicationInvite = newApplicationInviteResource()
                .withId((Long) null)
                .withName(applicants.get(0).getName())
                .withEmail(applicants.get(0).getEmail())
                .withApplication(applicationResource.getId())
                .build();
        InviteResultsResource inviteResultsResource = new InviteResultsResource();
        inviteResultsResource.setInvitesSendSuccess(1);

        List<Error> errors = new ArrayList<>();
        errors.add(fieldError("applicants[0].email", applicants.get(0).getEmail(), "email.already.in.invite", "You have used this email address for another applicant."));
        when(inviteRestService.createInvitesByOrganisation(organisation.getId(), singletonList(applicationInvite))).thenReturn(restFailure(errors));

        setLoggedInUser(leadApplicant);
        MvcResult result = mockMvc.perform(post("/application/{applicationId}/team/update", applicationResource.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("organisation", organisation.getId().toString())
                .param("updateOrganisation", "")
                .param("applicants[0].name", applicants.get(0).getName())
                .param("applicants[0].email", applicants.get(0).getEmail())
                .param("existingApplicants[0]", "phil.jones@empire.com"))
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
        assertEquals("You have used this email address for another applicant.", bindingResult.getFieldError("applicants[0].email").getArguments()[0]);

        InOrder inOrder = inOrder(applicationService, inviteOrganisationRestService, userService, inviteRestService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(userService).getLeadApplicantProcessRoleOrNull(applicationResource);
        inOrder.verify(userService).findById(leadApplicant.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void addApplicantLeadOrganisation_loggedInUserIsLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        setupOrganisationInviteWithInviteForLeadOrg(applicationResource.getId(), usersMap, organisationsMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);
        OrganisationResource organisation = organisationsMap.get("Empire Ltd");

        List<ApplicantInviteForm> applicants = singletonList(
                new ApplicantInviteForm("Fred Brown", "fred.brown@empire.com")
        );

        InviteResultsResource inviteResultsResource = new InviteResultsResource();
        inviteResultsResource.setInvitesSendSuccess(1);

        setLoggedInUser(leadApplicant);
        MvcResult mockResult = mockMvc.perform(post("/application/{applicationId}/team/update", applicationResource.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("organisation", organisation.getId().toString())
                .param("addApplicant", "true")
                .param("applicants[0].name", applicants.get(0).getName())
                .param("applicants[0].email", applicants.get(0).getEmail()))
                .andExpect(status().isOk())
                .andExpect(view().name("application-team/edit-org"))
                .andReturn();

        ApplicationTeamManagementViewModel viewModelResult = (ApplicationTeamManagementViewModel) mockResult.getModelAndView().getModel().get("model");
        assertEquals(2, viewModelResult.getApplicants().size());
        assertEquals("Steve Smith", viewModelResult.getApplicants().get(0).getName());
        assertEquals("Paul Davidson", viewModelResult.getApplicants().get(1).getName());

        ApplicationTeamUpdateForm formResult = (ApplicationTeamUpdateForm) mockResult.getModelAndView().getModel().get("form");
        assertEquals(2, formResult.getApplicants().size());
        assertEquals(applicants.get(0).getName(), formResult.getApplicants().get(0).getName());
        assertEquals(applicants.get(0).getEmail(), formResult.getApplicants().get(0).getEmail());

        InOrder inOrder = inOrder(applicationService, inviteOrganisationRestService, userService, inviteRestService);
        inOrder.verify(applicationService).getLeadOrganisation(applicationResource.getId());
        inOrder.verify(inviteOrganisationRestService).getByOrganisationIdWithInvitesForApplication(organisation.getId(), applicationResource.getId());
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(userService).getLeadApplicantProcessRoleOrNull(applicationResource);
        inOrder.verify(userService).findById(leadApplicant.getId());

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void addApplicantNewOrganisation_loggedInUserIsLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        InviteOrganisationResource inviteOrganisationResource = setupOrganisationInviteWithAnUnconfirmedOrganisation(usersMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        List<ApplicantInviteForm> applicants = singletonList(
                new ApplicantInviteForm("Fred Brown", "fred.brown@ludlow.com")
        );

        setLoggedInUser(leadApplicant);
        MvcResult mockResult = mockMvc.perform(post("/application/{applicationId}/team/update", applicationResource.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("inviteOrganisation", inviteOrganisationResource.getId().toString())
                .param("addApplicant", "true")
                .param("applicants[0].name", applicants.get(0).getName())
                .param("applicants[0].email", applicants.get(0).getEmail()))
                .andExpect(status().isOk())
                .andExpect(view().name("application-team/edit-org"))
                .andReturn();

        ApplicationTeamManagementViewModel viewModelResult = (ApplicationTeamManagementViewModel) mockResult.getModelAndView().getModel().get("model");
        assertEquals(2, viewModelResult.getApplicants().size());
        assertEquals("Jessica Doe", viewModelResult.getApplicants().get(0).getName());
        assertEquals("Ryan Dell", viewModelResult.getApplicants().get(1).getName());

        ApplicationTeamUpdateForm formResult = (ApplicationTeamUpdateForm) mockResult.getModelAndView().getModel().get("form");
        assertEquals(2, formResult.getApplicants().size());
        assertEquals(applicants.get(0).getName(), formResult.getApplicants().get(0).getName());
        assertEquals(applicants.get(0).getEmail(), formResult.getApplicants().get(0).getEmail());

        InOrder inOrder = inOrder(applicationService, inviteOrganisationRestService, userService, inviteRestService);
        inOrder.verify(applicationService).getLeadOrganisation(applicationResource.getId());
        inOrder.verify(inviteOrganisationRestService).getById(inviteOrganisationResource.getId());
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(userService).getLeadApplicantProcessRoleOrNull(applicationResource);
        inOrder.verify(userService).findById(leadApplicant.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void removeApplicantLeadOrganisation_loggedInUserIsLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        setupOrganisationInviteWithInviteForLeadOrg(applicationResource.getId(), usersMap, organisationsMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);
        OrganisationResource organisation = organisationsMap.get("Empire Ltd");

        List<ApplicantInviteForm> applicants = singletonList(
                new ApplicantInviteForm("Fred Brown", "fred.brown@empire.com")
        );

        InviteResultsResource inviteResultsResource = new InviteResultsResource();
        inviteResultsResource.setInvitesSendSuccess(1);

        setLoggedInUser(leadApplicant);
        MvcResult mockResult = mockMvc.perform(post("/application/{applicationId}/team/update", applicationResource.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("organisation", organisation.getId().toString())
                .param("removeApplicant", "0")
                .param("applicants[0].name", applicants.get(0).getName())
                .param("applicants[0].email", applicants.get(0).getEmail()))
                .andExpect(status().isOk())
                .andExpect(view().name("application-team/edit-org"))
                .andReturn();

        ApplicationTeamManagementViewModel viewModelResult = (ApplicationTeamManagementViewModel) mockResult.getModelAndView().getModel().get("model");
        assertEquals(2, viewModelResult.getApplicants().size());
        assertEquals("Steve Smith", viewModelResult.getApplicants().get(0).getName());
        assertEquals("Paul Davidson", viewModelResult.getApplicants().get(1).getName());

        ApplicationTeamUpdateForm formResult = (ApplicationTeamUpdateForm) mockResult.getModelAndView().getModel().get("form");
        assertEquals(0, formResult.getApplicants().size());

        InOrder inOrder = inOrder(applicationService, inviteOrganisationRestService, userService, inviteRestService);
        inOrder.verify(applicationService).getLeadOrganisation(applicationResource.getId());
        inOrder.verify(inviteOrganisationRestService).getByOrganisationIdWithInvitesForApplication(organisation.getId(), applicationResource.getId());
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(userService).getLeadApplicantProcessRoleOrNull(applicationResource);
        inOrder.verify(userService).findById(leadApplicant.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void removeApplicantNewOrganisation_loggedInUserIsLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        InviteOrganisationResource inviteOrganisationResource = setupOrganisationInviteWithAnUnconfirmedOrganisation(usersMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        List<ApplicantInviteForm> applicants = singletonList(
                new ApplicantInviteForm("Fred Brown", "fred.brown@ludlow.com")
        );

        setLoggedInUser(leadApplicant);
        MvcResult mockResult = mockMvc.perform(post("/application/{applicationId}/team/update", applicationResource.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("inviteOrganisation", inviteOrganisationResource.getId().toString())
                .param("removeApplicant", "0")
                .param("applicants[0].name", applicants.get(0).getName())
                .param("applicants[0].email", applicants.get(0).getEmail()))
                .andExpect(status().isOk())
                .andExpect(view().name("application-team/edit-org"))
                .andReturn();

        ApplicationTeamManagementViewModel viewModelResult = (ApplicationTeamManagementViewModel) mockResult.getModelAndView().getModel().get("model");
        assertEquals(2, viewModelResult.getApplicants().size());
        assertEquals("Jessica Doe", viewModelResult.getApplicants().get(0).getName());
        assertEquals("Ryan Dell", viewModelResult.getApplicants().get(1).getName());

        ApplicationTeamUpdateForm formResult = (ApplicationTeamUpdateForm) mockResult.getModelAndView().getModel().get("form");
        assertEquals(0, formResult.getApplicants().size());

        InOrder inOrder = inOrder(applicationService, inviteOrganisationRestService, userService, inviteRestService);
        inOrder.verify(applicationService).getLeadOrganisation(applicationResource.getId());
        inOrder.verify(inviteOrganisationRestService).getById(inviteOrganisationResource.getId());
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(userService).getLeadApplicantProcessRoleOrNull(applicationResource);
        inOrder.verify(userService).findById(leadApplicant.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void markApplicantLeadOrganisation_loggedInUserIsLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        InviteOrganisationResource inviteOrganisationResource = setupOrganisationInviteWithInviteForLeadOrg(applicationResource.getId(), usersMap, organisationsMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);
        OrganisationResource organisation = organisationsMap.get("Empire Ltd");
        Long applicationInviteId = inviteOrganisationResource.getInviteResources().get(0).getId();

        List<ApplicantInviteForm> applicants = singletonList(
                new ApplicantInviteForm("Fred Brown", "fred.brown@empire.com")
        );

        setLoggedInUser(leadApplicant);
        MvcResult mockResult = mockMvc.perform(post("/application/{applicationId}/team/update", applicationResource.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("organisation", organisation.getId().toString())
                .param("markForRemoval", applicationInviteId.toString())
                .param("applicants[0].name", applicants.get(0).getName())
                .param("applicants[0].email", applicants.get(0).getEmail()))
                .andExpect(status().isOk())
                .andExpect(view().name("application-team/edit-org"))
                .andReturn();

        ApplicationTeamManagementViewModel viewModelResult = (ApplicationTeamManagementViewModel) mockResult.getModelAndView().getModel().get("model");
        assertEquals(2, viewModelResult.getApplicants().size());
        assertEquals("Steve Smith", viewModelResult.getApplicants().get(0).getName());
        assertEquals("Paul Davidson", viewModelResult.getApplicants().get(1).getName());

        ApplicationTeamUpdateForm formResult = (ApplicationTeamUpdateForm) mockResult.getModelAndView().getModel().get("form");
        assertEquals(1, formResult.getMarkedForRemoval().size());
        assertTrue(formResult.getMarkedForRemoval().contains(applicationInviteId));

        InOrder inOrder = inOrder(applicationService, inviteOrganisationRestService, userService, inviteRestService);
        inOrder.verify(applicationService).getLeadOrganisation(applicationResource.getId());
        inOrder.verify(inviteOrganisationRestService).getByOrganisationIdWithInvitesForApplication(organisation.getId(), applicationResource.getId());
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(userService).getLeadApplicantProcessRoleOrNull(applicationResource);
        inOrder.verify(userService).findById(leadApplicant.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void markApplicantNewOrganisation_loggedInUserIsLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        InviteOrganisationResource inviteOrganisationResource = setupOrganisationInviteWithAnUnconfirmedOrganisation(usersMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);
        Long applicationInviteId = inviteOrganisationResource.getInviteResources().get(0).getId();

        List<ApplicantInviteForm> applicants = singletonList(
                new ApplicantInviteForm("Fred Brown", "fred.brown@ludlow.com")
        );

        setLoggedInUser(leadApplicant);
        MvcResult mockResult = mockMvc.perform(post("/application/{applicationId}/team/update", applicationResource.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("inviteOrganisation", inviteOrganisationResource.getId().toString())
                .param("markForRemoval", applicationInviteId.toString())
                .param("applicants[0].name", applicants.get(0).getName())
                .param("applicants[0].email", applicants.get(0).getEmail()))
                .andExpect(status().isOk())
                .andExpect(view().name("application-team/edit-org"))
                .andReturn();

        ApplicationTeamManagementViewModel viewModelResult = (ApplicationTeamManagementViewModel) mockResult.getModelAndView().getModel().get("model");
        assertEquals(2, viewModelResult.getApplicants().size());
        assertEquals("Jessica Doe", viewModelResult.getApplicants().get(0).getName());
        assertEquals("Ryan Dell", viewModelResult.getApplicants().get(1).getName());

        ApplicationTeamUpdateForm formResult = (ApplicationTeamUpdateForm) mockResult.getModelAndView().getModel().get("form");
        assertEquals(1, formResult.getMarkedForRemoval().size());
        assertTrue(formResult.getMarkedForRemoval().contains(applicationInviteId));

        InOrder inOrder = inOrder(applicationService, inviteOrganisationRestService, userService, inviteRestService);
        inOrder.verify(applicationService).getLeadOrganisation(applicationResource.getId());
        inOrder.verify(inviteOrganisationRestService).getById(inviteOrganisationResource.getId());
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(userService).getLeadApplicantProcessRoleOrNull(applicationResource);
        inOrder.verify(userService).findById(leadApplicant.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void confirmDeleteOrganisation() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        InviteOrganisationResource inviteOrganisationResource = setupOrganisationInviteForNonLeadOrg(applicationResource.getId(), usersMap, organisationsMap);
        OrganisationResource organisation = organisationsMap.get("Ludlow");

        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        setLoggedInUser(leadApplicant);
        MvcResult mockResult = mockMvc.perform(get("/application/{applicationId}/team/update", applicationResource.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("organisation", organisation.getId().toString())
                .param("deleteOrganisation", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("application-team/delete-org"))
                .andReturn();

        ApplicationTeamManagementViewModel model = (ApplicationTeamManagementViewModel) mockResult.getModelAndView().getModel().get("model");
        assertEquals(organisation.getName(), model.getOrganisationName());
        assertEquals(applicationResource.getName(), model.getApplicationName());
        assertEquals(organisation.getId(), model.getOrganisationId());

        InOrder inOrder = inOrder(applicationService, inviteOrganisationRestService, userService, inviteRestService);
        inOrder.verify(applicationService).getLeadOrganisation(applicationResource.getId());
        inOrder.verify(inviteOrganisationRestService).getByOrganisationIdWithInvitesForApplication(organisation.getId(), applicationResource.getId());
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(userService).getLeadApplicantProcessRoleOrNull(applicationResource);
        inOrder.verify(userService).findById(leadApplicant.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void confirmDeleteInviteOrganisation() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        InviteOrganisationResource inviteOrganisationResource = setupOrganisationInviteWithAnUnconfirmedOrganisation(usersMap);
        OrganisationResource organisation = organisationsMap.get("Ludlow");

        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        setLoggedInUser(leadApplicant);
        MvcResult mockResult = mockMvc.perform(get("/application/{applicationId}/team/update", applicationResource.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("inviteOrganisation", inviteOrganisationResource.getId().toString())
                .param("deleteOrganisation", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("application-team/delete-org"))
                .andReturn();

        ApplicationTeamManagementViewModel model = (ApplicationTeamManagementViewModel) mockResult.getModelAndView().getModel().get("model");
        assertEquals(organisation.getName(), model.getOrganisationName());
        assertEquals(applicationResource.getName(), model.getApplicationName());
        assertEquals(inviteOrganisationResource.getId(), model.getInviteOrganisationId());

        InOrder inOrder = inOrder(applicationService, inviteOrganisationRestService, userService, inviteRestService);
        inOrder.verify(applicationService).getLeadOrganisation(applicationResource.getId());
        inOrder.verify(inviteOrganisationRestService).getById(inviteOrganisationResource.getId());
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(userService).getLeadApplicantProcessRoleOrNull(applicationResource);
        inOrder.verify(userService).findById(leadApplicant.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void deleteOrganisation_loggedInUserIsLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        InviteOrganisationResource inviteOrganisationResource = setupOrganisationInviteForNonLeadOrg(applicationResource.getId(), usersMap, organisationsMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);
        OrganisationResource organisation = organisationsMap.get("Ludlow");

        when(applicationService.removeCollaborator(isA(Long.class))).thenReturn(serviceSuccess());

        setLoggedInUser(leadApplicant);
        mockMvc.perform(post("/application/{applicationId}/team/update", applicationResource.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("organisation", organisation.getId().toString())
                .param("deleteOrganisation", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/application/%s/team", applicationResource.getId())));

        InOrder inOrder = inOrder(applicationService, inviteOrganisationRestService, userService, inviteRestService);
        inOrder.verify(inviteOrganisationRestService).getByOrganisationIdWithInvitesForApplication(organisation.getId(), applicationResource.getId());
        inOrder.verify(applicationService).removeCollaborator(inviteOrganisationResource.getInviteResources().get(0).getId());
        inOrder.verify(applicationService).removeCollaborator(inviteOrganisationResource.getInviteResources().get(1).getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void deleteInviteOrganisation_loggedInUserIsLead() throws Exception {
        Map<String, OrganisationResource> organisationsMap = setupOrganisationResources();
        ApplicationResource applicationResource = setupApplicationResource(organisationsMap);
        Map<String, UserResource> usersMap = setupUserResources();
        InviteOrganisationResource inviteOrganisationResource = setupOrganisationInviteWithAnUnconfirmedOrganisation(usersMap);
        UserResource leadApplicant = setupLeadApplicant(applicationResource, usersMap);

        when(applicationService.removeCollaborator(isA(Long.class))).thenReturn(serviceSuccess());

        setLoggedInUser(leadApplicant);
        mockMvc.perform(post("/application/{applicationId}/team/update", applicationResource.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("inviteOrganisation", inviteOrganisationResource.getId().toString())
                .param("deleteOrganisation", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/application/%s/team", applicationResource.getId())));

        InOrder inOrder = inOrder(applicationService, inviteOrganisationRestService, userService, inviteRestService);
        inOrder.verify(inviteOrganisationRestService).getById(inviteOrganisationResource.getId());
        inOrder.verify(applicationService).removeCollaborator(inviteOrganisationResource.getInviteResources().get(0).getId());
        inOrder.verify(applicationService).removeCollaborator(inviteOrganisationResource.getInviteResources().get(1).getId());
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
                .withFirstName("Jessica", "Steve", "Paul")
                .withLastName("Doe", "Smith", "Davidson")
                .withEmail("jessica.doe@ludlow.com", "steve.smith@empire.com", "paul.davidson@empire.com")
                .build(3);

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
                .withName("Ludlow", "Empire Ltd")
                .build(2);

        return simpleToMap(organisationResources, OrganisationResource::getName);
    }

    private InviteOrganisationResource setupOrganisationInviteWithInviteForLeadOrg(long applicationId,
                                                                                   Map<String, UserResource> usersMap,
                                                                                   Map<String, OrganisationResource> organisationsMap) {
        UserResource user = usersMap.get("paul.davidson@empire.com");
        OrganisationResource organisation = organisationsMap.get("Empire Ltd");

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withOrganisation(organisation.getId())
                .withOrganisationName(organisation.getName())
                .withOrganisationNameConfirmed(organisation.getName())
                .withInviteResources(newApplicationInviteResource()
                        .withUsers(user.getId())
                        .withNameConfirmed(user.getName())
                        .withName(user.getName())
                        .withEmail(user.getEmail())
                        .withStatus(OPENED)
                        .build(1))
                .build();

        when(inviteOrganisationRestService.getById(inviteOrganisationResource.getId()))
                .thenReturn(restSuccess(inviteOrganisationResource));
        when(inviteOrganisationRestService.getByOrganisationIdWithInvitesForApplication(organisation.getId(), applicationId))
                .thenReturn(restSuccess(inviteOrganisationResource));
        return inviteOrganisationResource;
    }

    private InviteOrganisationResource setupOrganisationInviteForNonLeadOrg(long applicationId,
                                                                            Map<String, UserResource> usersMap,
                                                                            Map<String, OrganisationResource> organisationsMap) {
        UserResource user = usersMap.get("jessica.doe@ludlow.com");
        OrganisationResource organisation = organisationsMap.get("Ludlow");

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withOrganisation(organisation.getId())
                .withOrganisationName(organisation.getName())
                .withOrganisationNameConfirmed(organisation.getName())
                .withInviteResources(newApplicationInviteResource()
                        .withUsers(user.getId(), null)
                        .withNameConfirmed(user.getName(), null)
                        .withName("Jess Doe", "Ryan Dell")
                        .withEmail(user.getEmail(), "ryan.dell@ludlow.com")
                        .withStatus(OPENED, SENT)
                        .build(2))
                .build();

        when(inviteOrganisationRestService.getById(inviteOrganisationResource.getId()))
                .thenReturn(restSuccess(inviteOrganisationResource));
        when(inviteOrganisationRestService.getByOrganisationIdWithInvitesForApplication(organisation.getId(), applicationId))
                .thenReturn(restSuccess(inviteOrganisationResource));
        return inviteOrganisationResource;
    }

    private InviteOrganisationResource setupOrganisationInviteWithAnUnconfirmedOrganisation(Map<String, UserResource> usersMap) {
        UserResource user = usersMap.get("jessica.doe@ludlow.com");

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withOrganisationName("Ludlow")
                .withInviteResources(newApplicationInviteResource()
                        .withUsers(user.getId(), null)
                        .withNameConfirmed(user.getName(), null)
                        .withName("Jess Doe", "Ryan Dell")
                        .withEmail(user.getEmail(), "ryan.dell@ludlow.com")
                        .withStatus(SENT, SENT)
                        .build(2))
                .build();

        when(inviteOrganisationRestService.getById(inviteOrganisationResource.getId()))
                .thenReturn(restSuccess(inviteOrganisationResource));

        return inviteOrganisationResource;
    }
}