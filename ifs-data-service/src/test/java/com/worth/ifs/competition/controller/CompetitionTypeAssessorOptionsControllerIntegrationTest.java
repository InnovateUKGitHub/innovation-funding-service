package com.worth.ifs.competition.controller;


import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.CompetitionTypeAssessorOptionResource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Integration test for testing the rest services of the competition type controller
 */
@Rollback
@Transactional
public class CompetitionTypeAssessorOptionsControllerIntegrationTest extends BaseControllerIntegrationTest<CompetitionTypeAssessorOptionsController> {

    @Override
    @Autowired
    protected void setControllerUnderTest(CompetitionTypeAssessorOptionsController controller) {
        this.controller = controller;
    }

    @Test
    @Rollback
    public void testGetAllByCompetitionType() {
        RestResult<List<CompetitionTypeAssessorOptionResource>> optionsResult = controller.getAllByCompetitionType(1L);
        assertTrue(optionsResult.isSuccess());
        List<CompetitionTypeAssessorOptionResource> competitionTypeAssessorOptions = optionsResult.getSuccessObject();

        // Check if all the expected options are here.
        assertEquals(3, competitionTypeAssessorOptions.size());

        // Test three options.
        assertEquals(Integer.valueOf(1), competitionTypeAssessorOptions.get(0).getAssessorOptionValue());
        assertEquals(Integer.valueOf(3), competitionTypeAssessorOptions.get(1).getAssessorOptionValue());
        assertEquals(Integer.valueOf(5), competitionTypeAssessorOptions.get(2).getAssessorOptionValue());
    }


}
