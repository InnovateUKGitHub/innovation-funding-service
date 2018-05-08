package org.innovateuk.ifs.competitionsetup.service.modelpopulator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.TermsAndConditionsResource;
import org.innovateuk.ifs.competition.service.TermsAndConditionsRestService;
import org.innovateuk.ifs.competitionsetup.core.populator.TermsAndConditionsModelPopulator;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.TermsAndConditionsViewModel;
import org.innovateuk.ifs.competitionsetup.viewmodel.fragments.GeneralSetupViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.TermsAndConditionsResourceBuilder.newTermsAndConditionsResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
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
        TermsAndConditionsResource termsAndConditions = newTermsAndConditionsResource().build();

        CompetitionResource competitionResource = newCompetitionResource()
                .withTermsAndConditions(termsAndConditions)
                .build();

        List<TermsAndConditionsResource> termsAndConditionsList = new ArrayList<>();
        termsAndConditionsList.add(competitionResource.getTermsAndConditions());

        when(termsAndConditionsRestService.getById(termsAndConditions.getId()))
                .thenReturn(restSuccess(competitionResource.getTermsAndConditions()));
        when(termsAndConditionsRestService.getLatestVersionsForAllTermsAndConditions()).thenReturn(restSuccess(termsAndConditionsList));

        TermsAndConditionsViewModel viewModel = (TermsAndConditionsViewModel) populator.populateModel(
                getBasicGeneralSetupView(competitionResource),
                competitionResource);

        assertEquals(CompetitionSetupSection.TERMS_AND_CONDITIONS, viewModel.getGeneral().getCurrentSection());
        assertEquals(viewModel.getTermsAndConditionsList(), termsAndConditionsList);
        assertEquals(viewModel.getCurrentTermsAndConditions(), termsAndConditions);
    }

    private GeneralSetupViewModel getBasicGeneralSetupView(CompetitionResource competition) {
        return new GeneralSetupViewModel(Boolean.FALSE, competition, CompetitionSetupSection.TERMS_AND_CONDITIONS, CompetitionSetupSection.values(), Boolean.TRUE);
    }
}
