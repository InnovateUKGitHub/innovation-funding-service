package org.innovateuk.ifs.application.team.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.team.service.InviteOrganisationTeamManagementService;
import org.innovateuk.ifs.application.team.viewmodel.ApplicationTeamManagementViewModel;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Collections.emptyList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class InviteOrganisationTeamManagementControllerTest extends BaseControllerMockMVCTest<InviteOrganisationTeamManagementController> {
    @Mock
    private InviteOrganisationTeamManagementService inviteOrganisationTeamManagementService;

    protected InviteOrganisationTeamManagementController supplyControllerUnderTest() {
        return new InviteOrganisationTeamManagementController();
    }

    @Test
    public void callingMainViewWillInvokeAppropriateService() throws Exception {
        when(inviteOrganisationTeamManagementService.applicationAndOrganisationIdCombinationIsValid(any(), any())).thenReturn(true);
        when(inviteOrganisationTeamManagementService.createViewModel(anyLong(), anyLong(), any())).thenReturn(createAViewModel());

        mockMvc.perform(get("/application/{applicationId}/team/update/invited/{organisationId}", 1L, 2L))
                .andExpect(status().isOk())
                .andExpect(view().name("application-team/edit-org"))
                .andExpect(model().attribute("model", createAViewModel())).andReturn();

        verify(inviteOrganisationTeamManagementService, times(1)).createViewModel(anyLong(),anyLong(),any());
        verify(inviteOrganisationTeamManagementService, times(1)).applicationAndOrganisationIdCombinationIsValid(any(),any());
    }

    private static ApplicationTeamManagementViewModel createAViewModel() {
        return new ApplicationTeamManagementViewModel(1L,"application name", 2L, 3L, "organisation name", true, true, emptyList(), true);
    }
}