package com.worth.ifs.organisation.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
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
    public void testCheckInternetConnection() throws Exception {
        List<CompanyHouseBusiness> companies = controller.searchCompanyHouse("");
        assertEquals(0, companies.size());
    }

    @Test
    public void testSearchCompanyHouse() throws Exception {
        List<CompanyHouseBusiness> companies = controller.searchCompanyHouse("Batman Robin");
        assertEquals(1, companies.size());
    }

    @Test
    public void testGetCompanyHouse() throws Exception {
        CompanyHouseBusiness company = controller.getCompanyHouse(COMPANY_ID);
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
        CompanyHouseBusiness company = controller.getCompanyHouse(String.valueOf(Integer.MAX_VALUE));
        assertNull(company);
    }
}