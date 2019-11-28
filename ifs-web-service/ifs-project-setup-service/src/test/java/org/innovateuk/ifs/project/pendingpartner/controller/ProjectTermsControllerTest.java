package org.innovateuk.ifs.project.pendingpartner.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.pendingpartner.form.ProjectTermsForm;
import org.innovateuk.ifs.project.pendingpartner.populator.ProjectTermsModelPopulator;
import org.innovateuk.ifs.project.pendingpartner.viewmodel.ProjectTermsViewModel;
import org.innovateuk.ifs.project.projectteam.PendingPartnerProgressRestService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectTermsControllerTest extends BaseControllerMockMVCTest<ProjectTermsController> {

    private ProjectResource project;
    private OrganisationResource organisation;
    private boolean termsAccepted;
    private String competitionTermsTemplate;

    @Mock
    private ProjectTermsModelPopulator projectTermsModelPopulator;
    @Mock
    private PendingPartnerProgressRestService pendingPartnerProgressRestService;

    @Override
    protected ProjectTermsController supplyControllerUnderTest() {
        return new ProjectTermsController();
    }

    @Before
    public void setUp() {
        project = newProjectResource().withId(3L).build();
        organisation = newOrganisationResource().withId(5L).build();
        competitionTermsTemplate = "terms-template";
        termsAccepted = false;
    }

    @Test
    public void getTerms() throws Exception {
        ProjectTermsViewModel viewModel = new ProjectTermsViewModel(project.getId(), organisation.getId(), competitionTermsTemplate, termsAccepted, null);

        when(projectTermsModelPopulator.populate(project.getId(), organisation.getId())).thenReturn(viewModel);

        mockMvc.perform(get("/project/{projectId}/organisation/{organisationId}/terms-and-conditions", project.getId(), organisation.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", viewModel))
                .andExpect(view().name("project/pending-partner-progress/terms-and-conditions"));
    }

    @Test
    public void acceptTerms() throws Exception {
        when(pendingPartnerProgressRestService.markTermsAndConditionsComplete(project.getId(), organisation.getId())).thenReturn(restSuccess());

        ProjectTermsForm form = new ProjectTermsForm();

        mockMvc.perform(post("/project/{projectId}/organisation/{organisationId}/terms-and-conditions", project.getId(), organisation.getId())
                .param("agreed", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attribute("form", form))
                .andExpect(model().hasNoErrors())
                .andExpect(redirectedUrlTemplate("/project/{projectId}/organisation/{organisationId}/terms-and-conditions#terms-accepted", project.getId(), organisation.getId()));

        verify(pendingPartnerProgressRestService).markTermsAndConditionsComplete(project.getId(), organisation.getId());
        verifyNoMoreInteractions(pendingPartnerProgressRestService);
    }

    @Test
    public void acceptTerms_notAgreed() throws Exception {
        when(pendingPartnerProgressRestService.markTermsAndConditionsComplete(project.getId(), organisation.getId())).thenReturn(restFailure(fieldError("agreed", "false", "")));

        ProjectTermsForm form = new ProjectTermsForm();

        mockMvc.perform(post("/project/{projectId}/organisation/{organisationId}/terms-and-conditions", project.getId(), organisation.getId())
                .param("agreed", "false"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", form))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "agreed"))
                .andExpect(view().name("project/pending-partner-progress/terms-and-conditions"));

        verify(pendingPartnerProgressRestService).markTermsAndConditionsComplete(project.getId(), organisation.getId());
        verifyNoMoreInteractions(pendingPartnerProgressRestService);
    }
}