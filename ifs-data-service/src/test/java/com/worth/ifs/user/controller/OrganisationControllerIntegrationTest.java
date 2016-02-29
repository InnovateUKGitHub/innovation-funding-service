package com.worth.ifs.user.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.user.domain.AddressType;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.OrganisationSize;
import com.worth.ifs.user.resource.OrganisationResource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrganisationControllerIntegrationTest  extends BaseControllerIntegrationTest<OrganisationController> {

    String companyHouseId = "0123456789";
    String companyName = "CompanyName1";

    @Override
    @Autowired
    protected void setControllerUnderTest(OrganisationController controller) {
        this.controller = controller;
    }

    @Rollback
    @Test
    public void findByIdShouldReturnOrganisation() throws Exception {
        Organisation org = controller.findById(1L).getSuccessObject();
        assertEquals("Nomensa", org.getName());

        org = controller.findById(2L).getSuccessObject();
        assertEquals("Worth Internet Systems", org.getName());

        org = controller.findById(5L).getSuccessObject();
        assertEquals("Manchester University", org.getName());
    }

    @Rollback
    @Test
    public void testOrganisationType() throws Exception {
        Organisation org = controller.findById(1L).getSuccessObject();
        assertEquals("Business", org.getOrganisationType().getName());

        org = controller.findById(2L).getSuccessObject();
        assertEquals("Business", org.getOrganisationType().getName());

        org = controller.findById(5L).getSuccessObject();
        assertEquals("Academic", org.getOrganisationType().getName());
        assertEquals("Research", org.getOrganisationType().getParentOrganisationType().getName());
    }

    @Rollback
    @Test
    public void testFindByApplicationId() throws Exception {
        Set<Organisation> organisations = controller.findByApplicationId(1L).getSuccessObject();
        assertEquals("There should be 4 organisation in this application", 4, organisations.size());

        Organisation organisation = controller.findById(2L).getSuccessObject();
        assertTrue("One of the organisations should be Worth Internet Systems", organisations.contains(organisation));
    }

    private OrganisationResource createOrganisation(){
        Organisation organisation = new Organisation();
        organisation.setName(companyName);
        organisation.setCompanyHouseNumber(companyHouseId);

        OrganisationResource organisationResource = controller.create(organisation).getSuccessObject();
        return organisationResource;
    }

    @Rollback
    @Test
    public void testCreate() throws Exception {
        OrganisationResource organisationResource = createOrganisation();


        assertEquals(companyHouseId, organisationResource.getCompanyHouseNumber());
        assertEquals(companyName, organisationResource.getName());

        Organisation org = controller.findById(organisationResource.getId()).getSuccessObject();
        assertEquals(companyHouseId, org.getCompanyHouseNumber());
        assertEquals(companyName, org.getName());
    }
    
    @Rollback
    @Test
    public void testCreateSecondConstructor() throws Exception {
        Organisation organisation = new Organisation(null, companyName, companyHouseId, OrganisationSize.LARGE);
        OrganisationResource organisationResource = controller.create(organisation).getSuccessObject();


        assertEquals(companyHouseId, organisationResource.getCompanyHouseNumber());
        assertEquals(companyName, organisationResource.getName());
        assertEquals(OrganisationSize.LARGE, organisationResource.getOrganisationSize());

        Organisation org = controller.findById(organisationResource.getId()).getSuccessObject();
        assertEquals(companyHouseId, org.getCompanyHouseNumber());
        assertEquals(companyName, org.getName());
        assertEquals(OrganisationSize.LARGE, org.getOrganisationSize());
    }

    @Rollback
    @Test
    public void testAddAddress() throws Exception {
        OrganisationResource organisationResource = createOrganisation();
        AddressResource addressResource = new AddressResource("Line1", "Line2",  "Line3", "town", "county", "postcode");
        controller.addAddress(organisationResource.getId(), AddressType.OPERATING, addressResource);

        Organisation cleanOrganisation = controller.findById(organisationResource.getId()).getSuccessObject();
        assertEquals(1, cleanOrganisation.getAddresses().size());
        assertEquals("Line1", cleanOrganisation.getAddresses().get(0).getAddress().getAddressLine1());
        assertEquals("Line2", cleanOrganisation.getAddresses().get(0).getAddress().getAddressLine2());
        assertEquals("Line3", cleanOrganisation.getAddresses().get(0).getAddress().getAddressLine3());
        assertEquals("town", cleanOrganisation.getAddresses().get(0).getAddress().getTown());
        assertEquals("postcode", cleanOrganisation.getAddresses().get(0).getAddress().getPostcode());
        assertEquals("county", cleanOrganisation.getAddresses().get(0).getAddress().getCounty());
    }
}