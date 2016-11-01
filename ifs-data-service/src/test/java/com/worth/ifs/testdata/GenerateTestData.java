package com.worth.ifs.testdata;

import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.authentication.service.IdentityProviderService;
import com.worth.ifs.commons.BaseIntegrationTest;
import com.worth.ifs.notifications.resource.Notification;
import com.worth.ifs.notifications.resource.NotificationMedium;
import com.worth.ifs.notifications.service.NotificationService;
import com.worth.ifs.user.resource.OrganisationTypeEnum;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.transactional.RegistrationService;
import com.worth.ifs.user.transactional.UserService;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.testdata.BaseDataBuilder.COMP_ADMIN_EMAIL;
import static com.worth.ifs.testdata.BaseDataBuilder.INNOVATE_UK_ORG_NAME;
import static com.worth.ifs.testdata.CompetitionDataBuilder.newCompetitionData;
import static com.worth.ifs.testdata.ExternalUserDataBuilder.newExternalUserData;
import static com.worth.ifs.testdata.InternalUserDataBuilder.newInternalUserData;
import static com.worth.ifs.testdata.OrganisationDataBuilder.newOrganisationData;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static com.worth.ifs.user.resource.UserRoleType.SYSTEM_REGISTRATION_USER;
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

    @Autowired
    private UserService userService;

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

        competitionDataBuilder = newCompetitionData(serviceLocator);
        externalUserBuilder = newExternalUserData(serviceLocator);
        internalUserBuilder = newInternalUserData(serviceLocator);
        organisationBuilder = newOrganisationData(serviceLocator);
    }

    @Test
    public void test() {
        createBaseOrganisations();
        createInternalUsers();
        createExternalUsers();
        createCompetitions();
    }

    private void createBaseOrganisations() {
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
                registerUserWithNewOrganisation("Steve", "Smith", "steve.smith@empire.com", "Empire Ltd").
                verifyEmail().
                build();

        externalUserBuilder.
                registerUserWithExistingOrganisation("Malik", "Strandberg", "worth.email.test+submit@gmail.com", "Empire Ltd").
                verifyEmail().
                build();

        externalUserBuilder.
                registerUserWithNewOrganisation("Jessica", "Doe", "jessica.doe@ludlow.co.uk", "Ludlow").
                verifyEmail().
                build();

        externalUserBuilder.
                registerUserWithNewOrganisation("Pete", "Tom", "pete.tom@egg.com", "EGGS").
                verifyEmail().
                build();

        externalUserBuilder.
                registerUserWithNewOrganisation("Ewan", "Cormack", "ewan+1@hiveit.co.uk", "HIVE IT LIMITED").
                build();
    }

    private void createCompetitions() {
        createOpenCompetition();
        createInAssessmentCompetition();
        createFundersPanelCompetition();
        createInAssessorFeedbackCompetition();
        createInProjectSetupCompetition();
        createInPreparationCompetition();
        createReadyToOpenCompetition();
    }

    private void createOpenCompetition() {

        UserResource applicant1 = retrieveUserByEmail("steve.smith@empire.com");
        UserResource applicant2 = retrieveUserByEmail("jessica.doe@ludlow.co.uk");
        UserResource applicant3 = retrieveUserByEmail("worth.email.test+submit@gmail.com");
        UserResource applicant4 = retrieveUserByEmail("pete.tom@egg.com");
        UserResource applicant5 = retrieveUserByEmail("ewan+1@hiveit.co.uk");

        String name = "Connected digital additive manufacturing";

        String description = "Innovate UK is to invest up to £9 million in collaborative research and development to " +
                "stimulate innovation in integrated transport solutions for local authorities. The aim of this " +
                "competition is to meet user needs by connecting people and/or goods to transport products and " +
                "services. New or improved systems will be tested in environment laboratories.";

        competitionDataBuilder.
                withExistingCompetition(1L).
                withBasicData(name, description, "Programme", "Earth Observation", "Materials and manufacturing", "Technical feasibility").
                withOpenDate(LocalDateTime.of(2015, 3, 15, 9, 0, 0)).
                withSubmissionDate(LocalDateTime.of(2066, 9, 9, 9, 23, 59, 59)).
                withAssessorAcceptsDate(LocalDateTime.of(2066, 10, 29, 0, 0)).
                withFundersPanelDate(LocalDateTime.of(2066, 12, 31, 0, 0)).
                withAssessorEndDate(LocalDateTime.of(2067, 1, 10, 0, 0)).
                withSetupComplete().
                withApplications(
                    builder -> builder.
                            withBasicDetails(applicant1, "A novel solution to an old problem").
                            withStartDate(LocalDate.of(2016, 3, 1)).
                            withDurationInMonths(51).
                            inviteCollaborator(applicant2).
                            inviteCollaborator(applicant4).
                            inviteCollaborator(applicant5),
                    builder -> builder.
                            withBasicDetails(applicant1, "Providing sustainable childcare").
                            withStartDate(LocalDate.of(2015, 11, 1)).
                            withDurationInMonths(20).
                            inviteCollaborator(applicant2).
                            inviteCollaborator(applicant4),
                    builder -> builder.
                            withBasicDetails(applicant1, "Mobile Phone Data for Logistics Analysis").
                            withStartDate(LocalDate.of(2015, 11, 1)).
                            withDurationInMonths(10).
                            submitApplication(),
                    builder -> builder.
                            withBasicDetails(applicant1, "Using natural gas to heat homes").
                            withStartDate(LocalDate.of(2015, 11, 1)).
                            withDurationInMonths(43).
                            inviteCollaborator(applicant2).
                            submitApplication(),
                    builder -> builder.
                            withBasicDetails(applicant1, "A new innovative solution").
                            withStartDate(LocalDate.of(2015, 11, 1)).
                            withDurationInMonths(20).
                            inviteCollaborator(applicant2).
                            inviteCollaborator(applicant4).
                            submitApplication(),
                    builder -> builder.
                            withBasicDetails(applicant2, "Security for the Internet of Things").
                            withStartDate(LocalDate.of(2015, 11, 1)).
                            withDurationInMonths(23).
                            submitApplication(),
                    builder -> builder.
                            withBasicDetails(applicant3, "Marking it as complete").
                            withStartDate(LocalDate.of(2015, 11, 1)).
                            withDurationInMonths(23).
                            inviteCollaborator(applicant2)
                ).
                build();
    }

    private void createInAssessmentCompetition() {

        String name = "Juggling Craziness";

        String description = "Innovate UK is to invest up to £9 million in juggling. The aim of this competition is to make juggling even more fun.";

        competitionDataBuilder.
                createCompetition().
                withBasicData(name, description, "Programme", "Earth Observation", "Materials and manufacturing", "Technical feasibility").
                withApplicationFormFromTemplate().
                withNewMilestones().
                withOpenDate(LocalDateTime.of(2015, 6, 24, 0, 0)).
                withSubmissionDate(LocalDateTime.of(2016, 3, 16, 0, 0)).
                withFundersPanelDate(LocalDateTime.of(2016, 12, 31, 0, 0)).
                withAssessorAcceptsDate(LocalDateTime.of(2016, 1, 12, 0, 0)).
                withAssessorEndDate(LocalDateTime.of(2017, 1, 28, 0, 0)).
                withSetupComplete().
                build();
    }

    private void createFundersPanelCompetition() {

        String name = "La Fromage";

        String description = "Innovate UK is to invest up to £9 million in cheese. The aim of this competition is to make cheese tastier.";

        competitionDataBuilder.
                createCompetition().
                withBasicData(name, description, "Programme", "Earth Observation", "Materials and manufacturing", "Technical feasibility").
                withApplicationFormFromTemplate().
                withNewMilestones().
                withOpenDate(LocalDateTime.of(2015, 6, 24, 0, 0)).
                withSubmissionDate(LocalDateTime.of(2016, 3, 16, 0, 0)).
                withFundersPanelDate(LocalDateTime.of(2016, 4, 14, 0, 0)).
                withAssessorAcceptsDate(LocalDateTime.of(2016, 4, 12, 0, 0)).
                withAssessorEndDate(LocalDateTime.of(2017, 5, 12, 0, 0)).
                withSetupComplete().
                build();
    }

    private void createInAssessorFeedbackCompetition() {

        String name = "Theremin Theory";

        String description = "Innovate UK is to invest up to £9 million in theremins. The aim of this competition is to make theremin the prominent instrument in music.";

        competitionDataBuilder.
                createCompetition().
                withBasicData(name, description, "Programme", "Earth Observation", "Materials and manufacturing", "Technical feasibility").
                withApplicationFormFromTemplate().
                withNewMilestones().
                withOpenDate(LocalDateTime.of(2015, 6, 24, 0, 0)).
                withSubmissionDate(LocalDateTime.of(2016, 3, 16, 0, 0)).
                withFundersPanelDate(LocalDateTime.of(2016, 4, 14, 0, 0)).
                withFundersPanelEndDate(LocalDateTime.of(2016, 1, 28, 0, 0)).
                withAssessorAcceptsDate(LocalDateTime.of(2016, 1, 12, 0, 0)).
                withAssessorEndDate(LocalDateTime.of(2019, 1, 28, 0, 0)).
                withSetupComplete().
                build();
    }

    private void createInProjectSetupCompetition() {

        String name = "Killer Riffs";

        String description = "Innovate UK is to invest up to £9 million in heavy rock music. The aim of this competition is to make it so whenever you turn on the radio, you hear killer riffs and sick breakdowns.";

        competitionDataBuilder.
                createCompetition().
                withBasicData(name, description, "Programme", "Earth Observation", "Materials and manufacturing", "Technical feasibility").
                withApplicationFormFromTemplate().
                withNewMilestones().
                withOpenDate(LocalDateTime.of(2015, 6, 24, 0, 0)).
                withSubmissionDate(LocalDateTime.of(2016, 3, 16, 0, 0)).
                withFundersPanelDate(LocalDateTime.of(2016, 4, 14, 0, 0)).
                withFundersPanelEndDate(LocalDateTime.of(2016, 1, 28, 0, 0)).
                withAssessorAcceptsDate(LocalDateTime.of(2016, 1, 12, 0, 0)).
                withAssessorEndDate(LocalDateTime.of(2016, 1, 29, 0, 0)).
                withSetupComplete().
                build();
    }

    private void createReadyToOpenCompetition() {

        String name = "Sarcasm Stupendousness";

        String description = "Innovate UK is to invest up to £9 million in sarcasm. The aim of this competition is to make sarcasm such a huge deal.";

        competitionDataBuilder.
                createCompetition().
                withBasicData(name, description, "Programme", "Earth Observation", "Materials and manufacturing", "Technical feasibility").
                withApplicationFormFromTemplate().
                withNewMilestones().
                withOpenDate(LocalDateTime.of(2018, 2, 24, 0, 0)).
                withSubmissionDate(LocalDateTime.of(2018, 3, 16, 0, 0)).
                withFundersPanelDate(LocalDateTime.of(2018, 12, 31, 0, 0)).
                withAssessorAcceptsDate(LocalDateTime.of(2018, 1, 12, 0, 0)).
                withAssessorEndDate(LocalDateTime.of(2019, 1, 28, 0, 0)).
                withSetupComplete().
                build();
    }

    private void createInPreparationCompetition() {

        String name = null;
        String description = null;

        competitionDataBuilder.
                createCompetition().
                withBasicData(name, description, "Programme", "Earth Observation", "Materials and manufacturing", "Technical feasibility").
                withApplicationFormFromTemplate().
                withNewMilestones().
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

    protected UserResource compAdmin() {
        return retrieveUserByEmail(COMP_ADMIN_EMAIL);
    }

    protected UserResource retrieveUserByEmail(String emailAddress) {
        return doAs(systemRegistrar(), () -> userService.findByEmail(emailAddress).getSuccessObjectOrThrowException());
    }

    protected UserResource systemRegistrar() {
        return newUserResource().withRolesGlobal(newRoleResource().withType(SYSTEM_REGISTRATION_USER).build(1)).build();
    }

    private <T> T doAs(UserResource user, Supplier<T> action) {
        UserResource currentUser = setLoggedInUser(user);
        try {
            return action.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            setLoggedInUser(currentUser);
        }
    }

    private void doAs(UserResource user, Runnable action) {
        UserResource currentUser = setLoggedInUser(user);
        try {
            action.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            setLoggedInUser(currentUser);
        }
    }
}
