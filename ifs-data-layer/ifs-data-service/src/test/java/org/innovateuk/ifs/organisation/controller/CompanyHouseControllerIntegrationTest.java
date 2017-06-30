package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.hamcrest.text.IsEqualIgnoringCase;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;
@Ignore
public class CompanyHouseControllerIntegrationTest extends BaseControllerIntegrationTest<CompanyHouseController> {

    private String COMPANY_ID = "08241216";
    private String COMPANY_NAME = "NETWORTHNET LTD";

    @Override
    @Autowired
    protected void setControllerUnderTest(CompanyHouseController controller) {
        this.controller = controller;
    }

    @Test
    public void testSearchCompanyHouseName() throws Exception {
        RestResult<List<OrganisationSearchResult>> companies = controller.searchCompanyHouse("Batman Robin");
        assertTrue(companies.isSuccess());
        assertEquals(1, companies.getSuccessObject().size());
    }
    @Test
    public void testSearchCompanyHouseNumber() throws Exception {
        RestResult<List<OrganisationSearchResult>> companies = controller.searchCompanyHouse(COMPANY_ID);
        assertTrue(companies.isSuccess());
        assertEquals(1, companies.getSuccessObject().size());
        OrganisationSearchResult company = companies.getSuccessObject().get(0);

        assertNotNull(company);
        assertEquals(COMPANY_NAME, company.getName());
        assertEquals(COMPANY_ID, company.getOrganisationSearchId());
        assertThat("MONTROSE HOUSE", IsEqualIgnoringCase.equalToIgnoringCase(company.getOrganisationAddress().getAddressLine1()));
        assertThat("Clayhill Park", IsEqualIgnoringCase.equalToIgnoringCase(company.getOrganisationAddress().getAddressLine2()));
        assertThat("NESTON", IsEqualIgnoringCase.equalToIgnoringCase(company.getOrganisationAddress().getTown()));
        assertThat("Cheshire", IsEqualIgnoringCase.equalToIgnoringCase(company.getOrganisationAddress().getCounty()));
        assertThat("CH64 3RU", IsEqualIgnoringCase.equalToIgnoringCase(company.getOrganisationAddress().getPostcode()));
    }

    @Test
    public void testGetCompanyHouse() throws Exception {
        RestResult<OrganisationSearchResult> companyResult = controller.getCompanyHouse(COMPANY_ID);
        assertTrue(companyResult.isSuccess());
        OrganisationSearchResult company = companyResult.getSuccessObject();

        assertNotNull(company);
        assertEquals(COMPANY_NAME, company.getName());
        assertEquals(COMPANY_ID, company.getOrganisationSearchId());
        assertEquals("Montrose House", company.getOrganisationAddress().getAddressLine1());
        assertEquals("Clayhill Park", company.getOrganisationAddress().getAddressLine2());
        assertEquals("Neston", company.getOrganisationAddress().getTown());
        assertEquals("Cheshire", company.getOrganisationAddress().getCounty());
        assertEquals("CH64 3RU", company.getOrganisationAddress().getPostcode());
    }

    @Test
    public void testGetCompanyHouseNotExisting() throws Exception {
        RestResult<OrganisationSearchResult> company = controller.getCompanyHouse(String.valueOf(Integer.MAX_VALUE));
        assertTrue(company.isFailure());
    }
}
