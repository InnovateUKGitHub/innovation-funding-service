package com.worth.ifs.testdata;

import com.worth.ifs.authentication.service.IdentityProviderService;
import com.worth.ifs.category.repository.CategoryRepository;
import com.worth.ifs.commons.BaseIntegrationTest;
import com.worth.ifs.competition.repository.CompetitionTypeRepository;
import com.worth.ifs.competition.transactional.CompetitionService;
import com.worth.ifs.competition.transactional.CompetitionSetupService;
import com.worth.ifs.invite.transactional.InviteService;
import com.worth.ifs.notifications.resource.Notification;
import com.worth.ifs.notifications.resource.NotificationMedium;
import com.worth.ifs.notifications.service.NotificationService;
import com.worth.ifs.organisation.transactional.OrganisationService;
import com.worth.ifs.token.repository.TokenRepository;
import com.worth.ifs.token.transactional.TokenService;
import com.worth.ifs.user.repository.CompAdminEmailRepository;
import com.worth.ifs.user.repository.OrganisationRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.repository.UserRepository;
import com.worth.ifs.user.transactional.RegistrationService;
import com.worth.ifs.user.transactional.UserService;
import org.flywaydb.core.Flyway;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.testdata.BaseDataBuilder.COMP_ADMIN_EMAIL;
import static com.worth.ifs.testdata.BaseDataBuilder.INNOVATE_UK_ORG_NAME;
import static com.worth.ifs.testdata.CompetitionDataBuilder.newCompetitionData;
import static com.worth.ifs.testdata.ExternalUserBuilder.newExternalUserData;
import static com.worth.ifs.testdata.InternalUserBuilder.newInternalUserData;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static java.util.Arrays.asList;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Ignore
@Configuration
@ActiveProfiles({"integration-test,seeding-db"})
public class GenerateTestData extends BaseIntegrationTest {

    @Value("${flyway.url}")
    private String databaseUrl;

    @Value("${flyway.user}")
    private String databaseUser;

    @Value("${flyway.password}")
    private String databasePassword;

    @Value("${flyway.locations}")
    private String locations;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionSetupService competitionSetupService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CompetitionTypeRepository competitionTypeRepository;

    @Autowired
    private CompAdminEmailRepository compAdminEmailRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private InviteService inviteService;

    @Autowired
    private OrganisationService organisationService;

    private CompetitionDataBuilder competitionDataBuilder;

    private ExternalUserBuilder externalUserBuilder;

    private InternalUserBuilder internalUserBuilder;

    @Before
    public void setup() throws Exception {
        freshDb();
    }

    @PostConstruct
    public void replaceExternalDependencies() {

        IdentityProviderService idpService = mock(IdentityProviderService.class);
        NotificationService notificationService = mock(NotificationService.class);

        when(idpService.createUserRecordWithUid(isA(String.class), isA(String.class))).thenAnswer(
                user -> serviceSuccess(UUID.randomUUID().toString()));

        when(notificationService.sendNotification(isA(Notification.class), isA(NotificationMedium.class))).thenReturn(serviceSuccess());

        RegistrationService registrationServiceUnwrapped = (RegistrationService) unwrapProxy(registrationService);
        ReflectionTestUtils.setField(registrationServiceUnwrapped, "idpService", idpService);
        ReflectionTestUtils.setField(registrationServiceUnwrapped, "notificationService", notificationService);

        competitionDataBuilder = newCompetitionData(userService, competitionService, competitionTypeRepository,
                categoryRepository, competitionSetupService).createCompetition();

        externalUserBuilder = newExternalUserData(userRepository, userService, registrationService, roleRepository, organisationRepository,
                tokenRepository, tokenService, inviteService, organisationService);

        internalUserBuilder = newInternalUserData(userRepository, userService, registrationService, roleRepository, organisationRepository,
                tokenRepository, tokenService, inviteService, compAdminEmailRepository);
    }

    @Test
    public void test() {

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
        internalUserBuilder.
                withRole(COMP_ADMIN).
                createPreRegistrationEntry(COMP_ADMIN_EMAIL).
                registerUser("John", "Doe").
                verifyEmail().
                build();
    }

    @SuppressWarnings("unused")
    private void createExternalUsers() {
        externalUserBuilder.
                registerUser("Steve", "Smith", "steve.smith@empire.com", "Empire Ltd").
                verifyEmail().
                build();
    }

    private void createCompetitions() {
        createCompetition1();
        createCompetition2();
    }

    private void createCompetition1() {

        String name = "Connected digital additive manufacturing";

        String description = "Innovate UK is to invest up to £9 million in collaborative research and development to " +
                "stimulate innovation in integrated transport solutions for local authorities. The aim of this " +
                "competition is to meet user needs by connecting people and/or goods to transport products and " +
                "services. New or improved systems will be tested in environment laboratories.";

        competitionDataBuilder.
                withBasicData(name, description, "Programme", "Earth Observation", "Materials and manufacturing", "Technical feasibility").
                withApplicationFormFromTemplate().
                build();
    }

    private void createCompetition2() {

        String name = "Another Comp";

        String description = "Another desc.";

        competitionDataBuilder.
                withBasicData(name, description, "Programme", "Earth Observation", "Materials and manufacturing", "Technical feasibility").
                withApplicationFormFromTemplate().
                build();
    }

    private void freshDb() throws Exception {
        try {
            cleanAndMigrateDatabaseWithPatches(new String[] {"db/migration", "db/setup"});
        } catch (Exception e){
            fail("Exception thrown migrating with script directories: " + asList("db/migration", "db/setup") + e.getMessage());
        }
    }

    private Object unwrapProxy(Object services) {
        try {
            return unwrapProxies(asList(services)).get(0);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private List<Object> unwrapProxies(Collection<Object> services) {
        List<Object> unwrappedProxies = new ArrayList<>();
        for (Object service : services) {
            if (AopUtils.isJdkDynamicProxy(service)) {
                try {
                    unwrappedProxies.add(((Advised) service).getTargetSource().getTarget());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                unwrappedProxies.add(service);
            }
        }
        return unwrappedProxies;
    }

    private void cleanAndMigrateDatabaseWithPatches(String[] patchLocations){
        Flyway f = new Flyway();
        f.setDataSource(databaseUrl, databaseUser, databasePassword);
        f.setLocations(patchLocations);
        f.clean();
        f.migrate();
    }
}
