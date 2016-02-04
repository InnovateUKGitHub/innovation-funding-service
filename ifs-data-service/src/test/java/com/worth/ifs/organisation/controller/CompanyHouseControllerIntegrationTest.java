package com.worth.ifs.organisation.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.organisation.resource.CompanyHouseBusiness;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

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
        RestResult<List<CompanyHouseBusiness>> companies = controller.searchCompanyHouse("Batman Robin");
        assertTrue(companies.isSuccess());
        assertEquals(1, companies.getSuccess().getResult().size());
    }
    @Test
    public void testSearchCompanyHouseNumber() throws Exception {
        RestResult<List<CompanyHouseBusiness>> companies = controller.searchCompanyHouse(COMPANY_ID);
        assertTrue(companies.isSuccess());
        assertEquals(1, companies.getSuccess().getResult().size());
        CompanyHouseBusiness company = companies.getSuccess().getResult().get(0);

        assertNotNull(company);
        assertEquals(COMPANY_NAME, company.getName());
        assertEquals(COMPANY_ID, company.getCompanyNumber());
        assertEquals("ltd", company.getType());
        assertEquals("Montrose House", company.getOfficeAddress().getAddressLine1());
        assertEquals("Clayhill Park", company.getOfficeAddress().getAddressLine2());
        assertEquals("Neston", company.getOfficeAddress().getLocality());
        assertEquals("Cheshire", company.getOfficeAddress().getRegion());
        assertEquals("CH64 3RU", company.getOfficeAddress().getPostalCode());
    }

    @Test
    public void testGetCompanyHouse() throws Exception {
        RestResult<CompanyHouseBusiness> companyResult = controller.getCompanyHouse(COMPANY_ID);
        assertTrue(companyResult.isSuccess());
        CompanyHouseBusiness company = companyResult.getSuccess().getResult();

        assertNotNull(company);
        assertEquals(COMPANY_NAME, company.getName());
        assertEquals(COMPANY_ID, company.getCompanyNumber());
        assertEquals("ltd", company.getType());
        assertEquals("Montrose House", company.getOfficeAddress().getAddressLine1());
        assertEquals("Clayhill Park", company.getOfficeAddress().getAddressLine2());
        assertEquals("Neston", company.getOfficeAddress().getLocality());
        assertEquals("Cheshire", company.getOfficeAddress().getRegion());
        assertEquals("CH64 3RU", company.getOfficeAddress().getPostalCode());
    }

    @Test
    public void testGetCompanyHouseNotExisting() throws Exception {
        RestResult<CompanyHouseBusiness> company = controller.getCompanyHouse(String.valueOf(Integer.MAX_VALUE));
        assertTrue(company.isFailure());
    }
}