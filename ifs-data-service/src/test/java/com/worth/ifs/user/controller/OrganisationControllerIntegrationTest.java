package com.worth.ifs.user.controller;

import java.util.Set;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.address.domain.AddressType;
import com.worth.ifs.address.repository.AddressRepository;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.OrganisationSize;
import com.worth.ifs.user.domain.OrganisationType;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.mapper.UserMapper;
import com.worth.ifs.user.repository.OrganisationTypeRepository;
import com.worth.ifs.user.repository.UserRepository;
import com.worth.ifs.user.resource.OrganisationResource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrganisationControllerIntegrationTest extends BaseControllerIntegrationTest<OrganisationController> {

    private User user;

    @Autowired
    private OrganisationTypeRepository organisationTypeRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    UserMapper userMapper;

    private static final String companyHouseId = "0123456789";
    private static final String companyName = "CompanyName1";

    @Before
    public void setLoggedInUserOnThread() {
        user = userRepository.findByEmail("steve.smith@empire.com").get();
        setLoggedInUser(userMapper.mapToResource(user));
    }

    @Override
    @Autowired
    protected void setControllerUnderTest(OrganisationController controller) {
        this.controller = controller;
    }

    @Rollback
    @Test
    public void findByIdShouldReturnOrganisation() throws Exception {
        final OrganisationResource org = controller.findById(2L).getSuccessObject();
        assertEquals("Worth Internet Systems", org.getName());
    }

    @Rollback
    @Test
    public void testOrganisationType() throws Exception {
        OrganisationResource org = controller.findById(2L).getSuccessObject();
        OrganisationType organisationType = organisationTypeRepository.findOne(org.getOrganisationType());
        assertEquals("Business", organisationType.getName());
    }

    @Rollback
    @Test
    public void testFindByApplicationId() throws Exception {
        Set<OrganisationResource> organisations = controller.findByApplicationId(1L).getSuccessObject();
        assertEquals("There should be 5 organisation in this application", 5, organisations.size());

        OrganisationResource organisation = controller.findById(2L).getSuccessObject();
        assertTrue("One of the organisations should be Worth Internet Systems", simpleMap(organisations, OrganisationResource::getId).contains(organisation.getId()));
    }

    private OrganisationResource createOrganisation(){
        Organisation organisation = new Organisation();
        organisation.setName(companyName);
        organisation.setCompanyHouseNumber(companyHouseId);
        organisation.setUsers(singletonList(user));
        return controller.create(organisation).getSuccessObject();
    }

    @Rollback
    @Test
    public void testCreate() throws Exception {
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
        Organisation organisation = new Organisation(null, companyName, companyHouseId, OrganisationSize.LARGE);
        organisation.setUsers(singletonList(user));
        OrganisationResource organisationResource = controller.create(organisation).getSuccessObject();


        assertEquals(companyHouseId, organisationResource.getCompanyHouseNumber());
        assertEquals(companyName, organisationResource.getName());
        assertEquals(OrganisationSize.LARGE, organisationResource.getOrganisationSize());

        OrganisationResource org = controller.findById(organisationResource.getId()).getSuccessObject();
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

        OrganisationResource cleanOrganisation = controller.findById(organisationResource.getId()).getSuccessObject();
        assertEquals(1, cleanOrganisation.getAddresses().size());
        OrganisationAddressResource organisationAddressResource = cleanOrganisation.getAddresses().get(0);

        AddressResource address = organisationAddressResource.getAddress();

        assertEquals("Line1", address.getAddressLine1());
        assertEquals("Line2", address.getAddressLine2());
        assertEquals("Line3", address.getAddressLine3());
        assertEquals("town", address.getTown());
        assertEquals("postcode", address.getPostcode());
        assertEquals("county", address.getCounty());
    }
}
