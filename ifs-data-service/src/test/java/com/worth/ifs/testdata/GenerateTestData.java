package com.worth.ifs.testdata;

import com.worth.ifs.category.domain.Category;
import com.worth.ifs.category.repository.CategoryRepository;
import com.worth.ifs.commons.BaseIntegrationTest;
import com.worth.ifs.competition.domain.CompetitionType;
import com.worth.ifs.competition.repository.CompetitionTypeRepository;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.transactional.CompetitionSetupService;
import com.worth.ifs.user.domain.CompAdminEmail;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.repository.CompAdminEmailRepository;
import com.worth.ifs.user.repository.OrganisationRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.repository.UserRepository;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import com.worth.ifs.user.transactional.UserService;
import org.flywaydb.core.Flyway;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static com.worth.ifs.category.resource.CategoryType.*;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.*;
import static com.worth.ifs.util.CollectionFunctions.simpleFindFirst;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.junit.Assert.fail;

@Configuration
@ActiveProfiles({"integration-test,seeding-db"})
public class GenerateTestData extends BaseIntegrationTest {

    public static final String COMP_ADMIN_EMAIL = "john.doe@innovateuk.test";
    public static final String INNOVATE_UK_ORG_NAME = "Innovate UK";
    @Value("${flyway.url}")
    private String databaseUrl;

    @Value("${flyway.user}")
    private String databaseUser;

    @Value("${flyway.password}")
    private String databasePassword;

    @Value("${flyway.locations}")
    private String locations;

    @Autowired
    private CompetitionSetupService competitionSetupService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CompetitionTypeRepository competitionTypeRepository;

    @Autowired
    private CompAdminEmailRepository compAdminEmailRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Before
    public void setup() throws Exception {
        freshDb();
    }

    @Test
    public void createTestData() {

        createOrganisations();
        createInternalUsers();
        createExternalUsers();
        createCompetitions();
    }

    private void createOrganisations() {
        organisationRepository.save(newOrganisation().withName(INNOVATE_UK_ORG_NAME).build());
    }

    @SuppressWarnings("unused")
    private void createInternalUsers() {
        User johnDoe = createInternalUser("John", "Doe", COMP_ADMIN_EMAIL, COMP_ADMIN);
    }

    @SuppressWarnings("unused")
    private void createExternalUsers() {
        User steveSmith = createExternalUserViaRegistration("Steve", "Smith", "steve.smith@empire.com", APPLICANT);
    }

    private void createCompetitions() {

        doAsCompAdmin(() -> {
            createCompetition1();
        });
    }

    private void createCompetition1() {

        String name = "Connected digital additive manufacturing";

        String description = "Innovate UK is to invest up to £9 million in collaborative research and development to " +
                "stimulate innovation in integrated transport solutions for local authorities. The aim of this " +
                "competition is to meet user needs by connecting people and/or goods to transport products and " +
                "services. New or improved systems will be tested in environment laboratories.";

        CompetitionResource newCompetition = createBasicCompetitionDetails(name, description, "Programme", "Earth Observation", "Materials and manufacturing", "Technical feasibility");
        createApplicationFormForCompetition(newCompetition);
    }

    private void createApplicationFormForCompetition(CompetitionResource newCompetition) {
        competitionSetupService.initialiseFormForCompetitionType(newCompetition.getId(), newCompetition.getCompetitionType()).getSuccessObjectOrThrowException();
    }

