package org.innovateuk.ifs.project.pendingpartner.controller;

import static org.innovateuk.ifs.AsyncTestExpectationHelper.setupAsyncExpectations;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


import java.util.concurrent.Future;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableFormPopulator;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel.YourOrganisationViewModel;
import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.project.pendingpartner.populator.YourOrganisationViewModelPopulator;
import org.innovateuk.ifs.project.pendingpartner.viewmodel.ProjectYourOrganisationViewModel;
import org.innovateuk.ifs.project.projectteam.PendingPartnerProgressRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.Model;

public class ProjectYourOrganisationWithGrowthTableControllerTest extends BaseControllerMockMVCTest<ProjectYourOrganisationWithGrowthTableController> {

    private static final long projectId = 3L;
    private static final long organisationId = 5L;

    @Mock
    private UserResource userResource;

    @Mock
    private Model model;

    @Mock
    private YourOrganisationViewModelPopulator viewModelPopulator;

    @Mock
    private ProjectYourOrganisationViewModel yourOrganisationViewModel;

    @Mock
    private YourOrganisationWithGrowthTableFormPopulator withGrowthTableFormPopulator;

    @Mock
    private OrganisationFinancesWithGrowthTableResource organisationFinancesWithGrowthTableResource;

    @Mock
    private YourOrganisationWithGrowthTableForm yourOrganisationWithGrowthTableForm;

    @Mock
    private UserRestService userRestService;

    @Mock
    private ProjectYourOrganisationRestService yourOrganisationRestService;

    @Mock
    private PendingPartnerProgressRestService pendingPartnerProgressRestService;

    @Mock
    private AsyncFuturesGenerator asyncFuturesGenerator;

    @Override
    protected ProjectYourOrganisationWithGrowthTableController supplyControllerUnderTest() {
        return new ProjectYourOrganisationWithGrowthTableController();
    }

    @Before
    public void setupExpectations() {
        setupAsyncExpectations(asyncFuturesGenerator);
    }

    @Test
    public void viewPage() throws Exception {
        when(viewModelPopulator.populate(projectId, organisationId)).thenReturn(yourOrganisationViewModel);
        when(yourOrganisationRestService.getOrganisationFinancesWithGrowthTable(projectId, organisationId)).thenReturn(serviceSuccess(organisationFinancesWithGrowthTableResource));
        when(withGrowthTableFormPopulator.populate(organisationFinancesWithGrowthTableResource)).thenReturn(yourOrganisationWithGrowthTableForm);

        MvcResult result = mockMvc.perform(get("/project/{projectId}/organisation/{organisationId}/your-organisation" +
                "/with-growth" +
                "-table",
            projectId, organisationId))
            .andExpect(status().isOk())
            .andExpect(view().name("project/pending-partner-progress/your-organisation-with-growth-table"))
            .andReturn();

        Future<YourOrganisationViewModel> viewModelRequest = (Future<YourOrganisationViewModel>) result.getModelAndView().getModel().get("model");

        Future<YourOrganisationWithGrowthTableForm> formRequest = (Future<YourOrganisationWithGrowthTableForm>) result.getModelAndView().getModel().get("form");
    }
}
