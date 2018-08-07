package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.address.repository.AddressRepository;
import org.innovateuk.ifs.organisation.domain.OrganisationType;
import org.innovateuk.ifs.organisation.repository.OrganisationTypeRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.Set;

import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        OrganisationResource org = controller.findById(2L).getSuccess();
        assertEquals("Worth Internet Systems", org.getName());

        loginPeteTom();
        org = controller.findById(6L).getSuccess();
        assertEquals("EGGS", org.getName());
    }

    @Rollback
    @Test
    public void testOrganisationType() throws Exception {

        OrganisationResource org = controller.findById(2L).getSuccess();
        OrganisationType organisationType = organisationTypeRepository.findOne(org.getOrganisationType());

        assertEquals("Business", organisationType.getName());

        loginPeteTom();
        org = controller.findById(6L).getSuccess();
        organisationType = organisationTypeRepository.findOne(org.getOrganisationType());
        assertEquals("Research", organisationType.getName());
    }

    @Rollback
    @Test
    public void testFindByApplicationId() throws Exception {
        Set<OrganisationResource> organisations = controller.findByApplicationId(1L).getSuccess();
        assertEquals("There should be 4 organisation in this application", 4, organisations.size());

        OrganisationResource organisation = controller.findById(3L).getSuccess();
        assertTrue("One of the organisations should be Empire Ltd", simpleMap(organisations, OrganisationResource::getId).contains(organisation.getId()));
    }

    private OrganisationResource createOrganisation(){
        OrganisationResource organisation = newOrganisationResource().
                withName(companyName).withCompaniesHouseNumber(companyHouseId).build();
        return controller.create(organisation).getSuccess();
    }

    @Rollback
    @Test
    public void testCreate() throws Exception {

        loginSystemRegistrationUser();

        OrganisationResource organisationResource = createOrganisation();

        assertEquals(companyHouseId, organisationResource.getCompaniesHouseNumber());
        assertEquals(companyName, organisationResource.getName());

        OrganisationResource org = controller.findById(organisationResource.getId()).getSuccess();
        assertEquals(companyHouseId, org.getCompaniesHouseNumber());
        assertEquals(companyName, org.getName());
    }

    @Rollback
    @Test
    public void testCreateSecondConstructor() throws Exception {

        loginSystemRegistrationUser();

        OrganisationResource organisation = newOrganisationResource().
                withName(companyName).withCompaniesHouseNumber(companyHouseId).
                build();

        OrganisationResource organisationResource = controller.create(organisation).getSuccess();

        flushAndClearSession();

        assertEquals(companyHouseId, organisationResource.getCompaniesHouseNumber());
        assertEquals(companyName, organisationResource.getName());

        OrganisationResource org = controller.findById(organisationResource.getId()).getSuccess();
        assertEquals(companyHouseId, org.getCompaniesHouseNumber());
        assertEquals(companyName, org.getName());
    }

    @Rollback
    @Test
    public void testUpdateNameAndRegistration() throws Exception {

        loginSystemRegistrationUser();

        OrganisationResource organisationResource = createOrganisation();

        controller.updateNameAndRegistration(organisationResource.getId(), "Vitruvius Stonework Limited", "60674010");

        flushAndClearSession();

        OrganisationResource updatedOrganisation = controller.findById(organisationResource.getId()).getSuccess();

        assertEquals("Vitruvius Stonework Limited", updatedOrganisation.getName());
        assertEquals("60674010", updatedOrganisation.getCompaniesHouseNumber());
    }
}
