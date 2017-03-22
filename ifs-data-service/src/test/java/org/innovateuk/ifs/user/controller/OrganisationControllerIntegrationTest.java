package org.innovateuk.ifs.user.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.repository.AddressRepository;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.user.domain.OrganisationType;
import org.innovateuk.ifs.user.repository.OrganisationTypeRepository;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.Set;

import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.junit.Assert.*;

public class OrganisationControllerIntegrationTest extends BaseControllerIntegrationTest<OrganisationController> {

    @Autowired
    private OrganisationTypeRepository organisationTypeRepository;

    @Autowired
    private AddressRepository addressRepository;

    private static final String companyHouseId = "0123456789";
    private static final String companyName = "CompanyName1";

    @Before
    public void setLoggedInUserOnThread() {
        loginSteveSmith();
    }

    @Override
    @Autowired
    protected void setControllerUnderTest(OrganisationController controller) {
        this.controller = controller;
    }

    @Rollback
    @Test
    public void findByIdShouldReturnOrganisation() throws Exception {
        OrganisationResource org = controller.findById(2L).getSuccessObject();
        assertEquals("Worth Internet Systems", org.getName());

        loginPeteTom();
        org = controller.findById(6L).getSuccessObject();
        assertEquals("EGGS", org.getName());
    }

    @Rollback
    @Test
    public void testOrganisationType() throws Exception {

        OrganisationResource org = controller.findById(2L).getSuccessObject();
        OrganisationType organisationType = organisationTypeRepository.findOne(org.getOrganisationType());

        assertEquals("Business", organisationType.getName());

        loginPeteTom();
        org = controller.findById(6L).getSuccessObject();
        organisationType = organisationTypeRepository.findOne(org.getOrganisationType());
        assertEquals("Research", organisationType.getName());
    }

    @Rollback
    @Test
    public void testFindByApplicationId() throws Exception {
        Set<OrganisationResource> organisations = controller.findByApplicationId(1L).getSuccessObject();
        assertEquals("There should be 4 organisation in this application", 4, organisations.size());

        OrganisationResource organisation = controller.findById(3L).getSuccessObject();
        assertTrue("One of the organisations should be Empire Ltd", simpleMap(organisations, OrganisationResource::getId).contains(organisation.getId()));
    }

    private OrganisationResource createOrganisation(){
        OrganisationResource organisation = newOrganisationResource().
                withName(companyName).withCompanyHouseNumber(companyHouseId).build();
        return controller.create(organisation).getSuccessObject();
    }

    @Rollback
    @Test
    public void testCreate() throws Exception {

        loginSystemRegistrationUser();

        OrganisationResource organisationResource = createOrganisation();

        assertEquals(companyHouseId, organisationResource.getCompanyHouseNumber());
        assertEquals(companyName, organisationResource.getName());

        OrganisationResource org = controller.findById(organisationResource.getId()).getSuccessObject();
        assertEquals(companyHouseId, org.getCompanyHouseNumber());
        assertEquals(companyName, org.getName());
    }

    @Rollback
    @Test
    public void testCreateSecondConstructor() throws Exception {

        loginSystemRegistrationUser();

        OrganisationResource organisation = newOrganisationResource().
                withName(companyName).withCompanyHouseNumber(companyHouseId).
                build();

        OrganisationResource organisationResource = controller.create(organisation).getSuccessObject();

        flushAndClearSession();

        assertEquals(companyHouseId, organisationResource.getCompanyHouseNumber());
        assertEquals(companyName, organisationResource.getName());

        OrganisationResource org = controller.findById(organisationResource.getId()).getSuccessObject();
        assertEquals(companyHouseId, org.getCompanyHouseNumber());
        assertEquals(companyName, org.getName());
    }

    @Rollback
    @Test
    public void testAddAddress() throws Exception {

        loginSystemRegistrationUser();

        OrganisationResource organisationResource = createOrganisation();

        AddressResource addressResource = new AddressResource("Line1", "Line2",  "Line3", "town", "county", "postcode");
        controller.addAddress(organisationResource.getId(), OrganisationAddressType.OPERATING, addressResource);

        flushAndClearSession();

        OrganisationResource cleanOrganisation = controller.findById(organisationResource.getId()).getSuccessObject();
        assertEquals(1, cleanOrganisation.getAddresses().size());
        Long addressId = cleanOrganisation.getAddresses().get(0).getAddress().getId();
        Address address = addressRepository.findOne(addressId);

        assertEquals("Line1", address.getAddressLine1());
        assertEquals("Line2", address.getAddressLine2());
        assertEquals("Line3", address.getAddressLine3());
        assertEquals("town", address.getTown());
        assertEquals("postcode", address.getPostcode());
        assertEquals("county", address.getCounty());
    }

    @Rollback
    @Test
    public void testUpdateNameAndRegistration() throws Exception {

        loginSystemRegistrationUser();

        OrganisationResource organisationResource = createOrganisation();

        controller.updateNameAndRegistration(organisationResource.getId(), "Vitruvius Stonework Limited", "60674010");

        flushAndClearSession();

        OrganisationResource updatedOrganisation = controller.findById(organisationResource.getId()).getSuccessObject();

        assertEquals("Vitruvius Stonework Limited", updatedOrganisation.getName());
        assertEquals("60674010", updatedOrganisation.getCompanyHouseNumber());
    }
}
