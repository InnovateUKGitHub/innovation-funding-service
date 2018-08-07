package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResearchCategoryLinkResource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class CompetitionResearchCategoryControllerIntegrationTest extends BaseControllerIntegrationTest<CompetitionResearchCategoryController> {

    @Autowired
    @Override
    protected void setControllerUnderTest(CompetitionResearchCategoryController controller) {
        this.controller = controller;
    }

    @Test
    public void findByCompetition() {
        Long competitionId = 1L;
        RestResult<List<CompetitionResearchCategoryLinkResource>> result = controller.findByCompetition(competitionId);

        assertTrue(result.isSuccess());
    }
}
