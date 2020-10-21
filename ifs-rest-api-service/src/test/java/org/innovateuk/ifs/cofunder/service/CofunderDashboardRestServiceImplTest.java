package org.innovateuk.ifs.cofunder.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardApplicationPageResource;
import org.junit.Test;

import static java.lang.String.format;
import static org.junit.Assert.assertSame;

public class CofunderDashboardRestServiceImplTest extends BaseRestServiceUnitTest<CofunderDashboardRestServiceImpl> {

    private String cofunderRestUrl = "/cofunder/dashboard";

    @Override
    protected CofunderDashboardRestServiceImpl registerRestServiceUnderTest() {
        return new CofunderDashboardRestServiceImpl();
    }

    @Test
    public void getCofunderCompetitionDashboardApplications() {
        long userId = 1L;
        long competitionId = 2L;
        int page = 3;
        CofunderDashboardApplicationPageResource expected = new CofunderDashboardApplicationPageResource();

        setupGetWithRestResultExpectations(format("%s/user/%d/competition/%d?page=%d", cofunderRestUrl, userId, competitionId, page), CofunderDashboardApplicationPageResource.class, expected);
        assertSame(expected, service.getCofunderCompetitionDashboardApplications(userId, competitionId, page).getSuccess());
    }
}
