package org.innovateuk.ifs.supporter.dashboard.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.supporter.dashboard.viewmodel.SupporterCompetitionDashboardViewModel;
import org.innovateuk.ifs.supporter.resource.SupporterDashboardApplicationPageResource;
import org.innovateuk.ifs.supporter.resource.SupporterDashboardApplicationResource;
import org.innovateuk.ifs.supporter.service.SupporterDashboardRestService;
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
public class SupporterCompetitionDashboardControllerTest extends BaseControllerMockMVCTest<SupporterCompetitionDashboardController> {

    @Mock
    private SupporterDashboardRestService supporterDashboardRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Override
    protected SupporterCompetitionDashboardController supplyControllerUnderTest() {
        return new SupporterCompetitionDashboardController();
    }

    @Test
    public void viewPage() throws Exception {
        int page = 3;
        List<SupporterDashboardApplicationResource> content = newArrayList(new SupporterDashboardApplicationResource());
        SupporterDashboardApplicationPageResource pageResource = new SupporterDashboardApplicationPageResource(
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

        when(supporterDashboardRestService.getSupporterCompetitionDashboardApplications(getLoggedInUser().getId(), competition.getId(), 2)).thenReturn(restSuccess(pageResource));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));


        MvcResult result = mockMvc.perform(get("/supporter/dashboard/competition/{competitionId}?page={page}", competition.getId(), page))
                .andExpect(status().isOk())
                .andExpect(view().name("supporter/supporter-competition-dashboard"))
                .andReturn();

        SupporterCompetitionDashboardViewModel viewModel = (SupporterCompetitionDashboardViewModel) result.getModelAndView().getModel().get("model");

        assertThat(viewModel.getApplications().size(), equalTo(1));
        assertThat(viewModel.getDeadline(), notNullValue());
        assertThat(viewModel.getCompetitionName(), equalTo(competition.getName()));
        assertThat(viewModel.getCompetitionId(), equalTo(competition.getId()));
        assertThat(viewModel.getPagination().getTotalElements(), equalTo(41L));
    }
}
