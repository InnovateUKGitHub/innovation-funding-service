package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.junit.Assert.*;

public class CompaniesHouseControllerIntegrationTest extends BaseControllerIntegrationTest<CompaniesHouseController> {

    private static final String COMPANY_ID = "04658986";
    private static final String COMPANY_NAME = "POLYMORPHISM LIMITED";

    @Override
    @Autowired
    protected void setControllerUnderTest(CompaniesHouseController controller) {
        this.controller = controller;
    }

    @Before
    public void setUp() throws Exception {
        loginSystemRegistrationUser();
    }

    @Test
    public void testSearch_companyHouseName() {
        RestResult<List<OrganisationSearchResult>> companies = controller.search(COMPANY_NAME);
        assertTrue(companies.isSuccess());
        assertEquals(1, companies.getSuccess().size());
    }

    @Test
    public void testSearch_companyHouseNumber() {
        RestResult<List<OrganisationSearchResult>> companies = controller.search(COMPANY_ID);
        assertTrue(companies.isSuccess());
        assertEquals(1, companies.getSuccess().size());
        OrganisationSearchResult company = companies.getSuccess().get(0);

        assertNotNull(company);
        assertThat(company.getName(), equalToIgnoringWhiteSpace(COMPANY_NAME));
        assertEquals(COMPANY_ID, company.getOrganisationSearchId());
        assertThat(company.getOrganisationAddress().getAddressLine1(), equalToIgnoringCase("Sheffield Digital Campus"));
        assertNull(company.getOrganisationAddress().getAddressLine2());
        assertThat(company.getOrganisationAddress().getTown(), equalToIgnoringCase("Sheffield"));
        assertNull(company.getOrganisationAddress().getCounty());
        assertThat(company.getOrganisationAddress().getPostcode(), equalToIgnoringCase("S1 2BJ"));
    }

    @Test
    public void testGetCompany() {
        RestResult<OrganisationSearchResult> companyResult = controller.getCompany(COMPANY_ID);
        assertTrue(companyResult.isSuccess());
        OrganisationSearchResult company = companyResult.getSuccess();

        assertNotNull(company);
        assertEquals(COMPANY_NAME, company.getName());
        assertEquals(COMPANY_ID, company.getOrganisationSearchId());
        assertThat(company.getOrganisationAddress().getAddressLine1(), equalToIgnoringCase("Electric Works"));
        assertThat(company.getOrganisationAddress().getAddressLine2(), equalToIgnoringCase("Sheffield Digital Campus"));
        assertThat(company.getOrganisationAddress().getTown(), equalToIgnoringCase("Sheffield"));
        assertNull(company.getOrganisationAddress().getCounty());
        assertThat(company.getOrganisationAddress().getPostcode(), equalToIgnoringCase("S1 2BJ"));
    }

    @Test
    public void testGetCompany_notExists() {
        RestResult<OrganisationSearchResult> company = controller.getCompany(String.valueOf(Integer.MAX_VALUE));
        assertTrue(company.isFailure());
    }
}