package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionOrganisationConfig;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionOrganisationConfigResourceBuilder.newCompetitionOrganisationConfigResource;
import static org.junit.Assert.assertTrue;

public class CompetitionOrganisationConfigControllerIntegrationTest extends BaseControllerIntegrationTest<CompetitionOrganisationConfigController> {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Override
    @Autowired
    protected void setControllerUnderTest(CompetitionOrganisationConfigController controller) {
        this.controller = controller;
    }

    @Before
    public void setLoggedInUserOnThread() {
        loginCompAdmin();
    }

    @Test
    public void saveOrganisationalEligibility() throws Exception {

        long competitionId = 100L;
        CompetitionOrganisationConfig config = new CompetitionOrganisationConfig();
        Competition competition = competitionRepository.save(newCompetition().withId(competitionId).withCompetitionOrganisationConfig(config).build());

        RestResult<Void> result = controller.update(competition.getId(), newCompetitionOrganisationConfigResource()
                .withInternationalOrganisationsAllowed(false).build());
        assertTrue(result.isSuccess());
    }
}
