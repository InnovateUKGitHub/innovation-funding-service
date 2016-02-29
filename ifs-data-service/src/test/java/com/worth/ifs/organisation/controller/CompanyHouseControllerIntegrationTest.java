package com.worth.ifs.organisation.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
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
//        assertEquals("ltd", company.getType());
        assertThat("MONTROSE HOUSE", IsEqualIgnoringCase.equalToIgnoringCase(company.getOrganisationAddress().getAddressLine1()));
        assertThat("Clayhill Park", IsEqualIgnoringCase.equalToIgnoringCase(company.getOrganisationAddress().getAddressLine2()));
        assertThat("NESTON", IsEqualIgnoringCase.equalToIgnoringCase(company.getOrganisationAddress().getLocality()));
        assertThat("Cheshire", IsEqualIgnoringCase.equalToIgnoringCase(company.getOrganisationAddress().getRegion()));
        assertThat("CH64 3RU", IsEqualIgnoringCase.equalToIgnoringCase(company.getOrganisationAddress().getPostalCode()));
    }

    @Test
    public void testGetCompanyHouse() throws Exception {
        RestResult<OrganisationSearchResult> companyResult = controller.getCompanyHouse(COMPANY_ID);
        assertTrue(companyResult.isSuccess());
        OrganisationSearchResult company = companyResult.getSuccessObject();

        assertNotNull(company);
        assertEquals(COMPANY_NAME, company.getName());
        assertEquals(COMPANY_ID, company.getOrganisationSearchId());
//        assertEquals("ltd", company.getType());
        assertEquals("Montrose House", company.getOrganisationAddress().getAddressLine1());
        assertEquals("Clayhill Park", company.getOrganisationAddress().getAddressLine2());
        assertEquals("Neston", company.getOrganisationAddress().getLocality());
        assertEquals("Cheshire", company.getOrganisationAddress().getRegion());
        assertEquals("CH64 3RU", company.getOrganisationAddress().getPostalCode());
    }

    @Test
    public void testGetCompanyHouseNotExisting() throws Exception {
        RestResult<OrganisationSearchResult> company = controller.getCompanyHouse(String.valueOf(Integer.MAX_VALUE));
        assertTrue(company.isFailure());
    }
}