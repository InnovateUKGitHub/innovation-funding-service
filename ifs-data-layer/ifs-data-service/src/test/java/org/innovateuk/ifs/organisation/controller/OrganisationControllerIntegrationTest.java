package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.address.repository.AddressRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.domain.OrganisationType;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.organisation.repository.OrganisationTypeRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.junit.Assert.*;

public class OrganisationControllerIntegrationTest extends BaseControllerIntegrationTest<OrganisationController> {

    @Autowired
    private OrganisationTypeRepository organisationTypeRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    private static final String companiesHouseId = "0123456789";
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
        OrganisationType organisationType = organisationTypeRepository.findById(org.getOrganisationType()).get();

        assertEquals("Business", organisationType.getName());

        loginPeteTom();
        org = controller.findById(6L).getSuccess();
        organisationType = organisationTypeRepository.findById(org.getOrganisationType()).get();
        assertEquals("Research", organisationType.getName());
    }

    @Rollback
    @Test
    public void getUkBasedOrganisations() throws Exception {
        Organisation organisation = new Organisation();
        organisation.setName("uk based");
        organisation.setInternational(false);
        Organisation savedOrganisation = organisationRepository.save(organisation);

        User user = new User();
        user.setUid(UUID.randomUUID().toString());
        User savedUser = userRepository.save(user);

        // Applications ID is leveraging flyway test data ideally this will not be the case when services split
        ProcessRole processRole = new ProcessRole(savedUser, 1L, Role.APPLICANT, savedOrganisation.getId());
        processRoleRepository.save(processRole);

        List<OrganisationResource> results = controller.getOrganisations(savedUser.getId(), false).getSuccess();

        assertEquals(1, results.size());
        assertEquals("uk based", results.get(0).getName());
        assertFalse(results.get(0).isInternational());
    }

    @Rollback
    @Test
    public void getInternationalBasedOrganisations() throws Exception {
        Organisation organisation = new Organisation();
        organisation.setName("international based");
        organisation.setInternational(true);
        Organisation savedOrganisation = organisationRepository.save(organisation);

        User user = new User();
        user.setUid(UUID.randomUUID().toString());
        User savedUser = userRepository.save(user);

        // Applications ID is leveraging flyway test data ideally this will not be the case when services split
        ProcessRole processRole = new ProcessRole(savedUser, 1L, Role.APPLICANT, savedOrganisation.getId());
        processRoleRepository.save(processRole);

        List<OrganisationResource> results = controller.getOrganisations(savedUser.getId(), true).getSuccess();

        assertEquals(1, results.size());
        assertEquals("international based", results.get(0).getName());
        assertTrue(results.get(0).isInternational());
    }

    @Rollback
    @Test
    public void doesNotShowUkBasedOrganisations_WhenInternationalSearchTrue() throws Exception {
        Organisation organisation = new Organisation();
        organisation.setName("uk based");
        organisation.setInternational(false);
        Organisation savedOrganisation = organisationRepository.save(organisation);

        User user = new User();
        user.setUid(UUID.randomUUID().toString());
        User savedUser = userRepository.save(user);

        // Applications ID is leveraging flyway test data ideally this will not be the case when services split
        ProcessRole processRole = new ProcessRole(savedUser, 1L, Role.APPLICANT, savedOrganisation.getId());
        processRoleRepository.save(processRole);

        List<OrganisationResource> results = controller.getOrganisations(savedUser.getId(), true).getSuccess();

        assertEquals(0, results.size());
    }

    @Rollback
    @Test
    public void doesNotShowInternationalBasedOrganisations_WhenInternationalSearchFalse() throws Exception {
        Organisation organisation = new Organisation();
        organisation.setName("international based");
        organisation.setInternational(true);
        Organisation savedOrganisation = organisationRepository.save(organisation);

        User user = new User();
        user.setUid(UUID.randomUUID().toString());
        User savedUser = userRepository.save(user);

        // Applications ID is leveraging flyway test data ideally this will not be the case when services split
        ProcessRole processRole = new ProcessRole(savedUser, 1L, Role.APPLICANT, savedOrganisation.getId());
        processRoleRepository.save(processRole);

        List<OrganisationResource> results = controller.getOrganisations(savedUser.getId(), false).getSuccess();

        assertEquals(0, results.size());
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
                withName(companyName).withCompaniesHouseNumber(companiesHouseId).build();
        return organisationService.create(organisation).getSuccess();
    }

    @Rollback
    @Test
    public void testCreate() throws Exception {

        loginSystemRegistrationUser();

        OrganisationResource organisationResource = createOrganisation();

        assertEquals(companiesHouseId, organisationResource.getCompaniesHouseNumber());
        assertEquals(companyName, organisationResource.getName());

        OrganisationResource org = controller.findById(organisationResource.getId()).getSuccess();
        assertEquals(companiesHouseId, org.getCompaniesHouseNumber());
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
