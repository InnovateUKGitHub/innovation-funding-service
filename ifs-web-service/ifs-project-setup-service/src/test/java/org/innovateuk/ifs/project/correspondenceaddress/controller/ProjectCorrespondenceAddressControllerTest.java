package org.innovateuk.ifs.project.correspondenceaddress.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.lang.String.format;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlTemplate;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ProjectCorrespondenceAddressControllerTest extends BaseControllerMockMVCTest<ProjectCorrespondenceAddressController> {

    @Mock
    private ProjectService projectService;

    private OrganisationResource internationalOrganisation;
    private OrganisationResource UKOrganisation;
    private ProjectResource project;
    private long projectId;
    private String baseUrl;

    @Override
    protected ProjectCorrespondenceAddressController supplyControllerUnderTest() {
        return new ProjectCorrespondenceAddressController();
    }

    @Before
    public void setup() {
        projectId = 100L;
        baseUrl = format("/project/%d/details/project-address", projectId);

        internationalOrganisation = newOrganisationResource()
                .withId(543L)
                .withName("International Co")
                .withOrganisationType(1L)
                .withIsInternational(true)
                .build();

        UKOrganisation = newOrganisationResource()
                .withId(120L)
                .withName("UK Co")
                .withOrganisationType(1L)
                .withIsInternational(false)
                .build();

        project = newProjectResource()
                .withId(projectId)
                .build();
    }

    @Test
    public void viewPage_redirectToInternationalPage() throws Exception {

        when(projectService.getById(projectId)).thenReturn(project);
        when(projectService.getLeadOrganisation(projectId)).thenReturn(internationalOrganisation);

        mockMvc.perform(get(baseUrl))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlTemplate(baseUrl + "/international"));
    }

    @Test
    public void viewPage_redirectToUKPage() throws Exception {

        when(projectService.getById(projectId)).thenReturn(project);
        when(projectService.getLeadOrganisation(projectId)).thenReturn(UKOrganisation);

        mockMvc.perform(get(baseUrl))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlTemplate(baseUrl + "/UK"));
    }

}