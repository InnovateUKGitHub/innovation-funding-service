package com.worth.ifs.testdata;

import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.authentication.service.IdentityProviderService;
import com.worth.ifs.commons.BaseIntegrationTest;
import com.worth.ifs.notifications.resource.Notification;
import com.worth.ifs.notifications.resource.NotificationMedium;
import com.worth.ifs.notifications.service.NotificationService;
import com.worth.ifs.user.resource.OrganisationTypeEnum;
import com.worth.ifs.user.transactional.RegistrationService;
import org.flywaydb.core.Flyway;
import org.junit.Before;
import org.junit.Test;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.testdata.BaseDataBuilder.COMP_ADMIN_EMAIL;
import static com.worth.ifs.testdata.BaseDataBuilder.INNOVATE_UK_ORG_NAME;
import static com.worth.ifs.testdata.CompetitionDataBuilder.newCompetitionData;
import static com.worth.ifs.testdata.ExternalUserDataBuilder.newExternalUserData;
import static com.worth.ifs.testdata.InternalUserDataBuilder.newInternalUserData;
import static com.worth.ifs.testdata.OrganisationDataBuilder.newOrganisationData;
import static com.worth.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static java.util.Arrays.asList;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    private GenericApplicationContext applicationContext;

    @Autowired
    private RegistrationService registrationService;

    private CompetitionDataBuilder competitionDataBuilder;

    private ExternalUserDataBuilder externalUserBuilder;

    private InternalUserDataBuilder internalUserBuilder;

    private OrganisationDataBuilder organisationBuilder;

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

        ServiceLocator serviceLocator = new ServiceLocator(applicationContext);

        competitionDataBuilder = newCompetitionData(serviceLocator).createCompetition();
        externalUserBuilder = newExternalUserData(serviceLocator);
        internalUserBuilder = newInternalUserData(serviceLocator);
        organisationBuilder = newOrganisationData(serviceLocator);
    }

    @Test
    public void test() {

        createOrganisations();
        createInternalUsers();
        createCompetitions();
        createExternalUsers();
    }

    private void createOrganisations() {
        organisationBuilder.
                createOrganisation(INNOVATE_UK_ORG_NAME, OrganisationTypeEnum.BUSINESS.getOrganisationTypeId()).
                withAddress(OrganisationAddressType.REGISTERED, "North Star House").
                build();
    }

    private void createInternalUsers() {
        internalUserBuilder.
                withRole(COMP_ADMIN).
                createPreRegistrationEntry(COMP_ADMIN_EMAIL).
                registerUser("John", "Doe").
                verifyEmail().
                build();
    }

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
                withNewMilestones().
                withOpenDate(LocalDateTime.of(2015, 3, 15, 9, 0, 0)).
                withSubmissionDate(LocalDateTime.of(2066, 9, 9, 9, 23, 59, 59)).
                withAssessorAcceptsDate(LocalDateTime.of(2066, 10, 29, 0, 0)).
                withFundersPanelDate(LocalDateTime.of(2066, 12, 31, 0, 0)).
                withAssessorEndDate(LocalDateTime.of(2067, 1, 10, 0, 0)).
                withSetupComplete().
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
