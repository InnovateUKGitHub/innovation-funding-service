package com.worth.ifs.user.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.organisation.domain.Address;
import com.worth.ifs.user.domain.AddressType;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.OrganisationSize;
import com.worth.ifs.user.repository.OrganisationRepository;
import com.worth.ifs.user.resource.OrganisationResource;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.Set;

import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        Organisation org = controller.findById(1L);
        assertEquals("Nomensa", org.getName());

        org = controller.findById(2L);
        assertEquals("Worth Internet Systems", org.getName());
    }

    @Rollback
    @Test
    public void testFindByApplicationId() throws Exception {
        Set<Organisation> organisations = controller.findByApplicationId(1L);
        assertEquals("There should be 4 organisation in this application", 4, organisations.size());

        Organisation organisation = controller.findById(2L);
        assertTrue("One of the organisations should be Worth Internet Systems", organisations.contains(organisation));
    }

    private OrganisationResource createOrganisation(){
        Organisation organisation = new Organisation();
        organisation.setName(companyName);
        organisation.setCompanyHouseNumber(companyHouseId);

        OrganisationResource organisationResource = controller.create(organisation);
        return organisationResource;
    }

    @Rollback
    @Test
    public void testCreate() throws Exception {
        OrganisationResource organisationResource = createOrganisation();


        assertEquals(companyHouseId, organisationResource.getCompanyHouseNumber());
        assertEquals(companyName, organisationResource.getName());

        Organisation org = controller.findById(organisationResource.getId());
        assertEquals(companyHouseId, org.getCompanyHouseNumber());
        assertEquals(companyName, org.getName());
    }
    @Rollback
    @Test
    public void testCreateSecondConstructor() throws Exception {
        Organisation organisation = new Organisation(null, companyName, companyHouseId, OrganisationSize.LARGE);
        OrganisationResource organisationResource = controller.create(organisation);


        assertEquals(companyHouseId, organisationResource.getCompanyHouseNumber());
        assertEquals(companyName, organisationResource.getName());
        assertEquals(OrganisationSize.LARGE, organisationResource.getOrganisationSize());

        Organisation org = controller.findById(organisationResource.getId());
        assertEquals(companyHouseId, org.getCompanyHouseNumber());
        assertEquals(companyName, org.getName());
        assertEquals(OrganisationSize.LARGE, org.getOrganisationSize());
    }

    @Rollback
    @Test
    public void testAddAddress() throws Exception {
        OrganisationResource organisationResource = createOrganisation();
        Address address = new Address("Line1", "Line2", "careof", "Country", "locality", "po_box", "postal_code", "region");
        controller.addAddress(organisationResource.getId(), AddressType.OPERATING, address);


        Organisation cleanOrganisation = controller.findById(organisationResource.getId());
        assertEquals(1, cleanOrganisation.getAddresses().size());
        assertEquals("Line1", cleanOrganisation.getAddresses().get(0).getAddress().getAddressLine1());
        assertEquals("Line2", cleanOrganisation.getAddresses().get(0).getAddress().getAddressLine2());
        assertEquals("careof", cleanOrganisation.getAddresses().get(0).getAddress().getCareOf());
        assertEquals("Country", cleanOrganisation.getAddresses().get(0).getAddress().getCountry());
        assertEquals("locality", cleanOrganisation.getAddresses().get(0).getAddress().getLocality());
        assertEquals("po_box", cleanOrganisation.getAddresses().get(0).getAddress().getPoBox());
        assertEquals("postal_code", cleanOrganisation.getAddresses().get(0).getAddress().getPostalCode());
        assertEquals("region", cleanOrganisation.getAddresses().get(0).getAddress().getRegion());
    }
}