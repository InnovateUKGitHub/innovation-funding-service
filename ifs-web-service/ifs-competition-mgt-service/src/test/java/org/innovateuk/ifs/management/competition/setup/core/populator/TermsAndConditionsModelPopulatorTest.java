package org.innovateuk.ifs.management.competition.setup.core.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.competition.service.TermsAndConditionsRestService;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.TermsAndConditionsViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsResourceBuilder.newGrantTermsAndConditionsResource;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class TermsAndConditionsModelPopulatorTest {

    @InjectMocks
    private TermsAndConditionsModelPopulator populator;

    @Mock
    private TermsAndConditionsRestService termsAndConditionsRestService;

    @Mock
    private CompetitionSetupPopulator competitionSetupPopulator;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(populator, "subsidyControlNorthernIrelandEnabled", true);
    }

    @Test
    public void populateModel() {
        List<GrantTermsAndConditionsResource> termsAndConditions = newGrantTermsAndConditionsResource().build(1);

        CompetitionResource competitionResource = newCompetitionResource()
                .withTermsAndConditions(termsAndConditions.get(0))
                .withCompetitionTerms(newFileEntryResource().build())
                .build();

        UserResource userResource = newUserResource().build();

        when(termsAndConditionsRestService.getById(competitionResource.getTermsAndConditions().getId()))
                .thenReturn(restSuccess(competitionResource.getTermsAndConditions()));
        when(termsAndConditionsRestService.getLatestVersionsForAllTermsAndConditions()).thenReturn(
                restSuccess(termsAndConditions));
        when(competitionSetupPopulator.populateGeneralModelAttributes(competitionResource, userResource, CompetitionSetupSection.TERMS_AND_CONDITIONS)).thenReturn(getBasicGeneralSetupView(competitionResource));

        TermsAndConditionsViewModel viewModel = populator.populateModel(
                competitionResource,
                userResource,
                false);

        assertEquals(CompetitionSetupSection.TERMS_AND_CONDITIONS, viewModel.getGeneral().getCurrentSection());
        assertEquals(viewModel.getTermsAndConditionsList(), termsAndConditions);
        assertEquals(viewModel.getCurrentTermsAndConditions(), termsAndConditions.get(0));
        assertNull(viewModel.getCurrentStateAidTermsAndConditions());
        assertTrue(viewModel.isTermsAndConditionsDocUploaded());
    }

    @Test
    public void populateModelWhenDualTermsAndConditions() {
        List<GrantTermsAndConditionsResource> termsAndConditions = newGrantTermsAndConditionsResource().build(2);

        CompetitionResource competitionResource = newCompetitionResource()
                .withTermsAndConditions(termsAndConditions.get(0))
                .withOtherFundingRulesTermsAndConditions(termsAndConditions.get(1))
                .withCompetitionTerms(newFileEntryResource().build())
                .withFundingRules(FundingRules.SUBSIDY_CONTROL)
                .build();

        UserResource userResource = newUserResource().build();

        when(termsAndConditionsRestService.getById(competitionResource.getTermsAndConditions().getId()))
                .thenReturn(restSuccess(competitionResource.getTermsAndConditions()));
        when(termsAndConditionsRestService.getById(competitionResource.getOtherFundingRulesTermsAndConditions().getId()))
                .thenReturn(restSuccess(competitionResource.getOtherFundingRulesTermsAndConditions()));
        when(termsAndConditionsRestService.getLatestVersionsForAllTermsAndConditions()).thenReturn(
                restSuccess(termsAndConditions));
        when(competitionSetupPopulator.populateGeneralModelAttributes(competitionResource, userResource, CompetitionSetupSection.TERMS_AND_CONDITIONS)).thenReturn(getBasicGeneralSetupView(competitionResource));

        TermsAndConditionsViewModel viewModel = populator.populateModel(
                competitionResource,
                userResource,
                false);

        assertEquals(CompetitionSetupSection.TERMS_AND_CONDITIONS, viewModel.getGeneral().getCurrentSection());
        assertEquals(viewModel.getTermsAndConditionsList(), termsAndConditions);
        assertEquals(viewModel.getCurrentTermsAndConditions(), termsAndConditions.get(0));
        assertEquals(viewModel.getCurrentStateAidTermsAndConditions(), termsAndConditions.get(1));
        assertTrue(viewModel.isTermsAndConditionsDocUploaded());
    }

    private GeneralSetupViewModel getBasicGeneralSetupView(CompetitionResource competition) {
        return new GeneralSetupViewModel(false, false, competition, CompetitionSetupSection.TERMS_AND_CONDITIONS,
                CompetitionSetupSection.values(), true, false);
    }
}
