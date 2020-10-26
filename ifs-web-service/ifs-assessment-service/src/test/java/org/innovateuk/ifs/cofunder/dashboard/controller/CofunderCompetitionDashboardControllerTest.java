package org.innovateuk.ifs.cofunder.dashboard.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.cofunder.dashboard.viewmodel.CofunderCompetitionDashboardViewModel;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardApplicationPageResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardApplicationResource;
import org.innovateuk.ifs.cofunder.service.CofunderDashboardRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MvcResult;

import java.time.ZonedDateTime;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.class)
public class CofunderCompetitionDashboardControllerTest extends BaseControllerMockMVCTest<CofunderCompetitionDashboardController> {

    @Mock
    private CofunderDashboardRestService cofunderDashboardRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Override
    protected CofunderCompetitionDashboardController supplyControllerUnderTest() {
        return new CofunderCompetitionDashboardController();
    }

    @Test
    public void viewPage() throws Exception {
        int page = 3;
        List<CofunderDashboardApplicationResource> content = newArrayList(new CofunderDashboardApplicationResource());
        CofunderDashboardApplicationPageResource pageResource = new CofunderDashboardApplicationPageResource(
                41L,
                3,
                content,
                2,
                20
        );
        CompetitionResource competition = newCompetitionResource()
                .withAssessorDeadlineDate(ZonedDateTime.now())
                .withCompetitionStatus(CompetitionStatus.IN_ASSESSMENT)
                .build();

        when(cofunderDashboardRestService.getCofunderCompetitionDashboardApplications(getLoggedInUser().getId(), competition.getId(), 2)).thenReturn(restSuccess(pageResource));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));


        MvcResult result = mockMvc.perform(get("/cofunder/dashboard/competition/{competitionId}?page={page}", competition.getId(), page))
                .andExpect(status().isOk())
                .andExpect(view().name("cofunder/cofunder-competition-dashboard"))
                .andReturn();

        CofunderCompetitionDashboardViewModel viewModel = (CofunderCompetitionDashboardViewModel) result.getModelAndView().getModel().get("model");

        assertThat(viewModel.getApplications().size(), equalTo(1));
        assertThat(viewModel.getDeadline(), notNullValue());
        assertThat(viewModel.getCompetitionName(), equalTo(competition.getName()));
        assertThat(viewModel.getCompetitionId(), equalTo(competition.getId()));
        assertThat(viewModel.isAssessmentClosed(), equalTo(false));
        assertThat(viewModel.getPagination().getTotalElements(), equalTo(41L));
    }
}
