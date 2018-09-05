package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link OrganisationDetailsRestServiceImpl}
 */
public class OrganisationDetailsRestServiceImplTest extends
        BaseRestServiceUnitTest<OrganisationDetailsRestServiceImpl> {

    private static final String projectUrl = "/project";

    @Override
    protected OrganisationDetailsRestServiceImpl registerRestServiceUnderTest() {
        return new OrganisationDetailsRestServiceImpl();
    }

    @Test
    public void test_getHeadcount() {
        long applicationId = 1L;
        long organisationId = 2L;
        String expectedUrl = projectUrl + "/headcount/" + applicationId + "/" + organisationId;
        Long count = 1L;

        setupGetWithRestResultExpectations(expectedUrl, Long.TYPE, count);

        // now run the method under test
        Long actualCount = service.getHeadCount(applicationId, organisationId).getSuccess();
        assertEquals(actualCount, count);
    }

    @Test
    public void test_getTurnover() {
        long applicationId = 1L;
        long organisationId = 2L;
        String expectedUrl = projectUrl + "/turnover/" + applicationId + "/" + organisationId;
        Long count = 1L;

        setupGetWithRestResultExpectations(expectedUrl, Long.TYPE, count);

        // now run the method under test
        Long actualCount = service.getTurnover(applicationId, organisationId).getSuccess();
        assertEquals(actualCount, count);
    }
}
