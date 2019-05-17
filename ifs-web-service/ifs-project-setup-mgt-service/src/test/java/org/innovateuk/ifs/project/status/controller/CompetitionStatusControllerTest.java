package org.innovateuk.ifs.project.status.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionOpenQueryResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.SpendProfileStatusResource;
import org.innovateuk.ifs.competition.service.CompetitionPostSubmissionRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.bankdetails.service.BankDetailsRestService;
import org.innovateuk.ifs.project.status.populator.CompetitionStatusViewModelPopulator;
import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.status.security.StatusPermission;
import org.innovateuk.ifs.project.status.viewmodel.CompetitionOpenQueriesViewModel;
import org.innovateuk.ifs.project.status.viewmodel.CompetitionPendingSpendProfilesViewModel;
import org.innovateuk.ifs.project.status.viewmodel.CompetitionStatusViewModel;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.web.servlet.MvcResult;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.any;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class CompetitionStatusControllerTest extends BaseControllerMockMVCTest<CompetitionStatusController> {

    @Mock
    private CompetitionPostSubmissionRestService competitionPostSubmissionRestService;
    @Mock
    private CompetitionRestService competitionRestServiceMock;
    @Mock
    private BankDetailsRestService bankDetailsRestServiceMock;
    @Mock
    private CompetitionStatusViewModelPopulator competitionStatusViewModelPopulatorMock;

    @Test
    public void testViewCompetitionStatusPage() throws Exception {
        Long competitionId = 123L;

        mockMvc.perform(get("/competition/" + competitionId + "/status"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/%s/status/all", competitionId)));
    }

    @Test
    public void testViewCompetitionStatusPageAllProjectFinance() throws Exception {
        Long competitionId = 123L;
        String applicationSearchString = "12";

        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.PROJECT_FINANCE)).build());

        CompetitionStatusViewModel competitionStatusViewModel = new CompetitionStatusViewModel(new CompetitionProjectsStatusResource(), TRUE, new HashMap<Long, StatusPermission>(), 1L, 4L);
        when(competitionStatusViewModelPopulatorMock.populate(Mockito.any(UserResource.class), anyLong(), anyString())).thenReturn(competitionStatusViewModel);

        MvcResult result = mockMvc.perform(get("/competition/" + competitionId + "/status/all?applicationSearchString=" + applicationSearchString))
                .andExpect(view().name("project/competition-status-all"))
                .andExpect(model().attribute("model", any(CompetitionStatusViewModel.class)))
                .andReturn();
        CompetitionStatusViewModel viewModel = (CompetitionStatusViewModel) result.getModelAndView().getModel().get("model");
        Assert.assertEquals(1L, viewModel.getOpenQueryCount());
        Assert.assertEquals(4L, viewModel.getPendingSpendProfilesCount());
        Assert.assertEquals(TRUE, viewModel.isShowTabs());
    }

    @Test
    public void testViewCompetitionStatusPageAllCompAdmin() throws Exception {
        long competitionId = 123L;
        String applicationSearchString = "12";

        CompetitionStatusViewModel competitionStatusViewModel = new CompetitionStatusViewModel(new CompetitionProjectsStatusResource(), TRUE, new HashMap<Long, StatusPermission>(), 0L, 0L);
        when(competitionStatusViewModelPopulatorMock.populate(Mockito.any(UserResource.class), anyLong(), anyString())).thenReturn(competitionStatusViewModel);

        MvcResult result = mockMvc.perform(get("/competition/" + competitionId + "/status/all?applicationSearchString=" + applicationSearchString))
                .andExpect(view().name("project/competition-status-all"))
                .andExpect(model().attribute("model", any(CompetitionStatusViewModel.class)))
                .andReturn();

        CompetitionStatusViewModel viewModel = (CompetitionStatusViewModel) result.getModelAndView().getModel().get("model");
        Assert.assertEquals(0L, viewModel.getOpenQueryCount());
        Assert.assertEquals(0L, viewModel.getPendingSpendProfilesCount());
        Assert.assertEquals(TRUE, viewModel.isShowTabs());
        verify(competitionPostSubmissionRestService, never()).getCompetitionOpenQueriesCount(competitionId);
        verify(competitionPostSubmissionRestService, never()).countPendingSpendProfiles(competitionId);
    }

    @Test
    public void testViewCompetitionStatusPageQueries() throws Exception {
        long competitionId = 123L;

        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.PROJECT_FINANCE)).build());

        CompetitionResource competition = newCompetitionResource().withName("comp1").withId(123L).build();

        List<CompetitionOpenQueryResource> openQueries = singletonList(new CompetitionOpenQueryResource(1L, 2L, "org", 3L, "proj"));

        when(competitionRestServiceMock.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        when(competitionPostSubmissionRestService.getCompetitionOpenQueriesCount(competitionId)).thenReturn(restSuccess(1L));
        when(competitionPostSubmissionRestService.getCompetitionOpenQueries(competitionId)).thenReturn(restSuccess(openQueries));
        when(competitionPostSubmissionRestService.countPendingSpendProfiles(competitionId)).thenReturn(restSuccess(4L));


        MvcResult result = mockMvc.perform(get("/competition/" + competitionId + "/status/queries"))
                .andExpect(view().name("project/competition-status-queries"))
                .andExpect(model().attribute("model", any(CompetitionOpenQueriesViewModel.class)))
                .andReturn();
        CompetitionOpenQueriesViewModel viewModel = (CompetitionOpenQueriesViewModel) result.getModelAndView().getModel().get("model");
        Assert.assertEquals(123L, viewModel.getCompetitionId());
        Assert.assertEquals("comp1", viewModel.getCompetitionName());
        Assert.assertEquals(1L, viewModel.getOpenQueryCount());
        Assert.assertEquals(1, viewModel.getOpenQueries().size());
        Assert.assertEquals(1L, viewModel.getOpenQueries().get(0).getApplicationId().longValue());
        Assert.assertEquals(2L, viewModel.getOpenQueries().get(0).getOrganisationId().longValue());
        Assert.assertEquals("org", viewModel.getOpenQueries().get(0).getOrganisationName());
        Assert.assertEquals(3L, viewModel.getOpenQueries().get(0).getProjectId().longValue());
        Assert.assertEquals("proj", viewModel.getOpenQueries().get(0).getProjectName());
        Assert.assertEquals(4L, viewModel.getPendingSpendProfilesCount());
        Assert.assertEquals(TRUE, viewModel.isShowTabs());
    }

    @Test
    public void testViewPendingSpendProfiles() throws Exception {
        long competitionId = 123L;

        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.PROJECT_FINANCE)).build());

        SpendProfileStatusResource pendingSpendProfile1 = new SpendProfileStatusResource(11L, 1L, "Project Name 1");
        SpendProfileStatusResource pendingSpendProfile2 = new SpendProfileStatusResource(11L, 2L, "Project Name 2");
        List<SpendProfileStatusResource> pendingSpendProfiles = asList(pendingSpendProfile1, pendingSpendProfile2);

        CompetitionResource competition = newCompetitionResource().withName("comp1").withId(123L).build();

        when(competitionPostSubmissionRestService.getCompetitionOpenQueriesCount(competitionId)).thenReturn(restSuccess(4L));
        when(competitionPostSubmissionRestService.getPendingSpendProfiles(competitionId)).thenReturn(restSuccess(pendingSpendProfiles));
        when(competitionRestServiceMock.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));

        MvcResult result = mockMvc.perform(get("/competition/" + competitionId + "/status/pending-spend-profiles"))
                .andExpect(view().name("project/competition-pending-spend-profiles"))
                .andExpect(model().attribute("model", any(CompetitionPendingSpendProfilesViewModel.class)))
                .andReturn();

        CompetitionPendingSpendProfilesViewModel viewModel = (CompetitionPendingSpendProfilesViewModel) result.getModelAndView().getModel().get("model");
        Assert.assertEquals(123L, viewModel.getCompetitionId());
        Assert.assertEquals("comp1", viewModel.getCompetitionName());
        Assert.assertEquals(pendingSpendProfiles, viewModel.getPendingSpendProfiles());
        Assert.assertEquals(4L, viewModel.getOpenQueryCount());
        Assert.assertEquals(2, viewModel.getPendingSpendProfilesCount());
        Assert.assertEquals(TRUE, viewModel.isShowTabs());
    }

    @Test
    public void exportBankDetails() throws Exception {

        Long competitionId = 123L;

        ByteArrayResource result = new ByteArrayResource("My content!".getBytes());

        when(bankDetailsRestServiceMock.downloadByCompetition(competitionId)).thenReturn(restSuccess(result));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");

        mockMvc.perform(get("/competition/123/status/bank-details/export"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(("text/csv")))
                .andExpect(header().string("Content-Type", "text/csv"))
                .andExpect(header().string("Content-disposition", "attachment;filename=" + String.format("Bank_details_%s_%s.csv", competitionId, ZonedDateTime.now().format(formatter))))
                .andExpect(content().string("My content!"));

        verify(bankDetailsRestServiceMock).downloadByCompetition(123L);
    }

    @Override
    protected CompetitionStatusController supplyControllerUnderTest() {
        return new CompetitionStatusController();
    }
}
