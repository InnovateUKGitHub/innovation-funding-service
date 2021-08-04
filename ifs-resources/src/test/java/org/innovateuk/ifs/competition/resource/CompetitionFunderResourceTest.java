package org.innovateuk.ifs.competition.resource;

import org.junit.Test;

import static org.innovateuk.ifs.competition.builder.CompetitionFunderResourceBuilder.newCompetitionFunderResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CompetitionFunderResourceTest {

    @Test
    public void isOfGem() {
        CompetitionFunderResource competitionFunderResource = newCompetitionFunderResource()
                .withFunder(Funder.OFFICE_OF_GAS_AND_ELECTRICITY_MARKETS_OFGEM)
                .build();

        assertTrue(competitionFunderResource.isOfGem());
    }

    @Test
    public void isNotOfGem() {
        CompetitionFunderResource competitionFunderResource = newCompetitionFunderResource()
                .withFunder(Funder.OTHER_STAKEHOLDERS)
                .build();

        assertFalse(competitionFunderResource.isOfGem());
    }
}
