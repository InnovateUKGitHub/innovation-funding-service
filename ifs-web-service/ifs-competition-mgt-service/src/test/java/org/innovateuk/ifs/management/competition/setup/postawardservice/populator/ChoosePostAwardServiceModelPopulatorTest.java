package org.innovateuk.ifs.management.competition.setup.postawardservice.populator;

import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.competition.setup.postawardservice.viewmodel.ChoosePostAwardServiceViewModel;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ChoosePostAwardServiceModelPopulatorTest {

    private ChoosePostAwardServiceModelPopulator populator;

    @Before
    public void setUp() {
        populator = new ChoosePostAwardServiceModelPopulator();
    }

    @Test
    public void shouldPopulate() {
        // given
        Long id = 1L;
        String name = "competition";

        CompetitionResource competition = CompetitionResourceBuilder.newCompetitionResource()
                .withId(id)
                .withName(name)
                .build();

        // when
        ChoosePostAwardServiceViewModel result = populator.populateModel(competition);

        // then
        assertEquals(id, result.getCompetitionId());
        assertEquals(name, result.getCompetitionName());
    }
}
