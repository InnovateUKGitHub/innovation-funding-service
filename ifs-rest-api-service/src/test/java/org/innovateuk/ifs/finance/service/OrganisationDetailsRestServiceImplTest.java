package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.finance.resource.OrganisationSizeResource;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.organisationSizeListType;
import static org.junit.Assert.*;

/**
 * Test for {@link OrganisationDetailsRestServiceImpl}
 */
public class OrganisationDetailsRestServiceImplTest extends BaseRestServiceUnitTest<OrganisationDetailsRestServiceImpl> {

    private static final String organisationSizeUrl = "/organisation-size";
    private static final String projectUrl = "/project";


    @Override
    protected OrganisationDetailsRestServiceImpl registerRestServiceUnderTest() {
        return new OrganisationDetailsRestServiceImpl();
    }

    @Test
    public void test_getOrganisationSizes() {

        List<OrganisationSizeResource> returnedResponse = new ArrayList<>();

        setupGetWithRestResultExpectations(organisationSizeUrl, organisationSizeListType(), returnedResponse);
        List<OrganisationSizeResource> actual = service.getOrganisationSizes().getSuccessObject();
        assertNotNull(actual);
        assertEquals(returnedResponse, actual);
    }


    @Test
    public void test_getHeadcount() {
        long applicationId = 1L;
        long organisationId = 2L;
        String expectedUrl = projectUrl + "/headcount/" + applicationId + "/" + organisationId;
        Long count = 1L;

        setupGetWithRestResultExpectations(expectedUrl, Long.TYPE, count);

        // now run the method under test
        Long actualCount = service.getHeadCount(applicationId, organisationId).getSuccessObject();
        assertEquals(actualCount, Long.valueOf(count));
    }

    @Test
    public void test_getTurnover() {
        long applicationId = 1L;
        long organisationId = 2L;
        String expectedUrl = projectUrl + "/turnover/" + applicationId + "/" + organisationId;
        Long count = 1L;

        setupGetWithRestResultExpectations(expectedUrl, Long.TYPE, count);

        // now run the method under test
        Long actualCount = service.getTurnover(applicationId, organisationId).getSuccessObject();
        assertEquals(actualCount, Long.valueOf(count));
    }

}
