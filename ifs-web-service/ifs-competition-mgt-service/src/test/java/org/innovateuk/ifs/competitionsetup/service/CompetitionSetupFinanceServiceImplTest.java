package org.innovateuk.ifs.competitionsetup.service;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionSetupFinanceServiceImplTest {

    @InjectMocks
    private CompetitionSetupFinanceServiceImpl service;

    @Test
    public void testIsNoneFinanceCompetitionSuccess() {
        CompetitionResource competition = newCompetitionResource()
                .withId(1L)
                .withCompetitionTypeName("Expression of interest")
                .build();

        boolean result = service.isNoneFinanceCompetition(competition);

        assertTrue(result);
    }

    @Test
    public void testIsNoneFinanceCompetitionFailing() {
        CompetitionResource competition = newCompetitionResource()
                .withId(1L)
                .withCompetitionTypeName("Generic")
                .build();

        boolean result = service.isNoneFinanceCompetition(competition);

        assertFalse(result);
    }
}
