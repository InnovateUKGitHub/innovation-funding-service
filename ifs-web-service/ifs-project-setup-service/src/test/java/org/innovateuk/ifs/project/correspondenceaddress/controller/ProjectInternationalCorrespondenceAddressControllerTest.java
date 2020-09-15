package org.innovateuk.ifs.project.correspondenceaddress.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.correspondenceaddress.form.ProjectInternationalCorrespondenceAddressForm;
import org.innovateuk.ifs.project.correspondenceaddress.viewmodel.ProjectInternationalCorrespondenceAddressViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.projectdetails.ProjectDetailsService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static java.lang.String.format;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ProjectInternationalCorrespondenceAddressControllerTest extends BaseControllerMockMVCTest<ProjectInternationalCorrespondenceAddressController> {

    @Mock
    private ProjectService projectService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private ProjectDetailsService projectDetailsService;

    private AddressResource addressResource;
    private OrganisationResource internationalOrganisation;
    private ProjectResource project;
    private long projectId;
    private String url;

    @Override
    protected ProjectInternationalCorrespondenceAddressController supplyControllerUnderTest() {
        return new ProjectInternationalCorrespondenceAddressController();
    }

    @Before
    public void setup() {
        projectId = 150L;
        url = format("/project/%d/details/project-address/international", projectId);

        CompetitionResource competitionResource = newCompetitionResource().build();
        addressResource = newAddressResource()
                .withAddressLine1("1 Skyline Street")
                .withAddressLine2()
                .withTown("Queensland")
                .withPostcode("4610")
                .withCountry("Australia")
                .build();
        internationalOrganisation = newOrganisationResource()
                .withId(543L)
                .withName("International Co")
                .withOrganisationType(1L)
                .withIsInternational(true)
                .build();
        ApplicationResource applicationResource = newApplicationResource().withCompetition(competitionResource.getId()).build();
        project = newProjectResource()
                .withId(projectId)
                .withAddress(addressResource)
                .withApplication(applicationResource)
                .build();
    }

    @Test
    public void viewAddress() throws Exception {

        when(projectService.getById(projectId)).thenReturn(project);

        MvcResult result = mockMvc.perform(get(url, project.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("project/international-address"))
                .andExpect(model().hasNoErrors())
                .andReturn();

        Map<String, Object> model = result.getModelAndView().getModel();


        ProjectInternationalCorrespondenceAddressViewModel viewModel = (ProjectInternationalCorrespondenceAddressViewModel) model.get("model");
        assertEquals(project.getId(), viewModel.getProjectId());
        assertEquals(project.getName(), viewModel.getProjectName());

        ProjectInternationalCorrespondenceAddressForm form = (ProjectInternationalCorrespondenceAddressForm) model.get("form");
        assertEquals(project.getAddress().getCountry(), form.getCountry());
        assertEquals(project.getAddress().getAddressLine1(), form.getAddressLine1());
    }

    @Test
    public void updateAddress() throws Exception {

        when(projectService.getById(projectId)).thenReturn(project);
        when(projectDetailsService.updateAddress(eq(projectId), isA(AddressResource.class))).thenReturn(serviceSuccess());

        mockMvc.perform(post(url, project.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("addressLine1", addressResource.getAddressLine1())
                .param("addressLine2", addressResource.getAddressLine2())
                .param("town", addressResource.getTown())
                .param("country", addressResource.getCountry())
                .param("zipCode", addressResource.getPostcode()))
                .andExpect(redirectedUrl("/project/" + projectId + "/details"))
                .andReturn();
    }
}