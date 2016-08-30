package com.worth.ifs.project;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.AddressTypeResource;
import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.bankdetails.form.ProjectDetailsAddressForm;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.invite.resource.InviteProjectResource;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.project.viewmodel.ProjectDetailsAddressViewModel;
import com.worth.ifs.project.viewmodel.ProjectDetailsStartDateForm;
import com.worth.ifs.project.viewmodel.ProjectDetailsStartDateViewModel;
import com.worth.ifs.project.viewmodel.ProjectDetailsViewModel;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.worth.ifs.BaseBuilderAmendFunctions.name;
import static com.worth.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static com.worth.ifs.address.builder.AddressTypeResourceBuilder.newAddressTypeResource;
import static com.worth.ifs.address.resource.OrganisationAddressType.*;
import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.organisation.builder.OrganisationAddressResourceBuilder.newOrganisationAddressResource;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.PARTNER;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectDetailsControllerTest extends BaseControllerMockMVCTest<ProjectDetailsController> {
	
	@Before
	public void setUp() {
		super.setUp();
		setupInvites();
		loginDefaultUser();
		loggedInUser.setOrganisations(Collections.singletonList(8L));
	}
	
    @Override
    protected ProjectDetailsController supplyControllerUnderTest() {
        return new ProjectDetailsController();
    }
    
    @Test
    public void testCompetitionDetailsCompetitionId() throws Exception {
        CompetitionResource competitionResource = newCompetitionResource().build();
    	ApplicationResource applicationResource = newApplicationResource().withCompetition(competitionResource.getId()).build();
        ProjectResource project = newProjectResource().withApplication(applicationResource).build();

        OrganisationResource leadOrganisation = newOrganisationResource().build();

        List<ProjectUserResource> projectUsers = newProjectUserResource().
                withUser(loggedInUser.getId()).
                withOrganisation(leadOrganisation.getId()).
                withRoleName(PARTNER.getName()).
                build(1);

        when(applicationService.getById(project.getApplication())).thenReturn(applicationResource);
        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(leadOrganisation);
        when(organisationRestService.getOrganisationById(leadOrganisation.getId())).thenReturn(restSuccess(leadOrganisation));

        when(competitionService.getById(applicationResource.getCompetition())).thenReturn(competitionResource);
        when(projectService.isSubmitAllowed(project.getId())).thenReturn(serviceSuccess(false));

        MvcResult result = mockMvc.perform(get("/project/{id}/details", project.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("project/detail"))
                .andExpect(model().attribute("isSubmissionAllowed", false))
                .andReturn();

        ProjectDetailsViewModel viewModel = (ProjectDetailsViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(project, viewModel.getProject());
        assertEquals(applicationResource, viewModel.getApp());
        assertEquals(competitionResource, viewModel.getCompetition());
    }
    
    @Test
    public void testProjectDetailsProjectManager() throws Exception {
    	Long projectId = 20L;

        CompetitionResource competitionResource = newCompetitionResource().build();
    	ApplicationResource applicationResource = newApplicationResource().withCompetition(competitionResource.getId()).build();
        ProjectResource project = newProjectResource().withId(projectId).build();

        OrganisationResource leadOrganisation = newOrganisationResource().build();

        List<ProjectUserResource> projectUsers = newProjectUserResource().
                withUser(loggedInUser.getId()).
                withOrganisation(leadOrganisation.getId()).
                withRoleName(PARTNER.getName()).
                build(1);

        when(applicationService.getById(project.getApplication())).thenReturn(applicationResource);
        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(leadOrganisation);
        when(projectService.isUserLeadPartner(projectId, loggedInUser.getId())).thenReturn(true);

        mockMvc.perform(get("/project/{id}/details/project-manager", projectId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("project", project))
                .andExpect(model().attribute("app", applicationResource))
                .andExpect(view().name("project/project-manager"));
    }
    
    @Test
    public void testProjectDetailsSetProjectManager() throws Exception {
    	Long projectId = 20L;
    	Long projectManagerUserId = 80L;

        CompetitionResource competitionResource = newCompetitionResource().build();
    	ApplicationResource applicationResource = newApplicationResource().withCompetition(competitionResource.getId()).build();
        ProjectResource project = newProjectResource().withId(projectId).build();

        OrganisationResource leadOrganisation = newOrganisationResource().build();

        List<ProjectUserResource> projectUsers = newProjectUserResource().
                withUser(loggedInUser.getId(), projectManagerUserId).
                withOrganisation(leadOrganisation.getId()).
                withRoleName(PARTNER.getName()).
                build(2);

        when(applicationService.getById(project.getApplication())).thenReturn(applicationResource);
        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(leadOrganisation);
        when(competitionService.getById(applicationResource.getCompetition())).thenReturn(competitionResource);
        when(projectService.updateProjectManager(projectId, projectManagerUserId)).thenReturn(serviceSuccess());

        ProcessRoleResource processRoleResource = new ProcessRoleResource();
        processRoleResource.setUser(projectManagerUserId);
        when(userService.getLeadPartnerOrganisationProcessRoles(applicationResource)).thenReturn(singletonList(processRoleResource));

        when(projectService.updateProjectManager(projectId, projectManagerUserId)).thenReturn(serviceSuccess());

        
        mockMvc.perform(post("/project/{id}/details/project-manager", projectId)
        		.param("projectManager", projectManagerUserId.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/project/" + projectId + "/details"));
        
        verify(projectService).updateProjectManager(projectId, projectManagerUserId);
    }

    @Test
    public void testViewStartDate() throws Exception {
        ApplicationResource applicationResource = newApplicationResource().build();

        ProjectResource project = newProjectResource().
                withApplication(applicationResource).
                with(name("My Project")).
                withDuration(4L).
                withTargetStartDate(LocalDate.now().withDayOfMonth(5)).
                withDuration(4L).
                build();

        OrganisationResource leadOrganisation = newOrganisationResource().build();
        List<ProjectUserResource> projectUsers = newProjectUserResource().
                withUser(loggedInUser.getId()).
                withOrganisation(leadOrganisation.getId()).
                withRoleName(PARTNER.getName()).
                build(1);

        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(leadOrganisation);

        MvcResult result = mockMvc.perform(get("/project/{id}/details/start-date", project.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("project/details-start-date"))
                .andReturn();

        Map<String, Object> model = result.getModelAndView().getModel();
        ProjectDetailsStartDateViewModel viewModel = (ProjectDetailsStartDateViewModel) model.get("model");

        assertEquals(project.getName(), viewModel.getProjectName());
        assertEquals(project.getFormattedId(), viewModel.getProjectNumber());
        assertEquals(project.getDurationInMonths(), Long.valueOf(viewModel.getProjectDurationInMonths()));

        ProjectDetailsStartDateForm form = (ProjectDetailsStartDateForm) model.get(ProjectDetailsController.FORM_ATTR_NAME);
        assertEquals(project.getTargetStartDate().withDayOfMonth(1), form.getProjectStartDate());
    }

    @Test
    public void testUpdateStartDate() throws Exception {
        ApplicationResource applicationResource = newApplicationResource().build();
        ProjectResource projectResource = newProjectResource().withApplication(applicationResource).build();

        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);
        when(applicationService.getById(projectResource.getApplication())).thenReturn(applicationResource);
        when(projectService.updateProjectStartDate(projectResource.getId(), LocalDate.of(2017, 6, 3))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{id}/details/start-date", projectResource.getId()).
                    contentType(MediaType.APPLICATION_FORM_URLENCODED).
                    param("projectStartDate", "projectStartDate").
                    param("projectStartDate.dayOfMonth", "3").
                    param("projectStartDate.monthValue", "6").
                    param("projectStartDate.year", "2017"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/project/" + projectResource.getId() + "/details"))
                .andReturn();

    }

    @Test
    public void testUpdateFinanceContact() throws Exception {

        long competitionId = 1L;
        long applicationId = 1L;
        long projectId = 1L;
        long organisationId = 1L;
        long loggedInUserId= 1L;
        long invitedUserId = 2L;

        UserResource financeContactUserResource = newUserResource().withId(invitedUserId).withFirstName("First").withLastName("Last").withEmail("test@test.com").build();


        String invitedUserName = "First Last";
        String invitedUserEmail = "test@test.com";

        CompetitionResource competitionResource = newCompetitionResource().withId(competitionId).build();
        ApplicationResource applicationResource = newApplicationResource().withId(applicationId).withCompetition(competitionId).build();
        ProjectResource projectResource = newProjectResource().withId(projectId).withApplication(applicationId).build();

        List<ProjectUserResource> availableUsers = newProjectUserResource().
                withUser(loggedInUser.getId(), loggedInUserId).
                withOrganisation(organisationId).
                withRoleName(PARTNER).
                build(2);

        OrganisationResource leadOrganisation = newOrganisationResource().withName("Lead Organisation").build();

        InviteProjectResource inviteProjectResource = new InviteProjectResource(invitedUserName, invitedUserEmail, projectId);
        inviteProjectResource.setUser(invitedUserId);
        inviteProjectResource.setOrganisation(organisationId);
        inviteProjectResource.setInviteOrganisation(organisationId);
        inviteProjectResource.setApplicationId(applicationId);
        inviteProjectResource.setLeadOrganisation(leadOrganisation.getName());

        when(projectService.getProjectUsersForProject(projectId)).thenReturn(availableUsers);
        when(projectService.updateFinanceContact(projectId, organisationId, invitedUserId)).thenReturn(serviceSuccess());
        when(userService.findById(invitedUserId)).thenReturn(financeContactUserResource);
        when(projectService.getById(projectId)).thenReturn(projectResource);
        when(projectService.getLeadOrganisation(projectId)).thenReturn(leadOrganisation);
        when(applicationService.getById(applicationId)).thenReturn(applicationResource);
        when(competitionService.getById(applicationResource.getCompetition())).thenReturn(competitionResource);
        when(userService.getOrganisationProcessRoles(applicationResource, organisationId)).thenReturn(emptyList());
        when(projectService.saveProjectInvite(inviteProjectResource)).thenReturn(serviceSuccess());
        when(projectService.inviteFinanceContact(projectId, inviteProjectResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{id}/details/finance-contact", projectId).
                    contentType(MediaType.APPLICATION_FORM_URLENCODED).
                    param("organisation", "1").
                    param("financeContact", "2")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + projectId  + "/details")).
                andReturn();

        verify(projectService).updateFinanceContact(projectId, organisationId, invitedUserId);
    }


//    @Test
//    public void testUpdateFinanceContact() throws Exception {
//
//        List<ProjectUserResource> availableUsers = newProjectUserResource().
//                withUser(loggedInUser.getId(), 789L).
//                withOrganisation(8L).
//                withRoleName(PARTNER).
//                build(2);
//
//        when(projectService.getProjectUsersForProject(123L)).thenReturn(availableUsers);
//        when(projectService.updateFinanceContact(123L, 8L, 789L)).thenReturn(serviceSuccess());
//
//        mockMvc.perform(post("/project/{id}/details/finance-contact", 123L).
//                contentType(MediaType.APPLICATION_FORM_URLENCODED).
//                param("organisation", "8").
//                param("financeContact", "789")).
//                andExpect(status().is3xxRedirection()).
//                andExpect(view().name("redirect:/project/123/details")).
//                andReturn();
//
//        verify(projectService).updateFinanceContact(123L, 8L, 789L);
//    }


    @Test
    public void testAddressTypeValidation() throws Exception {
        ApplicationResource applicationResource = newApplicationResource().build();
        ProjectResource project = newProjectResource().withApplication(applicationResource).build();
        OrganisationResource organisationResource = newOrganisationResource().build();

        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(organisationResource);
        when(organisationService.getOrganisationById(organisationResource.getId())).thenReturn(organisationResource);

        mockMvc.perform(post("/project/{id}/details/project-address", project.getId()).
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                param("addressType", "")).
                andExpect(status().isOk()).
                andExpect(view().name("project/details-address")).
                andExpect(model().hasErrors()).
                andExpect(model().attributeHasFieldErrors("form", "addressType")).
                andReturn();
    }

    @Test
    public void testViewAddress() throws Exception {
        OrganisationResource organisationResource = newOrganisationResource().build();
        AddressResource addressResource = newAddressResource().withOrganisationList(Collections.singletonList(organisationResource.getId())).build();
        AddressTypeResource addressTypeResource = newAddressTypeResource().withId((long)REGISTERED.getOrdinal()).withName(REGISTERED.name()).build();
        OrganisationAddressResource organisationAddressResource = newOrganisationAddressResource().withAddressType(addressTypeResource).withAddress(addressResource).build();
        organisationResource.setAddresses(Collections.singletonList(organisationAddressResource));
        ApplicationResource applicationResource = newApplicationResource().build();
        ProjectResource project = newProjectResource().withApplication(applicationResource).withAddress(addressResource).build();

        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(organisationResource);
        when(organisationService.getOrganisationById(organisationResource.getId())).thenReturn(organisationResource);
        when(organisationAddressRestService.findOne(project.getAddress().getOrganisations().get(0))).thenReturn(restSuccess(organisationAddressResource));

        MvcResult result = mockMvc.perform(get("/project/{id}/details/project-address", project.getId())).
                andExpect(status().isOk()).
                andExpect(view().name("project/details-address")).
                andExpect(model().hasNoErrors()).
                andReturn();

        Map<String, Object> model = result.getModelAndView().getModel();

        ProjectDetailsAddressViewModel viewModel = (ProjectDetailsAddressViewModel) model.get("model");
        assertEquals(project.getId(), viewModel.getProjectId());
        assertEquals(project.getName(), viewModel.getProjectName());
        assertEquals(project.getFormattedId(), viewModel.getProjectNumber());
        assertNull(viewModel.getOperatingAddress());
        assertEquals(addressResource, viewModel.getRegisteredAddress());
        assertNull(viewModel.getProjectAddress());

        ProjectDetailsAddressForm form = (ProjectDetailsAddressForm) model.get(ProjectDetailsController.FORM_ATTR_NAME);
        assertEquals(OrganisationAddressType.valueOf(organisationAddressResource.getAddressType().getName()), form.getAddressType());
    }

    @Test
    public void testUpdateProjectAddressToBeSameAsRegistered() throws Exception {
        OrganisationResource leadOrganisation = newOrganisationResource().build();
        AddressResource addressResource = newAddressResource().withOrganisationList(Collections.singletonList(leadOrganisation.getId())).build();
        AddressTypeResource addressTypeResource = newAddressTypeResource().withId((long)REGISTERED.getOrdinal()).withName(REGISTERED.name()).build();
        OrganisationAddressResource organisationAddressResource = newOrganisationAddressResource().withAddressType(addressTypeResource).withAddress(addressResource).build();
        leadOrganisation.setAddresses(Collections.singletonList(organisationAddressResource));
        CompetitionResource competitionResource = newCompetitionResource().build();
        ApplicationResource applicationResource = newApplicationResource().withCompetition(competitionResource.getId()).build();
        ProjectResource project = newProjectResource().withApplication(applicationResource).withAddress(addressResource).build();

        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(leadOrganisation);
        when(projectService.updateAddress(leadOrganisation.getId(), project.getId(), REGISTERED, addressResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{id}/details/project-address", project.getId()).
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                param("addressType", REGISTERED.name())).
                andExpect(status().is3xxRedirection()).
                andExpect(redirectedUrl("/project/" + project.getId() + "/details")).
                andReturn();
    }

    @Test
    public void testUpdateProjectAddressAddNewManually() throws Exception {
        OrganisationResource leadOrganisation = newOrganisationResource().build();
        AddressResource addressResource = newAddressResource().withPostcode("S1 2LB").withAddressLine1("Address Line 1").withTown("Sheffield").build();
        addressResource.setId(null);
        AddressTypeResource addressTypeResource = newAddressTypeResource().withId((long)REGISTERED.getOrdinal()).withName(REGISTERED.name()).build();
        OrganisationAddressResource organisationAddressResource = newOrganisationAddressResource().withAddressType(addressTypeResource).withAddress(addressResource).build();
        leadOrganisation.setAddresses(Collections.singletonList(organisationAddressResource));
        CompetitionResource competitionResource = newCompetitionResource().build();
        ApplicationResource applicationResource = newApplicationResource().withCompetition(competitionResource.getId()).build();
        ProjectResource project = newProjectResource().withApplication(applicationResource).withAddress(addressResource).build();

        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(leadOrganisation);
        when(projectService.updateAddress(leadOrganisation.getId(), project.getId(), PROJECT, addressResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{id}/details/project-address", project.getId()).
                contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("addressType", ADD_NEW.name())
                .param("manualEntry", "true")
                .param("addressForm.postcodeInput", "S101LB")
                .param("addressForm.selectedPostcode.addressLine1", addressResource.getAddressLine1())
                .param("addressForm.selectedPostcode.town", addressResource.getTown())
                .param("addressForm.selectedPostcode.postcode", addressResource.getPostcode()))
                .andExpect(redirectedUrl("/project/" + project.getId() + "/details")).
                andReturn();
    }

    @Test
    public void testSubmitProjectDetails() throws Exception {
        when(projectService.setApplicationDetailsSubmitted(1L)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{id}/details/submit", 1L)).
        andExpect(redirectedUrl("/project/1/details"));
    }
 }

