package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.populator.CompetitionOverviewPopulator;
import org.innovateuk.ifs.competition.populator.CompetitionTermsAndConditionsPopulator;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.viewmodel.CompetitionOverviewViewModel;
import org.innovateuk.ifs.competition.viewmodel.CompetitionTermsViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.publiccontent.service.PublicContentItemRestService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionThirdPartyConfigResourceBuilder.newCompetitionThirdPartyConfigResource;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentItemResourceBuilder.newPublicContentItemResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
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

    @Mock
    private CompetitionTermsAndConditionsPopulator competitionTermsAndConditionsPopulator;

    @Test
    public void competitionOverview() throws Exception {
        final long competitionId = 20L;

        final PublicContentResource publicContent = newPublicContentResource()
                .withHash(null)
                .build();

        final PublicContentItemResource publicContentItem = newPublicContentItemResource()
                .withPublicContentResource(publicContent)
                .build();

        final CompetitionOverviewViewModel viewModel = new CompetitionOverviewViewModel();

        when(publicContentItemRestService.getItemByCompetitionId(competitionId)).thenReturn(restSuccess(publicContentItem));
        when(overviewPopulator.populateViewModel(publicContentItem, true)).thenReturn(viewModel);

        mockMvc.perform(get("/competition/{id}/overview", competitionId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", viewModel))
                .andExpect(view().name("competition/overview"));
    }

    @Test
    public void competitionOverviewWithHash() throws Exception {

        final long competitionId = 999L;
        final String hash = "abc-123";

        final PublicContentResource publicContent =
                newPublicContentResource()
                        .withHash(hash)
                        .build();

        final PublicContentItemResource publicContentItem =
                newPublicContentItemResource()
                        .withPublicContentResource(publicContent)
                        .build();

        final CompetitionOverviewViewModel viewModel = new CompetitionOverviewViewModel();

        when(publicContentItemRestService.getItemByCompetitionId(competitionId)).thenReturn(restSuccess(publicContentItem));
        when(overviewPopulator.populateViewModel(publicContentItem, true)).thenReturn(viewModel);

        mockMvc.perform(get("/competition/{id}/overview/{hash}", competitionId, hash))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", viewModel))
                .andExpect(view().name("competition/overview"));

        verify(publicContentItemRestService).getItemByCompetitionId(competitionId);
        verify(overviewPopulator).populateViewModel(publicContentItem, true);
    }

    @Test
    public void competitionOverviewWithIncorrectHash() throws Exception {

        final long competitionId = 999L;
        final String hash = "abc-123";
        final String incorrectHash = "def-456";

        final PublicContentResource publicContent =
                newPublicContentResource()
                        .withHash(hash)
                        .build();

        final PublicContentItemResource publicContentItem =
                newPublicContentItemResource()
                        .withPublicContentResource(publicContent)
                        .build();

        when(publicContentItemRestService.getItemByCompetitionId(competitionId)).thenReturn(restSuccess(publicContentItem));

        mockMvc.perform(get("/competition/{id}/overview/{hash}", competitionId, incorrectHash))
                .andExpect(status().isForbidden());

        verify(publicContentItemRestService).getItemByCompetitionId(competitionId);
        verifyNoInteractions(overviewPopulator);
    }

    @Test
    public void privateCompetitionOverview() throws Exception {

        final long competitionId = 999L;
        final String hash = "abc-123";

        final PublicContentResource publicContent =
                newPublicContentResource()
                        .withHash(hash)
                        .withInviteOnly(true)
                        .build();

        final PublicContentItemResource publicContentItem =
                newPublicContentItemResource()
                        .withPublicContentResource(publicContent)
                        .build();

        final CompetitionOverviewViewModel viewModel = new CompetitionOverviewViewModel();

        when(publicContentItemRestService.getItemByCompetitionId(competitionId)).thenReturn(restSuccess(publicContentItem));
        when(overviewPopulator.populateViewModel(publicContentItem, true)).thenReturn(viewModel);

        mockMvc.perform(get("/competition/{id}/overview/{hash}", competitionId, hash))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", viewModel))
                .andExpect(view().name("competition/overview"));

        verify(publicContentItemRestService).getItemByCompetitionId(competitionId);
        verify(overviewPopulator).populateViewModel(publicContentItem, true);
    }

    @Test
    public void privateCompetitionOverviewWithIncorrectHash() throws Exception {

        final long competitionId = 999L;
        final String hash = "abc-123";
        final String incorrectHash = "def-456";

        final PublicContentResource publicContent =
                newPublicContentResource()
                        .withHash(hash)
                        .withInviteOnly(true)
                        .build();

        final PublicContentItemResource publicContentItem =
                newPublicContentItemResource()
                        .withPublicContentResource(publicContent)
                        .build();

        when(publicContentItemRestService.getItemByCompetitionId(competitionId)).thenReturn(restSuccess(publicContentItem));

        mockMvc.perform(get("/competition/{id}/overview/{hash}", competitionId, incorrectHash))
                .andExpect(status().isForbidden());

        verify(publicContentItemRestService).getItemByCompetitionId(competitionId);
        verifyNoInteractions(overviewPopulator);
    }

    @Test
    public void competitionOverview_notLoggedIn() throws Exception {
        final long competitionId = 20L;
        setLoggedInUser(null);

        final PublicContentResource publicContent =
                newPublicContentResource()
                        .withHash(null)
                        .build();

        final PublicContentItemResource publicContentItem = newPublicContentItemResource()
                .withPublicContentResource(publicContent)
                .build();

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

        final CompetitionResource competitionResource = newCompetitionResource().build();

        CompetitionTermsViewModel competitionTermsViewModel = new CompetitionTermsViewModel(competitionResource.getId(), termsAndConditions);

        when(competitionTermsAndConditionsPopulator.populate(competitionResource.getId())).thenReturn(competitionTermsViewModel);

        mockMvc.perform(get("/competition/{id}/info/terms-and-conditions", competitionResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", competitionTermsViewModel))
                .andExpect(view().name("competition/info/special-terms-and-conditions"));

        verify(competitionTermsAndConditionsPopulator, only()).populate(competitionResource.getId());
    }

    @Test
    public void thirdPartyTermsAndConditions() throws Exception {
        GrantTermsAndConditionsResource termsAndConditions = new GrantTermsAndConditionsResource("T&C",
                "third-party-terms-and-conditions", 3);

        CompetitionResource competitionResource = newCompetitionResource().build();

        FileEntryResource fileEntryResource = newFileEntryResource().build();

        CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource = newCompetitionThirdPartyConfigResource()
                .withTermsAndConditionsLabel("Strategic Innovation Fund Governance Document")
                .withTermsAndConditionsGuidance( "<div>Guidance</div>")
                .build();

        CompetitionTermsViewModel competitionTermsViewModel = new CompetitionTermsViewModel(competitionResource.getId(),
                termsAndConditions,
                fileEntryResource,
                competitionThirdPartyConfigResource);

        when(competitionTermsAndConditionsPopulator.populate(competitionResource.getId())).thenReturn(competitionTermsViewModel);

        mockMvc.perform(get("/competition/{id}/info/terms-and-conditions", competitionResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", competitionTermsViewModel))
                .andExpect(view().name("competition/info/third-party-terms-and-conditions"));

        verify(competitionTermsAndConditionsPopulator, only()).populate(competitionResource.getId());
    }

    @Test
    public void stateAidTermsAndConditions() throws Exception {
        GrantTermsAndConditionsResource termsAndConditions = new GrantTermsAndConditionsResource("T&C",
                "special-terms-and-conditions", 3);

        final CompetitionResource competitionResource = newCompetitionResource()
                .withCompetitionTypeName("Competition name")
                .withFundingRules(FundingRules.SUBSIDY_CONTROL)
                .withOtherFundingRulesTermsAndConditions(termsAndConditions)
                .build();

        when(competitionRestService.getCompetitionById(competitionResource.getId())).thenReturn(restSuccess(competitionResource));

        mockMvc.perform(get("/competition/{id}/info/state-aid-terms-and-conditions", competitionResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", new CompetitionTermsViewModel(competitionResource.getId())))
                .andExpect(view().name("competition/info/special-terms-and-conditions"));

        verify(competitionRestService, only()).getCompetitionById(competitionResource.getId());
    }

    @Test
    public void stateAidTermsAndConditionsForNonDualTermsAndConditionsCompetition() throws Exception {
        GrantTermsAndConditionsResource termsAndConditions = new GrantTermsAndConditionsResource("T&C",
                "special-terms-and-conditions", 3);

        final CompetitionResource competitionResource = newCompetitionResource()
                .withCompetitionTypeName("Competition name")
                .withOtherFundingRulesTermsAndConditions(termsAndConditions)
                .build();

        when(competitionRestService.getCompetitionById(competitionResource.getId())).thenReturn(restSuccess(competitionResource));

        mockMvc.perform(get("/competition/{id}/info/state-aid-terms-and-conditions", competitionResource.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/setup/" + competitionResource.getId()));

        verify(competitionRestService, only()).getCompetitionById(competitionResource.getId());
    }
}