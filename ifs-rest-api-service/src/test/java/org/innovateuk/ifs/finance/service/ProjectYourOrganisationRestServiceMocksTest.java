package org.innovateuk.ifs.finance.service;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestServiceImpl;
import org.junit.Test;
import org.springframework.http.HttpStatus;

public class ProjectYourOrganisationRestServiceMocksTest extends BaseRestServiceUnitTest<ProjectYourOrganisationRestServiceImpl> {

    private final static long projectId = 1L;
    private final static long organisationId = 2L;
    private static String baseUrl = format("/project/%d/organisation/%d/finance", projectId, organisationId);

    @Override
    protected ProjectYourOrganisationRestServiceImpl registerRestServiceUnderTest() {
        return new ProjectYourOrganisationRestServiceImpl();
    }

    @Test
    public void getOrganisationFinancesWithGrowthTable() {
        OrganisationFinancesWithGrowthTableResource expectedResponse = new OrganisationFinancesWithGrowthTableResource();

        setupGetWithRestResultExpectations(baseUrl + "/with-growth-table", OrganisationFinancesWithGrowthTableResource.class, expectedResponse);

        OrganisationFinancesWithGrowthTableResource result =
            service.getOrganisationFinancesWithGrowthTable(projectId, organisationId).getSuccess();

        assertEquals(expectedResponse, result);
    }

    @Test
    public void getOrganisationFinancesWithoutGrowthTable() {
        OrganisationFinancesWithoutGrowthTableResource expectedResponse =
            new OrganisationFinancesWithoutGrowthTableResource();

        setupGetWithRestResultExpectations(baseUrl + "/without-growth-table",
            OrganisationFinancesWithoutGrowthTableResource.class, expectedResponse);

        OrganisationFinancesWithoutGrowthTableResource result =
            service.getOrganisationFinancesWithoutGrowthTable(projectId, organisationId).getSuccess();

        assertEquals(expectedResponse, result);
    }

    @Test
    public void updateOrganisationFinancesWithGrowthTable() {
        OrganisationFinancesWithGrowthTableResource finances = new OrganisationFinancesWithGrowthTableResource();

        setupPostWithRestResultExpectations(baseUrl + "/with-growth-table", finances, HttpStatus.OK);

        ServiceResult<Void> actual = service.updateOrganisationFinancesWithGrowthTable(projectId, organisationId, finances);
        assertTrue(actual.isSuccess());
    }

    @Test
    public void updateOrganisationFinancesWithoutGrowthTable() {
        OrganisationFinancesWithoutGrowthTableResource finances = new OrganisationFinancesWithoutGrowthTableResource();

        setupPostWithRestResultExpectations(baseUrl + "/without-growth-table", finances, HttpStatus.OK);

        ServiceResult<Void> actual = service.updateOrganisationFinancesWithoutGrowthTable(projectId, organisationId, finances);
        assertTrue(actual.isSuccess());
    }

    @Test
    public void isShowStateAidAgreement() {
        Boolean isShowStateAidAgreed = true;

        assertTrue(setupGetWithRestResultExpectations(baseUrl + "/show-state-aid", Boolean.class,
            isShowStateAidAgreed).getBody());
    }
}
