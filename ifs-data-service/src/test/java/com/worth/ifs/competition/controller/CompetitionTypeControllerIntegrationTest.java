package com.worth.ifs.competition.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.category.repository.CategoryLinkRepository;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.CompetitionTypeResource;
import org.junit.Before;
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
public class CompetitionTypeControllerIntegrationTest extends BaseControllerIntegrationTest<CompetitionTypeController> {
    public static final int EXISTING_CATEGORY_LINK_BEFORE_TEST = 2;
    @Autowired
    CategoryLinkRepository categoryLinkRepository;


    public static final String COMPETITION_NAME_UPDATED = "Competition name updated";
    public static final int INNOVATION_SECTOR_ID = 1;
    public static final String INNOVATION_SECTOR_NAME = "Health and life sciences";
    public static final int INNOVATION_AREA_ID = 9;
    public static final int INNOVATION_AREA_ID_TWO = 10;
    public static final String INNOVATION_AREA_NAME = "Agriculture and food";
    public static final String EXISTING_COMPETITION_NAME = "Connected digital additive manufacturing";
    private static final Long COMPETITION_ID = 1L;

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
        assertTrue(competitionTypesResult.isSuccess());
        List<CompetitionTypeResource> competitionTypes = competitionTypesResult.getSuccessObject();

        // Check if all the type are here.
        assertEquals(5L, (long) competitionTypes.size());

        // Test ordering.
        assertEquals("Programme", competitionTypes.get(0).getName());
        assertEquals("Additive Manufacturing", competitionTypes.get(1).getName());
        assertEquals("SBRI", competitionTypes.get(2).getName());
        assertEquals("Special", competitionTypes.get(3).getName());
        assertEquals("Sector", competitionTypes.get(4).getName());
    }

}
