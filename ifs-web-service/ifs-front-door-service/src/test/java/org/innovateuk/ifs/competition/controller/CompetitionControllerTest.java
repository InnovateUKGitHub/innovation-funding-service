package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.populator.CompetitionOverviewPopulator;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.viewmodel.CompetitionOverviewViewModel;
import org.innovateuk.ifs.competition.viewmodel.CompetitionTermsViewModel;
import org.innovateuk.ifs.publiccontent.service.PublicContentItemRestService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentItemResourceBuilder.newPublicContentItemResource;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CompetitionControllerTest extends BaseControllerMockMVCTest<CompetitionController> {

    @Override
    protected CompetitionController supplyControllerUnderTest() {
        return new CompetitionController();
    }

    @Mock
    private CompetitionOverviewPopulator overviewPopulator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private PublicContentItemRestService publicContentItemRestService;

    @Test
    public void competitionOverview() throws Exception {
        final long competitionId = 20L;

        final PublicContentItemResource publicContentItem = newPublicContentItemResource().build();
        final CompetitionOverviewViewModel viewModel = new CompetitionOverviewViewModel();

        when(publicContentItemRestService.getItemByCompetitionId(competitionId)).thenReturn(restSuccess(publicContentItem));
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

        when(publicContentItemRestService.getItemByCompetitionId(competitionId)).thenReturn(restSuccess(publicContentItem));
        when(overviewPopulator.populateViewModel(publicContentItem, false)).thenReturn(viewModel);

        mockMvc.perform(get("/competition/{id}/overview", competitionId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", viewModel))
                .andExpect(view().name("competition/overview"));
    }

    @Test
    public void termsAndConditions() throws Exception {
        GrantTermsAndConditionsResource termsAndConditions = new GrantTermsAndConditionsResource("T&C",
                "special-terms-and-conditions", 3);

        final CompetitionResource competitionResource = newCompetitionResource()
                .withCompetitionTypeName("Competition name")
                .withTermsAndConditions(termsAndConditions)
                .build();

        when(competitionRestService.getCompetitionById(competitionResource.getId())).thenReturn(restSuccess(competitionResource));

        mockMvc.perform(get("/competition/{id}/info/terms-and-conditions", competitionResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", new CompetitionTermsViewModel(competitionResource.getId())))
                .andExpect(view().name("competition/info/special-terms-and-conditions"));

        verify(competitionRestService, only()).getCompetitionById(competitionResource.getId());
    }
}