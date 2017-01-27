package org.innovateuk.ifs.publiccontent.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemPageResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class PublicContentItemControllerIntegrationTest extends BaseControllerIntegrationTest<PublicContentItemController> {
    private static final Long COMPETITION_ID = 1L;


    @Override
    @Autowired
    protected void setControllerUnderTest(PublicContentItemController controller) {
        this.controller = controller;
    }


    @Before
    public void setLoggedInUserOnThread() {
        loginCompAdmin();
    }


    @Test
    @Rollback
    public void testFindFilteredItems() throws Exception {
        //Save competition, content, keywords

        flushAndClearSession();

        RestResult<PublicContentItemPageResource> result = controller.findFilteredItems(Optional.of(123L), Optional.of("Keywords"), Optional.of(1L), Optional.of(20L));

        assertTrue(result.isSuccess());

        //check result
    }

    @Test
    @Rollback
    public void testByCompetitionId() throws Exception {
        //Save competition, content
        flushAndClearSession();

        RestResult<PublicContentItemResource> result = controller.byCompetitionId(COMPETITION_ID);

        assertTrue(result.isSuccess());

        //check result
    }
}
