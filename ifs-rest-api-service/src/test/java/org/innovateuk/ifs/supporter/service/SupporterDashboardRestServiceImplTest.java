package org.innovateuk.ifs.supporter.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.supporter.resource.SupporterDashboardApplicationPageResource;
import org.junit.Test;

import static java.lang.String.format;
import static org.junit.Assert.assertSame;

public class SupporterDashboardRestServiceImplTest extends BaseRestServiceUnitTest<SupporterDashboardRestServiceImpl> {

    private String supporterRestUrl = "/supporter/dashboard";

    @Override
    protected SupporterDashboardRestServiceImpl registerRestServiceUnderTest() {
        return new SupporterDashboardRestServiceImpl();
    }

    @Test
    public void getSupporterCompetitionDashboardApplications() {
        long userId = 1L;
        long competitionId = 2L;
        int page = 3;
        SupporterDashboardApplicationPageResource expected = new SupporterDashboardApplicationPageResource();

        setupGetWithRestResultExpectations(format("%s/user/%d/competition/%d?page=%d", supporterRestUrl, userId, competitionId, page), SupporterDashboardApplicationPageResource.class, expected);
        assertSame(expected, service.getSupporterCompetitionDashboardApplications(userId, competitionId, page).getSuccess());
    }
}
