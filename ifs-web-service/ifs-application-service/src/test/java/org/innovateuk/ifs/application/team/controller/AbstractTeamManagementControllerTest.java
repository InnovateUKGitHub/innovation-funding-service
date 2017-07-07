package org.innovateuk.ifs.application.team.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.team.form.ApplicationTeamUpdateForm;
import org.innovateuk.ifs.application.team.service.AbstractTeamManagementService;
import org.innovateuk.ifs.application.team.viewmodel.ApplicationTeamManagementViewModel;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.InviteResultsResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AbstractTeamManagementControllerTest extends BaseControllerMockMVCTest<AbstractTeamManagementController> {

    private long testApplicationId = 1L;
    private long testOrganisationId = 2L;

    @Mock
    private TestTeamManagementService testTeamManagementService;

    protected AbstractTeamManagementController supplyControllerUnderTest() {
       return new TestTeamManagementController();
    }

    @Test
    public void getUpdateOrganisation_shouldReturnSuccessViewAndViewModelWhenOrganisationIsValid() throws Exception {
        when(testTeamManagementService.applicationAndOrganisationIdCombinationIsValid(same(testApplicationId), same(testOrganisationId))).thenReturn(true);
        when(testTeamManagementService.createViewModel(anyLong(), anyLong(), any())).thenReturn(createAViewModel());

        mockMvc.perform(get("/application/{applicationId}/team/update/invited/{organisationId}", testApplicationId, testOrganisationId))
                .andExpect(status().isOk())
                .andExpect(view().name("application-team/edit-org"))
                .andExpect(model().attribute("model", createAViewModel())).andReturn();
    }

    @Test
    public void getUpdateOrganisation_shouldReturnNotFoundWhenOrganisationIsInvalid() throws Exception {
        when(testTeamManagementService.applicationAndOrganisationIdCombinationIsValid(same(testApplicationId), same(testOrganisationId))).thenReturn(false);

        mockMvc.perform(get("/application/{applicationId}/team/update/invited/{organisationId}", testApplicationId, testOrganisationId))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("404"));

        verify(testTeamManagementService, never()).createViewModel(anyLong(), anyLong(), any());
    }

    @Test
    public void addStagedInvite_shouldReturnSuccessViewWithStagedInviteWhenOrganisationIsValid() throws Exception {
        when(testTeamManagementService.applicationAndOrganisationIdCombinationIsValid(same(testApplicationId), same(testOrganisationId))).thenReturn(true);
        when(testTeamManagementService.createViewModel(anyLong(), anyLong(), any())).thenReturn(createAViewModel());

        MvcResult result = mockMvc.perform(post("/application/{applicationId}/team/update/invited/{organisationId}", testApplicationId, testOrganisationId)
        .param("addStagedInvite", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("application-team/edit-org"))
                .andExpect(model().attribute("model", createAViewModel())).andReturn();

        ApplicationTeamUpdateForm model = (ApplicationTeamUpdateForm)result.getModelAndView().getModel().get("form");

        assertNotNull(model.getStagedInvite());
    }

    @Test
    public void addStagedInvite_shouldReturnNotFoundWhenOrganisationIsInvalid() throws Exception {
        when(testTeamManagementService.applicationAndOrganisationIdCombinationIsValid(same(testApplicationId), same(testOrganisationId))).thenReturn(false);

        mockMvc.perform(post("/application/{applicationId}/team/update/invited/{organisationId}", testApplicationId, testOrganisationId)
        .param("addStagedInvite", "true"))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("404"));

        verify(testTeamManagementService, never()).createViewModel(anyLong(), anyLong(), any());
    }

    @Test
    public void removeStagedInvite_shouldReturnSuccessViewAndRemoveStagedInviteWhenOrganisationIsValid() throws Exception {
        when(testTeamManagementService.applicationAndOrganisationIdCombinationIsValid(same(testApplicationId), same(testOrganisationId))).thenReturn(true);
        when(testTeamManagementService.createViewModel(anyLong(), anyLong(), any())).thenReturn(createAViewModel());

        MvcResult result = mockMvc.perform(post("/application/{applicationId}/team/update/invited/{organisationId}", testApplicationId, testOrganisationId)
                .param("removeStagedInvite", "true")
                .param("stagedInvite.email", "an email")
                .param("stagedInvite.name", "a name"))
                .andExpect(status().isOk())
                .andExpect(view().name("application-team/edit-org"))
                .andExpect(model().attribute("model", createAViewModel())).andReturn();

        ApplicationTeamUpdateForm model = (ApplicationTeamUpdateForm) result.getModelAndView().getModel().get("form");

        assertNull(model.getStagedInvite());
    }

    @Test
    public void removeStagedInvite_shouldReturnNotFoundWhenOrganisationIsInvalid() throws Exception {
        when(testTeamManagementService.applicationAndOrganisationIdCombinationIsValid(same(testApplicationId), same(testOrganisationId))).thenReturn(false);

        mockMvc.perform(post("/application/{applicationId}/team/update/invited/{organisationId}", testApplicationId, testOrganisationId)
                .param("removeStagedInvite", "true"))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("404"));

        verify(testTeamManagementService, never()).createViewModel(anyLong(), anyLong(), any());
    }

    @Test
    public void inviteApplicant_shouldReturnSuccessViewWhenOrganisationIsValid() throws Exception {
        when(testTeamManagementService.applicationAndOrganisationIdCombinationIsValid(same(testApplicationId), same(testOrganisationId))).thenReturn(true);
        when(testTeamManagementService.createViewModel(anyLong(), anyLong(), any())).thenReturn(createAViewModel());
        when(testTeamManagementService.executeStagedInvite(anyLong(), anyLong(), any())).thenReturn(serviceSuccess(new InviteResultsResource()));

        mockMvc.perform(post("/application/{applicationId}/team/update/invited/{organisationId}", testApplicationId, testOrganisationId)
                .param("executeStagedInvite", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("application-team/edit-org"))
                .andExpect(model().attribute("model", createAViewModel())).andReturn();
    }

    @Test
    public void inviteApplicant_shouldInviteApplicantWhenTheFormIsValidAndTheOrganisationIsValid() throws Exception {
        String validName = "valid name";
        String validEmail = "valid@email.com";

        when(testTeamManagementService.applicationAndOrganisationIdCombinationIsValid(same(testApplicationId), same(testOrganisationId))).thenReturn(true);
        when(testTeamManagementService.createViewModel(anyLong(), anyLong(), any())).thenReturn(createAViewModel());
        when(testTeamManagementService.executeStagedInvite(anyLong(), anyLong(), any())).thenReturn(serviceSuccess(new InviteResultsResource()));

        mockMvc.perform(post("/application/{applicationId}/team/update/invited/{organisationId}", testApplicationId, testOrganisationId)
                .param("executeStagedInvite", "true")
                .param("stagedInvite.name", validName)
                .param("stagedInvite.email", validEmail))
                .andExpect(status().isOk())
                .andExpect(view().name("application-team/edit-org"))
                .andExpect(model().attribute("model", createAViewModel())).andReturn();

        //TODO: verify invite contents
        verify(testTeamManagementService, times(1)).executeStagedInvite(anyLong(), anyLong(), any());
    }

    @Test
    public void inviteApplicant_shouldNotInviteApplicantWhenTheFormIsInvalidAndTheOrganisationIsValid() throws Exception {
        String invalidName = "";
        String invalidEmail = "invalidemail";

        when(testTeamManagementService.applicationAndOrganisationIdCombinationIsValid(same(testApplicationId), same(testOrganisationId))).thenReturn(true);
        when(testTeamManagementService.createViewModel(anyLong(), anyLong(), any())).thenReturn(createAViewModel());
        when(testTeamManagementService.executeStagedInvite(anyLong(), anyLong(), any())).thenReturn(serviceSuccess(new InviteResultsResource()));

        mockMvc.perform(post("/application/{applicationId}/team/update/invited/{organisationId}", testApplicationId, testOrganisationId)
                .param("executeStagedInvite", "true")
                .param("stagedInvite.name", invalidName)
                .param("stagedInvite.name", invalidEmail))
                .andExpect(status().isOk())
                .andExpect(view().name("application-team/edit-org"))
                .andExpect(model().attribute("model", createAViewModel())).andReturn();

        verify(testTeamManagementService, never()).executeStagedInvite(anyLong(), anyLong(), any());

    }

    @Test
    public void inviteApplicant_shouldReturnNotFoundWhenOrganisationIsInvalid() throws Exception {
        when(testTeamManagementService.applicationAndOrganisationIdCombinationIsValid(same(testApplicationId), same(testOrganisationId))).thenReturn(false);

        mockMvc.perform(post("/application/{applicationId}/team/update/invited/{organisationId}", testApplicationId, testOrganisationId)
                .param("executeStagedInvite", "true"))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("404"));

        verify(testTeamManagementService, never()).createViewModel(anyLong(), anyLong(), any());
    }

    @Test
    public void removeApplicant_shouldReturnSuccessViewWhenOrganisationIsValid() throws Exception {
        when(testTeamManagementService.applicationAndOrganisationIdCombinationIsValid(same(testApplicationId), same(testOrganisationId))).thenReturn(true);
        when(testTeamManagementService.createViewModel(anyLong(), anyLong(), any())).thenReturn(createAViewModel());
        when(testTeamManagementService.removeInvite(3L)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/application/{applicationId}/team/update/invited/{organisationId}", testApplicationId, testOrganisationId)
                .param("removeInvite", "3"))
                .andExpect(status().isOk())
                .andExpect(view().name("application-team/edit-org"))
                .andExpect(model().attribute("model", createAViewModel())).andReturn();
    }

    @Test
    public void removeApplicant_shouldReturnNotFoundWhenOrganisationIsInvalid() throws Exception {
        when(testTeamManagementService.applicationAndOrganisationIdCombinationIsValid(same(testApplicationId), same(testOrganisationId))).thenReturn(false);

        mockMvc.perform(post("/application/{applicationId}/team/update/invited/{organisationId}", testApplicationId, testOrganisationId)
                .param("removeInvite", "3"))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("404"));

        verify(testTeamManagementService, never()).createViewModel(anyLong(), anyLong(), any());
    }

    @Test
    public void confirmDeleteInviteOrganisation_shouldReturnSuccessViewWhenOrganisationIsValid() throws Exception {
        when(testTeamManagementService.applicationAndOrganisationIdCombinationIsValid(same(testApplicationId), same(testOrganisationId))).thenReturn(true);
        when(testTeamManagementService.createViewModel(anyLong(), anyLong(), any())).thenReturn(createAViewModel());

        mockMvc.perform(get("/application/{applicationId}/team/update/invited/{organisationId}", testApplicationId, testOrganisationId)
                .param("confirmDeleteOrganisation", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("application-team/edit-org"))
                .andExpect(model().attribute("model", createAViewModel()));
    }

    @Test
    public void confirmDeleteInviteOrganisation_shouldReturnNotFoundWhenOrganisationIsInvalid() throws Exception {
        when(testTeamManagementService.applicationAndOrganisationIdCombinationIsValid(same(testApplicationId), same(testOrganisationId))).thenReturn(false);

        mockMvc.perform(get("/application/{applicationId}/team/update/invited/{organisationId}", testApplicationId, testOrganisationId)
                .param("confirmDeleteOrganisation", "true"))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("404"));

        verify(testTeamManagementService, never()).createViewModel(anyLong(), anyLong(), any());
    }

    @Test
    public void deleteOrganisation_shouldReturnSuccessViewWhenOrganisationIsValid() throws Exception {
        when(testTeamManagementService.applicationAndOrganisationIdCombinationIsValid(same(testApplicationId), same(testOrganisationId))).thenReturn(true);
        when(testTeamManagementService.createViewModel(anyLong(), anyLong(), any())).thenReturn(createAViewModel());
        when(testTeamManagementService.getInviteIds(same(testApplicationId), same(testOrganisationId))).thenReturn(Arrays.asList(1L, 2L, 3L));
        when(testTeamManagementService.removeInvite(anyLong())).thenReturn(serviceSuccess());

        mockMvc.perform(post("/application/{applicationId}/team/update/invited/{organisationId}", testApplicationId, testOrganisationId)
                .param("deleteOrganisation", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(format("redirect:/application/%s/team", testApplicationId)));
    }

    @Test
    public void deleteOrganisation_shouldReturnNotFoundWhenOrganisationIsInvalid() throws Exception {
        when(testTeamManagementService.applicationAndOrganisationIdCombinationIsValid(same(testApplicationId), same(testOrganisationId))).thenReturn(false);

        mockMvc.perform(post("/application/{applicationId}/team/update/invited/{organisationId}", testApplicationId, testOrganisationId)
                .param("deleteOrganisation", "true"))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("404"));

        verify(testTeamManagementService, never()).createViewModel(anyLong(), anyLong(), any());
    }

    @Service
    public class TestTeamManagementService extends AbstractTeamManagementService {
        @Override
        public boolean applicationAndOrganisationIdCombinationIsValid(Long applicationId, Long organisationId) {
            return false;
        }

        @Override
        public ApplicationTeamManagementViewModel createViewModel(long applicationId, long organisationId, UserResource loggedInUser) {
            return null;
        }

        @Override
        public ServiceResult<InviteResultsResource> executeStagedInvite(long applicationId, long organisationId, ApplicationTeamUpdateForm form) {
            return null;
        }

        @Override
        public List<Long> getInviteIds(long applicationId, long organisationId) {
            return null;
        }
    }

    @RequestMapping("/application/{applicationId}/team/update/invited/{organisationId}")
    public class TestTeamManagementController extends AbstractTeamManagementController<TestTeamManagementService> {

    }

    private static ApplicationTeamManagementViewModel createAViewModel() {
        return new ApplicationTeamManagementViewModel(1L,"application name", 2L, 3L, "organisation name", true, true, emptyList(), true);
    }
}