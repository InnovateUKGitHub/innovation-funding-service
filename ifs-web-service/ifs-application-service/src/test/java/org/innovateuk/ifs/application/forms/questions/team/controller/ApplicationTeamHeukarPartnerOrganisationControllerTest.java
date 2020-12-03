package org.innovateuk.ifs.application.forms.questions.team.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.forms.questions.team.form.HeukarPartnerOrganisationForm;
import org.innovateuk.ifs.application.forms.questions.team.populator.ApplicationTeamHeukarPartnerOrganisationPopulator;
import org.innovateuk.ifs.application.forms.questions.team.viewmodel.ApplicationTeamHeukarPartnerOrganisationViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.heukar.service.HeukarPartnerOrganisationRestService;
import org.innovateuk.ifs.organisation.resource.HeukarPartnerOrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ApplicationTeamHeukarPartnerOrganisationControllerTest extends BaseControllerMockMVCTest<ApplicationTeamHeukarPartnerOrganisationController> {

    @Mock
    private ApplicationTeamHeukarPartnerOrganisationPopulator populator;

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private HeukarPartnerOrganisationRestService heukarPartnerOrganisationRestService;

    @Override
    protected ApplicationTeamHeukarPartnerOrganisationController supplyControllerUnderTest() {
        return new ApplicationTeamHeukarPartnerOrganisationController();
    }

    @Test
    public void showAddNewPartnerOrganisationForm() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;
        ApplicationTeamHeukarPartnerOrganisationViewModel expected = mock(ApplicationTeamHeukarPartnerOrganisationViewModel.class);
        ApplicationResource application = mock(ApplicationResource.class);
        when(application.getId()).thenReturn(applicationId);

        when(applicationRestService.getApplicationById(applicationId)).thenReturn(RestResult.restSuccess(application));
        when(populator.populate(application, questionId)).thenReturn(expected);
        MvcResult result = mockMvc.perform(
                get("/application/{applicationId}/form/question/{questionId}/team/heukar-partner-org",
                        applicationId, questionId))
                .andExpect(status().isOk())
                .andExpect(view()
                        .name("application/questions/application-team-heukar-partner-organisation"))
                .andReturn();

        ApplicationTeamHeukarPartnerOrganisationViewModel actual =
                (ApplicationTeamHeukarPartnerOrganisationViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(expected, actual);
    }

    @Test
    public void showEditPartnerOrganisationForm() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;
        Long existingId = 1L;

        ApplicationTeamHeukarPartnerOrganisationViewModel expected = mock(ApplicationTeamHeukarPartnerOrganisationViewModel.class);

        ApplicationResource application = mock(ApplicationResource.class);
        when(application.getId()).thenReturn(applicationId);
        when(applicationRestService.getApplicationById(applicationId)).thenReturn(RestResult.restSuccess(application));
        when(populator.populate(application, questionId)).thenReturn(expected);

        HeukarPartnerOrganisationResource heukarPartnerOrganisationResource = mock(HeukarPartnerOrganisationResource.class);

        when(heukarPartnerOrganisationRestService.getExistingPartnerById(existingId))
                .thenReturn(RestResult.restSuccess(heukarPartnerOrganisationResource));
        OrganisationTypeResource organisationTypeResource = mock(OrganisationTypeResource.class);
        when(heukarPartnerOrganisationResource.getOrganisationTypeResource()).thenReturn(organisationTypeResource);
        when(organisationTypeResource.getId()).thenReturn(existingId);

        MvcResult result = mockMvc.perform(
                get("/application/{applicationId}/form/question/{questionId}/team/heukar-partner-org/{existingId}",
                        applicationId, questionId, existingId))
                .andExpect(status().isOk())
                .andExpect(view()
                        .name("application/questions/application-team-heukar-partner-organisation"))
                .andReturn();

        ApplicationTeamHeukarPartnerOrganisationViewModel actual =
                (ApplicationTeamHeukarPartnerOrganisationViewModel) result.getModelAndView().getModel().get("model");
        HeukarPartnerOrganisationForm form = (HeukarPartnerOrganisationForm) result.getModelAndView().getModel().get("form");
        assertEquals(expected, actual);
        assertEquals(existingId, form.getOrganisationTypeId());
    }

    @Test
    public void submitFormForAddition() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;
        Long existingId = 1L;

        ApplicationTeamHeukarPartnerOrganisationViewModel expected = mock(ApplicationTeamHeukarPartnerOrganisationViewModel.class);
        ApplicationResource application = mock(ApplicationResource.class);
        when(application.getId()).thenReturn(applicationId);

        when(applicationRestService.getApplicationById(applicationId)).thenReturn(RestResult.restSuccess(application));
        when(populator.populate(application, questionId)).thenReturn(expected);

        MvcResult result = mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}/team/heukar-partner-org?existingId=",
                        applicationId, questionId))
                .andExpect(status().isOk())
                .andExpect(view()
                        .name("application/questions/application-team-heukar-partner-organisation"))
                .andReturn();

        verify(heukarPartnerOrganisationRestService, times(1)).addNewHeukarOrgType(any(), any());
        verify(heukarPartnerOrganisationRestService, never()).updateHeukarOrgType(anyLong(), anyLong());
    }

}