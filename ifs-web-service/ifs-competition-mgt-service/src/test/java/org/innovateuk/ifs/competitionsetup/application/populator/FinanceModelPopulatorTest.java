package org.innovateuk.ifs.competitionsetup.application.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.application.viewmodel.FinanceViewModel;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class FinanceModelPopulatorTest {

    @InjectMocks
    private FinanceModelPopulator populator;

    @Test
    public void testSectionToPopulateModel() {
        CompetitionSetupSubsection result = populator.sectionToPopulateModel();
        assertEquals(CompetitionSetupSubsection.FINANCES, result);
    }

    @Test
    public void testPopulateModel() {
        long competitionId = 8L;
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionCode("code")
                .withName("name")
                .withId(competitionId)
                .withResearchCategories(CollectionFunctions.asLinkedSet(2L, 3L))
                .withNonFinanceType(true)
                .build();

        assertTrue(competition.isNonFinanceType());

        FinanceViewModel viewModel = (FinanceViewModel) populator.populateModel(competition, Optional.empty());

        assertEquals(FinanceViewModel.class, viewModel.getClass());
        assertFalse(viewModel.isSectorCompetition());
        assertTrue(viewModel.isNoFinancesCompetition());
    }
}
