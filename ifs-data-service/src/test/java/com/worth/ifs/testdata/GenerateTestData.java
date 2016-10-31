package com.worth.ifs.testdata;

import com.worth.ifs.category.repository.CategoryRepository;
import com.worth.ifs.commons.BaseIntegrationTest;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.transactional.CompetitionSetupService;
import com.worth.ifs.user.domain.User;
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
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static com.worth.ifs.category.resource.CategoryType.INNOVATION_AREA;
import static com.worth.ifs.category.resource.CategoryType.INNOVATION_SECTOR;
import static com.worth.ifs.category.resource.CategoryType.RESEARCH_CATEGORY;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.junit.Assert.fail;

@ActiveProfiles({"integration-test,seeding-db"})
public class GenerateTestData extends BaseIntegrationTest {

    public static final String COMP_ADMIN_EMAIL = "john.doe@innovateuk.test";
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

    @Before
    public void setup() throws Exception {
        freshDb();
    }

    @Test
    public void test() {

        createUsers();
        createCompetitions();
    }

    @SuppressWarnings("unused")
    private void createUsers() {

        User steveSmith = createUser("Steve", "Smith", "steve.smith@empire.com", APPLICANT);
        User johnDoe = createUser("John", "Doe", COMP_ADMIN_EMAIL, COMP_ADMIN);
    }

    private void createCompetitions() {

        loginCompAdmin();
        createCompetition1();
    }

    private void createCompetition1() {

        CompetitionResource newCompetition = competitionSetupService.
                create().
                getSuccessObjectOrThrowException();

        CompetitionResource newCompetitionDetails = newCompetitionResource().
                withId(newCompetition.getId()).
                withName("My new Competition").
                withInnovationArea(categoryRepository.findByType(INNOVATION_AREA).get(0).getId()).
                withInnovationSector(categoryRepository.findByType(INNOVATION_SECTOR).get(0).getId()).
                withResearchCategories(singleton(categoryRepository.findByType(RESEARCH_CATEGORY).get(0).getId())).
                build();

        competitionSetupService.update(newCompetition.getId(), newCompetitionDetails).getSuccessObjectOrThrowException();
    }

    private void loginCompAdmin() {
        setLoggedInUser(compAdminUser());
    }

    private UserResource compAdminUser() {
        return retrieveUserByEmail(COMP_ADMIN_EMAIL);
    }

    private UserResource retrieveUserByEmail(String emailAddress) {
        setLoggedInSystemRegistrarUser(systemRegistrarUser());
        return userService.findByEmail(emailAddress).getSuccessObjectOrThrowException();
    }

    private void setLoggedInSystemRegistrarUser(UserResource user) {
        setLoggedInUser(user);
    }

    private UserResource systemRegistrarUser() {
        return newUserResource().withRolesGlobal(newRoleResource().withType(SYSTEM_REGISTRATION_USER).build(1)).build();
    }

    private User createUser(String firstName, String lastName, String emailAddress, UserRoleType role) {
        User newUser = newUser().
                withFirstName(firstName).
                withLastName(lastName).
                withEmailAddress(emailAddress).
                withRoles(roleRepository.findByNameIn(singletonList(role.getName()))).
                withUid(UUID.randomUUID().toString()).
                build();

        return userRepository.save(newUser);
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
