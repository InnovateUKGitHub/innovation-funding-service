package org.innovateuk.ifs.project.projectdetails.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.form.AddressForm;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.projectdetails.form.ProjectDetailsAddressForm;
import org.innovateuk.ifs.project.projectdetails.viewmodel.ProjectDetailsAddressViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.projectdetails.ProjectDetailsService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.AddressLookupBaseController.FORM_ATTR_NAME;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ProjectDetailsAddressControllerTest extends BaseControllerMockMVCTest<ProjectDetailsAddressController> {

     @Mock
    private ProjectService projectService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private ProjectDetailsService projectDetailsService;


    @Override
    protected ProjectDetailsAddressController supplyControllerUnderTest() {
        return new ProjectDetailsAddressController();
    }

    @Test
    public void testViewAddress() throws Exception {
        OrganisationResource organisationResource = newOrganisationResource().build();
        AddressResource addressResource = newAddressResource().build();
        ApplicationResource applicationResource = newApplicationResource().build();
        ProjectResource project = newProjectResource().withApplication(applicationResource).withAddress(addressResource).build();

        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(organisationResource);
        when(organisationRestService.getOrganisationById(organisationResource.getId())).thenReturn(restSuccess(organisationResource));

        MvcResult result = mockMvc.perform(get("/project/{id}/details/project-address", project.getId())).
                andExpect(status().isOk()).
                andExpect(view().name("project/details-address")).
                andExpect(model().hasNoErrors()).
                andReturn();

        Map<String, Object> model = result.getModelAndView().getModel();

        ProjectDetailsAddressViewModel viewModel = (ProjectDetailsAddressViewModel) model.get("model");
        assertEquals(project.getId(), viewModel.getProjectId());
        assertEquals(project.getName(), viewModel.getProjectName());
        assertEquals(project.getApplication(), (long) viewModel.getApplicationId());

        ProjectDetailsAddressForm form = (ProjectDetailsAddressForm) model.get(FORM_ATTR_NAME);
        assertTrue(form.getAddressForm().isManualAddressEntry());
        assertEquals(form.getAddressForm().getManualAddress().getPostcode(), addressResource.getPostcode());
    }

    @Test
    public void testUpdateProjectAddressAddNewManually() throws Exception {
        OrganisationResource leadOrganisation = newOrganisationResource().build();

        AddressResource addressResource = newAddressResource().
                withId().
                withAddressLine1("Address Line 1").
                withAddressLine2().
                withAddressLine3().
                withTown("Sheffield").
                withCounty().
                withPostcode("S1 2LB").
                build();

        CompetitionResource competitionResource = newCompetitionResource().build();
        ApplicationResource applicationResource = newApplicationResource().withCompetition(competitionResource.getId()).build();
        ProjectResource project = newProjectResource().withApplication(applicationResource).withAddress(addressResource).build();

        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(leadOrganisation);
        when(projectDetailsService.updateAddress(leadOrganisation.getId(), project.getId(), addressResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{id}/details/project-address", project.getId()).
                contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("addressForm.addressType", AddressForm.AddressType.MANUAL_ENTRY.name())
                .param("addressForm.manualAddress.addressLine1", addressResource.getAddressLine1())
                .param("addressForm.manualAddress.town", addressResource.getTown())
                .param("addressForm.manualAddress.postcode", addressResource.getPostcode()))
                .andExpect(redirectedUrl("/project/" + project.getId() + "/details"))
                .andExpect(model().attributeDoesNotExist("readOnlyView"))
                .andReturn();
    }

    @Test
    public void testSearchAddressFailsWithFieldErrorOnEmpty() throws Exception {
        OrganisationResource leadOrganisation = newOrganisationResource().build();
        CompetitionResource competitionResource = newCompetitionResource().build();
        ApplicationResource applicationResource = newApplicationResource().withCompetition(competitionResource.getId()).build();
        ProjectResource project = newProjectResource().withApplication(applicationResource).build();

        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(leadOrganisation);

        mockMvc.perform(post("/project/{id}/details/project-address", project.getId()).
                contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("addressForm.action", AddressForm.Action.SEARCH_POSTCODE.name())
                .param("addressForm.postcodeInput", "")).
                andExpect(model().hasErrors()).
                andExpect(model().attributeDoesNotExist("readOnlyView")).
                andExpect(model().attributeHasFieldErrors("form", "addressForm.postcodeInput")).
                andReturn();
    }
}