    private CompetitionResource createBasicCompetitionDetails(String name, String description, String competitionTypeName, String innovationAreaName, String innovationSectorName, String researchCategoryName) {

        CompetitionType competitionType = competitionTypeRepository.findByName(competitionTypeName).get(0);
        Category innovationArea = simpleFindFirst(categoryRepository.findByType(INNOVATION_AREA), c -> innovationAreaName.equals(c.getName())).get();
        Category innovationSector = simpleFindFirst(categoryRepository.findByType(INNOVATION_SECTOR), c -> innovationSectorName.equals(c.getName())).get();
        Category researchCategory = simpleFindFirst(categoryRepository.findByType(RESEARCH_CATEGORY), c -> researchCategoryName.equals(c.getName())).get();

        CompetitionResource newCompetition = competitionSetupService.
                create().
                getSuccessObjectOrThrowException();

        CompetitionResource newCompetitionDetails = newCompetitionResource().
                withId(newCompetition.getId()).
                withName(name).
                withDescription(description).
                withInnovationArea(innovationArea.getId()).
                withInnovationSector(innovationSector.getId()).
                withResearchCategories(singleton(researchCategory.getId())).
                withMaxResearchRatio(30).
                withAcademicGrantClaimPercentage(0).
                withCompetitionType(competitionType.getId()).
                build();

        return competitionSetupService.update(newCompetition.getId(), newCompetitionDetails).getSuccessObjectOrThrowException();
    }

    private UserResource compAdminUser() {
        return retrieveUserByEmail(COMP_ADMIN_EMAIL);
    }

    private UserResource retrieveUserByEmail(String emailAddress) {
        return doAsSystemRegistrar(() -> userService.findByEmail(emailAddress).getSuccessObjectOrThrowException());
    }

    private UserResource systemRegistrarUser() {
        return newUserResource().withRolesGlobal(newRoleResource().withType(SYSTEM_REGISTRATION_USER).build(1)).build();
    }

    private User createInternalUser(String firstName, String lastName, String emailAddress, UserRoleType role) {

        switch (role) {
            case COMP_ADMIN: {
                CompAdminEmail preregistrationEntry = new CompAdminEmail();
                preregistrationEntry.setEmail(emailAddress);
                compAdminEmailRepository.save(preregistrationEntry);
            }
        }

        List<Role> rolesByName = roleRepository.findByNameIn(singletonList(role.getName()));

        User newUser = newUser().
                withFirstName(firstName).
                withLastName(lastName).
                withEmailAddress(emailAddress).
                withRoles(rolesByName).
                withUid(UUID.randomUUID().toString()).
                withOrganisations(singletonList(organisation(INNOVATE_UK_ORG_NAME))).
                build();

        return userRepository.save(newUser);
    }

    private <T> T doAsSystemRegistrar(Supplier<T> action) {
        return doAsUser(action, systemRegistrarUser());
    }

    private <T> T doAsCompAdmin(Supplier<T> action) {
        return doAsUser(action, compAdminUser());
    }

    private void doAsCompAdmin(Runnable action) {
        doAsUser(action, compAdminUser());
    }

    private <T> T doAsUser(Supplier<T> action, UserResource user) {
        UserResource currentUser = setLoggedInUser(user);
        try {
            return action.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            setLoggedInUser(currentUser);
        }
    }

    private void doAsUser(Runnable action, UserResource user) {
        UserResource currentUser = setLoggedInUser(user);
        try {
            action.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            setLoggedInUser(currentUser);
        }
    }

    private User createExternalUserViaRegistration(String firstName, String lastName, String emailAddress, UserRoleType role) {
        User newUser = newUser().
                withFirstName(firstName).
                withLastName(lastName).
                withEmailAddress(emailAddress).
                withRoles(roleRepository.findByNameIn(singletonList(role.getName()))).
                withUid(UUID.randomUUID().toString()).
                build();

        return userRepository.save(newUser);
    }

    private Organisation organisation(String name) {
        return organisationRepository.findOneByName(name);
    }

    private void freshDb() throws Exception {
        try {
            cleanAndMigrateDatabaseWithPatches(new String[] {"db/migration", "db/setup"});
        } catch (Exception e){
            fail("Exception thrown migrating with script directories: " + asList("db/migration", "db/setup") + e.getMessage());
        }
    }

    private void cleanAndMigrateDatabaseWithPatches(String[] patchLocations){
        Flyway f = new Flyway();
        f.setDataSource(databaseUrl, databaseUser, databasePassword);
        f.setLocations(patchLocations);
        f.clean();
        f.migrate();
    }
}
