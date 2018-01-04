package org.innovateuk.ifs.competitionsetup.service.modelpopulator.application;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupFinanceService;
import org.innovateuk.ifs.competitionsetup.viewmodel.application.ApplicationFinanceViewModel;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationFinanceModelPopulatorTest {

    @InjectMocks
    private ApplicationFinanceModelPopulator populator;

    @Mock
    private CompetitionSetupFinanceService competitionSetupFinanceService;

    @Mock
    private CompetitionService competitionService;

    @Mock
    private SectionService sectionService;

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
                .withCompetitionTypeName("programme")
                .build();

        assertTrue(competition.isNonFinanceType());

        ApplicationFinanceViewModel viewModel = (ApplicationFinanceViewModel) populator.populateModel(competition, Optional.empty());

        assertEquals(ApplicationFinanceViewModel.class, viewModel.getClass());
        assertEquals(false, viewModel.isSectorCompetition());
        assertEquals(true, viewModel.isNoneFinanceCompetition());
    }
}
