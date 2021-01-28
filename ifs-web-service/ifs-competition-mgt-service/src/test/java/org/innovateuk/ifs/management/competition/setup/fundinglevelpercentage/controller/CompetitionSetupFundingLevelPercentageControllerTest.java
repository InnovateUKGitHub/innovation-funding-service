package org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.populator.FundingLevelPercentageFormPopulator;
import org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.sectionupdater.FundingLevelPercentageSectionUpdater;
import org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.validator.FundingLevelPercentageValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.FUNDING_LEVEL_PERCENTAGE;
import static org.innovateuk.ifs.competition.resource.FundingRules.STATE_AID;
import static org.innovateuk.ifs.competition.resource.FundingRules.SUBSIDY_CONTROL;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionSetupFundingLevelPercentageControllerTest extends BaseControllerMockMVCTest<CompetitionSetupFundingLevelPercentageController> {
    private static final long COMPETITION_ID = 1L;
    private static final String URL = "/competition/setup/{competitionId}/section/funding-level-percentage";
    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private CompetitionSetupService competitionSetupService;

    @Mock
    private FundingLevelPercentageValidator fundingLevelPercentageValidator;

    @Mock
    private FundingLevelPercentageSectionUpdater updater;

    @Mock
    private FundingLevelPercentageFormPopulator formPopulator;

    @Override
    protected CompetitionSetupFundingLevelPercentageController supplyControllerUnderTest() {
        CompetitionSetupFundingLevelPercentageController controller =  new CompetitionSetupFundingLevelPercentageController();
        setField(controller, "northernIrelandSubsidyControlToggle", true);
        return controller;
    }

    @Before
    public void setup() {
        when(competitionSetupService.getSectionFormPopulator(FUNDING_LEVEL_PERCENTAGE)).thenReturn(formPopulator);
    }

    @Test
    public void fundingLevelPercentage_stateAid() throws Exception {
        CompetitionResource competition = newCompetitionResource().withId(COMPETITION_ID)
                .withFundingRules(FundingRules.STATE_AID)
                .build();
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));
        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(COMPETITION_ID)).thenReturn(true);
        CompetitionSetupViewModel viewModel = mock(CompetitionSetupViewModel.class);
        when(competitionSetupService.populateCompetitionSectionModelAttributes(competition, loggedInUser, FUNDING_LEVEL_PERCENTAGE)).thenReturn(viewModel);
        CompetitionSetupForm form = mock(CompetitionSetupForm.class);
        when(formPopulator.populateForm(competition)).thenReturn(form);

        mockMvc.perform(get(URL, COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup"))
                .andExpect(MockMvcResultMatchers.model().attribute("model", viewModel))
                .andExpect(MockMvcResultMatchers.model().attribute("competitionSetupForm", form));
    }

    @Test
    public void fundingLevelPercentage_subsidyControl() throws Exception {
        CompetitionResource competition = newCompetitionResource().withId(COMPETITION_ID)
                .withFundingRules(FundingRules.SUBSIDY_CONTROL)
                .build();
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));
        CompetitionSetupViewModel viewModel = mock(CompetitionSetupViewModel.class);
        when(competitionSetupService.populateCompetitionSectionModelAttributes(competition, loggedInUser, FUNDING_LEVEL_PERCENTAGE)).thenReturn(viewModel);
        GeneralSetupViewModel generalSetupViewModel = mock(GeneralSetupViewModel.class);
        when(viewModel.getGeneral()).thenReturn(generalSetupViewModel);
        when(generalSetupViewModel.isEditable()).thenReturn(true);

        mockMvc.perform(get(URL, COMPETITION_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/competition/setup/%d/section/funding-level-percentage/funding-rule/%s", COMPETITION_ID, FundingRules.SUBSIDY_CONTROL.toUrl())));
    }

    @Test
    public void fundingLevelPercentage_subsidyControl_readonly() throws Exception {
        CompetitionResource competition = newCompetitionResource().withId(COMPETITION_ID)
                .withFundingRules(FundingRules.SUBSIDY_CONTROL)
                .build();
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));
        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(COMPETITION_ID)).thenReturn(true);
        CompetitionSetupViewModel viewModel = mock(CompetitionSetupViewModel.class);
        when(competitionSetupService.populateCompetitionSectionModelAttributes(competition, loggedInUser, FUNDING_LEVEL_PERCENTAGE)).thenReturn(viewModel);
        GeneralSetupViewModel generalSetupViewModel = mock(GeneralSetupViewModel.class);
        when(viewModel.getGeneral()).thenReturn(generalSetupViewModel);
        when(generalSetupViewModel.isEditable()).thenReturn(false);
        CompetitionSetupForm form = mock(CompetitionSetupForm.class);
        when(formPopulator.populateForm(competition)).thenReturn(form);

        mockMvc.perform(get(URL, COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup"))
                .andExpect(MockMvcResultMatchers.model().attribute("model", viewModel))
                .andExpect(MockMvcResultMatchers.model().attribute("competitionSetupForm", form));
    }

    @Test
    public void testFundingLevelPercentage() throws Exception {
        CompetitionResource competition = newCompetitionResource().withId(COMPETITION_ID)
                .withFundingRules(FundingRules.SUBSIDY_CONTROL)
                .build();
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));
        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(COMPETITION_ID)).thenReturn(true);
        CompetitionSetupViewModel viewModel = mock(CompetitionSetupViewModel.class);
        when(competitionSetupService.populateCompetitionSectionModelAttributes(competition, loggedInUser, FUNDING_LEVEL_PERCENTAGE)).thenReturn(viewModel);
        GeneralSetupViewModel generalSetupViewModel = mock(GeneralSetupViewModel.class);
        when(viewModel.getGeneral()).thenReturn(generalSetupViewModel);
        when(generalSetupViewModel.isEditable()).thenReturn(true);
        CompetitionSetupForm form = mock(CompetitionSetupForm.class);
        when(formPopulator.populateForm(competition, STATE_AID)).thenReturn(form);

        mockMvc.perform(get(URL + "/funding-rule/" + STATE_AID.toUrl(), COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup"))
                .andExpect(MockMvcResultMatchers.model().attribute("model", viewModel))
                .andExpect(MockMvcResultMatchers.model().attribute("competitionSetupForm", form));
    }

    @Test
    public void submitFundingLevelPercentageSectionDetails() throws Exception {
        CompetitionResource competition = newCompetitionResource().withId(COMPETITION_ID)
                .withFundingRules(STATE_AID)
                .build();
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));
        when(competitionSetupService.saveCompetitionSetupSection(any(), eq(competition), eq(FUNDING_LEVEL_PERCENTAGE))).thenReturn(serviceSuccess());

        mockMvc.perform(post(URL, COMPETITION_ID)
                .param("maximum[0][0].maximum", "50"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/competition/setup/%d/section/funding-level-percentage", COMPETITION_ID)));

        verify(fundingLevelPercentageValidator).validate(any(), any());
    }

    @Test
    public void submitFundingRulesPercentages_subsidyControl() throws Exception{
        CompetitionResource competition = newCompetitionResource().withId(COMPETITION_ID)
                .withFundingRules(SUBSIDY_CONTROL)
                .build();
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));
        when(updater.saveSection(eq(competition), any())).thenReturn(serviceSuccess());

        mockMvc.perform(post(URL + "/funding-rule/" + SUBSIDY_CONTROL.toUrl(), COMPETITION_ID)
                .param("maximum[0][0].maximum", "50"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/competition/setup/%d/section/funding-level-percentage/funding-rule/%s", COMPETITION_ID, STATE_AID.toUrl())));

        verify(fundingLevelPercentageValidator).validate(any(), any());
    }

    @Test
    public void submitFundingRulesPercentages_stateAid() throws Exception{
        CompetitionResource competition = newCompetitionResource().withId(COMPETITION_ID)
                .withFundingRules(SUBSIDY_CONTROL)
                .build();
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));
        when(competitionSetupService.saveCompetitionSetupSection(any(), eq(competition), eq(FUNDING_LEVEL_PERCENTAGE))).thenReturn(serviceSuccess());

        mockMvc.perform(post(URL + "/funding-rule/" + STATE_AID.toUrl(), COMPETITION_ID)
                .param("maximum[0][0].maximum", "50"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/competition/setup/%d/section/funding-level-percentage", COMPETITION_ID)));

        verify(fundingLevelPercentageValidator).validate(any(), any());
    }
}