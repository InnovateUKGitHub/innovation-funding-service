package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.populator.CompetitionOverviewPopulator;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.viewmodel.CompetitionOverviewViewModel;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentItemResourceBuilder.newPublicContentItemResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CompetitionControllerTest extends BaseControllerMockMVCTest<CompetitionController> {

    @Override
    protected CompetitionController supplyControllerUnderTest() {
        return new CompetitionController();
    }

    @Mock
    private CompetitionOverviewPopulator overviewPopulator;

    @Test
    public void competitionOverview() throws Exception {
        final long competitionId = 20L;

        final PublicContentItemResource publicContentItem = newPublicContentItemResource().build();
        final CompetitionOverviewViewModel viewModel = new CompetitionOverviewViewModel();

        when(competitionService.getPublicContentOfCompetition(competitionId)).thenReturn(publicContentItem);
        when(overviewPopulator.populateViewModel(publicContentItem, true)).thenReturn(viewModel);

        mockMvc.perform(get("/competition/{id}/overview", competitionId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", viewModel))
                .andExpect(view().name("competition/overview"));
    }

    @Test
    public void competitionOverview_notLoggedIn() throws Exception {
        final long competitionId = 20L;
        setLoggedInUser(null);

        final PublicContentItemResource publicContentItem = newPublicContentItemResource().build();
        final CompetitionOverviewViewModel viewModel = new CompetitionOverviewViewModel();

        when(competitionService.getPublicContentOfCompetition(competitionId)).thenReturn(publicContentItem);
        when(overviewPopulator.populateViewModel(publicContentItem, false)).thenReturn(viewModel);

        mockMvc.perform(get("/competition/{id}/overview", competitionId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", viewModel))
                .andExpect(view().name("competition/overview"));
    }

    @Test
    public void beforeYouApply() throws Exception {
        final CompetitionResource competitionResource = newCompetitionResource().build();
        when(competitionService.getPublishedById(competitionResource.getId())).thenReturn(competitionResource);

        mockMvc.perform(get("/competition/{id}/info/before-you-apply", competitionResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentCompetition", competitionResource))
                .andExpect(view().name("competition/info/before-you-apply"));
    }

    @Test
    public void eligibility() throws Exception {
        final CompetitionResource competitionResource = newCompetitionResource().build();
        when(competitionService.getPublishedById(competitionResource.getId())).thenReturn(competitionResource);

        mockMvc.perform(get("/competition/{id}/info/eligibility", competitionResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentCompetition", competitionResource))
                .andExpect(view().name("competition/info/eligibility"));
    }

    @Test
    public void termsAndConditions() throws Exception {
        final CompetitionResource competitionResource = newCompetitionResource().build();
        when(competitionService.getPublishedById(competitionResource.getId())).thenReturn(competitionResource);

        mockMvc.perform(get("/competition/{id}/info/terms-and-conditions", competitionResource.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/info/terms-and-conditions"));
    }

    @Test
    public void whatWeAskYou() throws Exception {
        final CompetitionResource competitionResource = newCompetitionResource().build();
        when(competitionService.getPublishedById(competitionResource.getId())).thenReturn(competitionResource);

        mockMvc.perform(get("/competition/{id}/info/what-we-ask-you", competitionResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentCompetition", competitionResource))
                .andExpect(view().name("competition/info/what-we-ask-you"));
    }
}