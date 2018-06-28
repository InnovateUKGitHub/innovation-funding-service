package org.innovateuk.ifs.competitionsetup.core.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.competition.service.TermsAndConditionsRestService;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.TermsAndConditionsViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsResourceBuilder.newGrantTermsAndConditionsResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TermsAndConditionsModelPopulatorTest {

    @InjectMocks
    private TermsAndConditionsModelPopulator populator;

    @Mock
    private TermsAndConditionsRestService termsAndConditionsRestService;

    @Test
    public void testSectionToPopulateModel() {
        CompetitionSetupSection result = populator.sectionToPopulateModel();

        assertEquals(CompetitionSetupSection.TERMS_AND_CONDITIONS, result);
    }

    @Test
    public void populateModel() {
        List<GrantTermsAndConditionsResource> termsAndConditions = newGrantTermsAndConditionsResource().build(1);

        CompetitionResource competitionResource = newCompetitionResource()
                .withTermsAndConditions(termsAndConditions.get(0))
                .build();

        when(termsAndConditionsRestService.getById(competitionResource.getTermsAndConditions().getId()))
                .thenReturn(restSuccess(competitionResource.getTermsAndConditions()));
        when(termsAndConditionsRestService.getLatestVersionsForAllTermsAndConditions()).thenReturn(
                restSuccess(termsAndConditions));

        TermsAndConditionsViewModel viewModel = (TermsAndConditionsViewModel) populator.populateModel(
                getBasicGeneralSetupView(competitionResource),
                competitionResource);

        assertEquals(CompetitionSetupSection.TERMS_AND_CONDITIONS, viewModel.getGeneral().getCurrentSection());
        assertEquals(viewModel.getTermsAndConditionsList(), termsAndConditions);
        assertEquals(viewModel.getCurrentTermsAndConditions(), termsAndConditions.get(0));
    }

    private GeneralSetupViewModel getBasicGeneralSetupView(CompetitionResource competition) {
        return new GeneralSetupViewModel(Boolean.FALSE, competition, CompetitionSetupSection.TERMS_AND_CONDITIONS,
                CompetitionSetupSection.values(), Boolean.TRUE);
    }
}
