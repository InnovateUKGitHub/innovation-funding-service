package org.innovateuk.ifs.application.team.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.team.service.OrganisationTeamManagementService;
import org.innovateuk.ifs.application.team.viewmodel.ApplicationTeamManagementViewModel;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Collections.emptyList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class OrganisationTeamManagementControllerTest extends BaseControllerMockMVCTest<OrganisationTeamManagementController> {
    @Mock
    private OrganisationTeamManagementService organisationTeamManagementService;

    protected OrganisationTeamManagementController supplyControllerUnderTest() {
        return new OrganisationTeamManagementController();
    }

    @Test
    public void callingMainViewWillInvokeAppropriateService() throws Exception {
        when(organisationTeamManagementService.applicationAndOrganisationIdCombinationIsValid(any(), any())).thenReturn(true);
        when(organisationTeamManagementService.createViewModel(anyLong(), anyLong(), any())).thenReturn(createAViewModel());

        mockMvc.perform(get("/application/{applicationId}/team/update/existing/{organisationId}", 1L, 2L))
                .andExpect(status().isOk())
                .andExpect(view().name("application-team/edit-org"))
                .andExpect(model().attribute("model", createAViewModel())).andReturn();

        verify(organisationTeamManagementService, times(1)).createViewModel(anyLong(),anyLong(),any());
        verify(organisationTeamManagementService, times(1)).applicationAndOrganisationIdCombinationIsValid(any(),any());
    }

    private static ApplicationTeamManagementViewModel createAViewModel() {
        return new ApplicationTeamManagementViewModel(1L,"application name", 2L, 3L, "organisation name", true, true, emptyList(), true);
    }


}