package org.innovateuk.ifs.project.status.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionOpenQueryResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionPostSubmissionRestService;
import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.status.viewmodel.CompetitionOpenQueriesViewModel;
import org.innovateuk.ifs.project.status.viewmodel.CompetitionStatusViewModel;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.web.servlet.MvcResult;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;
import static org.hamcrest.Matchers.any;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.project.builder.CompetitionProjectsStatusResourceBuilder.newCompetitionProjectsStatusResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CompetitionStatusControllerTest extends BaseControllerMockMVCTest<CompetitionStatusController> {

    @Mock
    private CompetitionPostSubmissionRestService competitionPostSubmissionRestService;

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

        setLoggedInUser(newUserResource().withRolesGlobal(Arrays.asList(newRoleResource().withType(UserRoleType.PROJECT_FINANCE).build())).build());

        CompetitionProjectsStatusResource competitionProjectsStatus = newCompetitionProjectsStatusResource().build();

        when(statusRestService.getCompetitionStatus(competitionId)).thenReturn(restSuccess(competitionProjectsStatus));

        when(competitionPostSubmissionRestService.getCompetitionOpenQueriesCount(competitionId)).thenReturn(restSuccess(1L));

        MvcResult result = mockMvc.perform(get("/competition/" + competitionId + "/status/all"))
                .andExpect(view().name("project/competition-status-all"))
                .andExpect(model().attribute("model", any(CompetitionStatusViewModel.class)))
                .andReturn();
        CompetitionStatusViewModel viewModel = (CompetitionStatusViewModel) result.getModelAndView().getModel().get("model");
        Assert.assertEquals(true, viewModel.isShowTabs());
    }

    @Test
    public void testViewCompetitionStatusPageAllCompAdmin() throws Exception {
        Long competitionId = 123L;

        setLoggedInUser(newUserResource().withRolesGlobal(Arrays.asList(newRoleResource().withType(UserRoleType.COMP_ADMIN).build())).build());

        CompetitionProjectsStatusResource competitionProjectsStatus = newCompetitionProjectsStatusResource().build();

        when(statusRestService.getCompetitionStatus(competitionId)).thenReturn(restSuccess(competitionProjectsStatus));

        MvcResult result = mockMvc.perform(get("/competition/" + competitionId + "/status/all"))
                .andExpect(view().name("project/competition-status-all"))
                .andExpect(model().attribute("model", any(CompetitionStatusViewModel.class)))
                .andReturn();
        CompetitionStatusViewModel viewModel = (CompetitionStatusViewModel) result.getModelAndView().getModel().get("model");
        Assert.assertEquals(false, viewModel.isShowTabs());
        verify(competitionPostSubmissionRestService, never()).getCompetitionOpenQueriesCount(competitionId);
    }

    @Test
    public void testViewCompetitionStatusPageQueries() throws Exception {
        Long competitionId = 123L;

        setLoggedInUser(newUserResource().withRolesGlobal(Arrays.asList(newRoleResource().withType(UserRoleType.PROJECT_FINANCE).build())).build());

        CompetitionResource competition = newCompetitionResource().withName("comp1").withId(123L).build();

        List<CompetitionOpenQueryResource> openQueries = Arrays.asList(new CompetitionOpenQueryResource(1L, 2L, "org", 3L, "proj"));

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        when(competitionPostSubmissionRestService.getCompetitionOpenQueriesCount(competitionId)).thenReturn(restSuccess(1L));

        when(competitionPostSubmissionRestService.getCompetitionOpenQueries(competitionId)).thenReturn(restSuccess(openQueries));


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
        Assert.assertEquals(true, viewModel.isShowTabs());
    }

    @Test
    public void exportBankDetails() throws Exception {

        Long competitionId = 123L;

        ByteArrayResource result = new ByteArrayResource("My content!".getBytes());

        when(bankDetailsRestService.downloadByCompetition(competitionId)).thenReturn(restSuccess(result));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");

        mockMvc.perform(get("/competition/123/status/bank-details/export"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(("text/csv")))
                .andExpect(header().string("Content-Type", "text/csv"))
                .andExpect(header().string("Content-disposition", "attachment;filename=" + String.format("Bank_details_%s_%s.csv", competitionId, ZonedDateTime.now().format(formatter))))
                .andExpect(content().string("My content!"));

        verify(bankDetailsRestService).downloadByCompetition(123L);
    }

    @Override
    protected CompetitionStatusController supplyControllerUnderTest() {
        return new CompetitionStatusController();
    }
}
