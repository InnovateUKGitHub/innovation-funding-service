package org.innovateuk.ifs.project.pendingpartner.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.pendingpartner.populator.ProjectTermsModelPopulator;
import org.innovateuk.ifs.project.pendingpartner.viewmodel.ProjectTermsViewModel;
import org.innovateuk.ifs.project.projectteam.PendingPartnerProgressRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectTermsControllerTest extends BaseControllerMockMVCTest<ProjectTermsController> {

    @Mock
    private ProjectTermsModelPopulator projectTermsModelPopulator;
    @Mock
    private PendingPartnerProgressRestService pendingPartnerProgressRestService;

    @Override
    protected ProjectTermsController supplyControllerUnderTest() {
        return new ProjectTermsController(projectTermsModelPopulator, pendingPartnerProgressRestService);
    }

    @Test
    public void getTerms() throws Exception {
        long projectId = 3L;
        long organisationId = 5L;
        String competitionTermsTemplate = "terms-template";
        boolean termsAccepted = false;

        ProjectTermsViewModel viewModel = new ProjectTermsViewModel(projectId, organisationId, competitionTermsTemplate, termsAccepted);

        when(projectTermsModelPopulator.populate(projectId, organisationId)).thenReturn(viewModel);

        mockMvc.perform(get("/project/{projectId}/organisation/{organisationId}/terms-and-conditions", projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", viewModel))
                .andExpect(view().name("project/pending-partner-progress/terms-and-conditions"));
    }
}