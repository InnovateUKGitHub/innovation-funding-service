package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for testing the rest services of the competition type controller
 */
public class CompetitionTypeControllerIntegrationTest extends BaseControllerIntegrationTest<CompetitionTypeController> {

    @Override
    @Autowired
    protected void setControllerUnderTest(CompetitionTypeController controller) {
        this.controller = controller;
    }

    @Before
    public void setLoggedInUserOnThread() {
        loginCompAdmin();
    }

    @Test
    @Rollback
    public void getAllCompetitionTypes() {
        RestResult<List<CompetitionTypeResource>> competitionTypesResult = controller.findAllTypes();

        assertThat(competitionTypesResult.isSuccess()).isTrue();
        List<CompetitionTypeResource> competitionTypes = competitionTypesResult.getSuccess();

        // Test ordering.
        assertThat(competitionTypes)
                .extracting(CompetitionTypeResource::getName)
                .hasSize(12)
                .containsExactly(
                        "Additive Manufacturing",
                        "Advanced Propulsion Centre",
                        "Aerospace Technology Institute",
                        "Expression of interest",
                        "Generic",
                        "HEUKAR",
                        "Horizon 2020",
                        "Programme",
                        "SBRI",
                        "Sector",
                        "Special",
                        "The Prince's Trust"
                );
    }
}
