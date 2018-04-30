package org.innovateuk.ifs.competitionsetup.service.modelpopulator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.TermsAndConditionsResource;
import org.innovateuk.ifs.competition.service.TermsAndConditionsRestService;
import org.innovateuk.ifs.competitionsetup.viewmodel.TermsAndConditionsViewModel;
import org.innovateuk.ifs.competitionsetup.viewmodel.fragments.GeneralSetupViewModel;
import org.innovateuk.ifs.util.CollectionFunctions;
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

//    @Test
//    public void populateModel() {
//        TermsAndConditionsResource termsAndConditions = newTermsAndConditionsResource()
//                .withName("default")
//                .withTemplate("default-template")
//                .withVersion("1").build();
//
//        CompetitionResource competitionResource = newCompetitionResource()
//                .withCompetitionCode("code")
//                .withName("name")
//                .withResearchCategories(CollectionFunctions.asLinkedSet(2L, 3L))
//                .withTermsAndConditions(termsAndConditions)
//                .build();
//
//        List<TermsAndConditionsResource> termsAndConditionsList = new ArrayList<>();
//        termsAndConditionsList.add(termsAndConditions);
//
//        when(termsAndConditionsRestService.getById(competitionResource.getId())).thenReturn(restSuccess(termsAndConditions));
//        when(termsAndConditionsRestService.getLatestTermsAndConditions()).thenReturn(restSuccess(termsAndConditionsList));
//
//        TermsAndConditionsViewModel viewModel = (TermsAndConditionsViewModel) populator.populateModel(
//                getBasicGeneralSetupView(competitionResource),
//                competitionResource);
//
//        assertEquals(CompetitionSetupSection.TERMS_AND_CONDITIONS, viewModel.getGeneral().getCurrentSection());
//        assertEquals(viewModel.getTermsAndConditionsList(), termsAndConditionsList);
//        assertEquals(viewModel.getCurrentTermsAndConditions(), termsAndConditions);
//    }

    private GeneralSetupViewModel getBasicGeneralSetupView(CompetitionResource competition) {
        return new GeneralSetupViewModel(Boolean.FALSE, competition, CompetitionSetupSection.TERMS_AND_CONDITIONS, CompetitionSetupSection.values(), Boolean.TRUE);
    }
}
