package com.worth.ifs.testdata;

import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.authentication.service.IdentityProviderService;
import com.worth.ifs.commons.BaseIntegrationTest;
import com.worth.ifs.notifications.resource.Notification;
import com.worth.ifs.notifications.resource.NotificationMedium;
import com.worth.ifs.notifications.service.NotificationService;
import com.worth.ifs.user.repository.OrganisationRepository;
import com.worth.ifs.user.resource.OrganisationSize;
import com.worth.ifs.user.resource.OrganisationTypeEnum;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import com.worth.ifs.user.transactional.RegistrationService;
import com.worth.ifs.user.transactional.UserService;
import org.flywaydb.core.Flyway;
import org.junit.Before;
import org.junit.Test;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.testdata.BaseDataBuilder.COMP_ADMIN_EMAIL;
import static com.worth.ifs.testdata.CompetitionDataBuilder.newCompetitionData;
import static com.worth.ifs.testdata.CsvUtils.*;
import static com.worth.ifs.testdata.ExternalUserDataBuilder.newExternalUserData;
import static com.worth.ifs.testdata.InternalUserDataBuilder.newInternalUserData;
import static com.worth.ifs.testdata.OrganisationDataBuilder.newOrganisationData;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.SYSTEM_REGISTRATION_USER;
import static java.util.Arrays.asList;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles({"integration-test,seeding-db"})
public class GenerateTestData extends BaseIntegrationTest {

    private static final String PROJECT_SUMMARY = "The Project aims to identify, isolate and correct an issue that has hindered progress in this field for a number of years. Identification will involve the university testing conditions to determine the exact circumstance of the Issue. Once Identification has been assured we will work to Isolate the issue but replicating the circumstances in which it occurs within a laboratory environment. After this we will work with our prototyping partner to create a tool to correct the issue. Once tested and certified this will be rolled out to mass production.";
    private static final String PUBLIC_DESCRIPTION = "Wastage in our industry can be attributed in no small part to one issue. To date businesses have been reluctant to tackle that problem and instead worked around it. That has stifled progress.\n" +
            "\n" +
            "The end result of our project will be a novel tool to manage the issue and substantially reduce the wastage caused by it.";


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

    @Autowired
    private OrganisationRepository organisationRepository;

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
        createInternalUsers();
        createExternalUsers();
        createCompetitions();
    }

    /**
     * select "Email","First name","Last name","Status","Organisation name","Organisation type","Organisation address line 1", "Line 2","Line3","Town or city","Postcode","County","Address Type" UNION ALL SELECT u.email, u.first_name, u.last_name, u.status, o.name, ot.name, a.address_line1, a.address_line2, a.address_line3, a.town, a.postcode, a.county, at.name from user u join user_organisation uo on uo.user_id = u.id join organisation o on o.id = uo.organisation_id join organisation_type ot on ot.id = o.organisation_type_id join user_role ur on ur.user_id = u.id and ur.role_id = 4 left join organisation_address oa on oa.organisation_id = o.id left join address a on oa.address_id = a.id left join address_type at on at.id = oa.address_type_id INTO OUTFILE '/var/lib/mysql-files/external-users8.csv' FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\n';
     */
    private void createExternalUsers() {
        List<CsvUtils.ExternalUserLine> userDetails = readExternalUsers();
        userDetails.forEach(line -> createUser(externalUserBuilder, line));
    }

    /**
     * select "Email","First name","Last name","Status","Organisation name","Organisation type","Organisation address line 1", "Line 2","Line3","Town or city","Postcode","County","Address Type","Role" UNION ALL SELECT u.email, u.first_name, u.last_name, u.status, o.name, ot.name, a.address_line1, a.address_line2, a.address_line3, a.town, a.postcode, a.county, at.name, r.name from user u join user_organisation uo on uo.user_id = u.id join organisation o on o.id = uo.organisation_id join organisation_type ot on ot.id = o.organisation_type_id left join organisation_address oa on oa.organisation_id = o.id left join address a on oa.address_id = a.id left join address_type at on at.id = oa.address_type_id join user_role ur on ur.user_id = u.id and ur.role_id != 4 join role r on ur.role_id = r.id INTO OUTFILE '/var/lib/mysql-files/internal-users3.csv' FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\n';
     **/
    private void createInternalUsers() {
        List<CsvUtils.InternalUserLine> userDetails = readInternalUsers();
        userDetails.forEach(line -> {

            InternalUserDataBuilder baseBuilder = internalUserBuilder.
                    withRole(UserRoleType.fromName(line.role)).
                    createPreRegistrationEntry(line.emailAddress);

            createUser(baseBuilder, line);
        });
    }

    /**
     * select "Competition name", "Description", "Type", "Innovation Area", "Innovation Sector", "Research Category", "Open date", "Submission Date", "Funders Panel Date", "Funders Panel End Date", "Assessor Accepts Date", "Assessor End Date","Setup Complete" UNION ALL select c.name, c.description, "Programme", IFNULL(innArea.NAME,"Earth Observation"), IFNULL(innSec.NAME,"Materials and manufacturing"), IFNULL(resCat.NAME,"Technical feasibility"), open.DATE, submit.DATE, funders.DATE, funderEnd.DATE, assessorAccept.DATE, assessorEnd.DATE, c.setup_complete from competition c left join category_link cl on cl.class_pk = c.id and cl.class_name = 'com.worth.ifs.competition.domain.Competition' left join category innArea on innArea.id = cl.category_id and innArea.type = 'INNOVATION_AREA' left join category innSec on innSec.id = cl.category_id and innSec.type = 'INNOVATION_SECTOR' left join category resCat on resCat.id = cl.category_id and resCat.type = 'RESEARCH_CATEGORY' left join milestone open on open.type = 'OPEN_DATE' and open.competition_id = c.id  left join milestone submit on submit.type = 'SUBMISSION_DATE' and submit.competition_id = c.id left join milestone funders on funders.type = 'FUNDERS_PANEL' and funders.competition_id = c.id  left join milestone funderEnd on funderEnd.type = 'NOTIFICATIONS' and funderEnd.competition_id = c.id  left join milestone assessorAccept on assessorAccept.type = 'ASSESSOR_ACCEPTS' and assessorAccept.competition_id = c.id  left join milestone assessorEnd on assessorEnd.type = 'ASSESSOR_DEADLINE' and assessorEnd.competition_id = c.id  INTO OUTFILE '/var/lib/mysql-files/competitions4.csv' FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\n';
     */
    private void createCompetitions() {
        List<CsvUtils.CompetitionLine> competitionLines = readCompetitions();
        createOpenCompetition(competitionLines.get(2));
        createInAssessmentCompetition(competitionLines.get(3));
        createFundersPanelCompetition(competitionLines.get(4));
        createInAssessorFeedbackCompetition(competitionLines.get(6));
        createInProjectSetupCompetition(competitionLines.get(7));
        createInPreparationCompetition(competitionLines.get(8));
        createReadyToOpenCompetition(competitionLines.get(5));
    }

    private void createOpenCompetition(CsvUtils.CompetitionLine line) {

        UserResource applicant1 = retrieveUserByEmail("steve.smith@empire.com");
        UserResource applicant2 = retrieveUserByEmail("jessica.doe@ludlow.co.uk");
        UserResource applicant3 = retrieveUserByEmail("worth.email.test+submit@gmail.com");
        UserResource applicant4 = retrieveUserByEmail("pete.tom@egg.com");
        UserResource applicant5 = retrieveUserByEmail("ewan+1@hiveit.co.uk");

        competitionBuilderWithBasicInformation(line, Optional.of(1L)).
                withApplications(
                    builder -> builder.
                            withBasicDetails(applicant1, "A novel solution to an old problem").
                            withProjectSummary(PROJECT_SUMMARY).
                            withPublicDescription(PUBLIC_DESCRIPTION).
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

    private void createInAssessmentCompetition(CsvUtils.CompetitionLine line) {
        competitionBuilderWithBasicInformation(line, Optional.empty()).build();
    }

    private void createFundersPanelCompetition(CsvUtils.CompetitionLine line) {
        competitionBuilderWithBasicInformation(line, Optional.empty()).build();
    }

    private void createInAssessorFeedbackCompetition(CsvUtils.CompetitionLine line) {
        competitionBuilderWithBasicInformation(line, Optional.empty()).build();
    }

    private void createInProjectSetupCompetition(CsvUtils.CompetitionLine line) {

        UserResource applicant1 = retrieveUserByEmail("steve.smith@empire.com");
        UserResource applicant2 = retrieveUserByEmail("jessica.doe@ludlow.co.uk");
        UserResource applicant4 = retrieveUserByEmail("pete.tom@egg.com");
        UserResource applicant5 = retrieveUserByEmail("ewan+1@hiveit.co.uk");

        competitionBuilderWithBasicInformation(line, Optional.empty()).
                moveCompetitionIntoOpenStatus().
                withApplications(
                    builder -> builder.
                        withBasicDetails(applicant1, "A novel solution to an old problem").
                        withProjectSummary(PROJECT_SUMMARY).
                        withPublicDescription(PUBLIC_DESCRIPTION).
                        withStartDate(LocalDate.of(2016, 3, 1)).
                        withDurationInMonths(51).
                        inviteCollaborator(applicant2).
                        inviteCollaborator(applicant4).
                        inviteCollaborator(applicant5).
                        withFinances(
                            finance -> finance.
                                withOrganisation("Empire Ltd").
                                withUser(applicant1).
                                withIndustrialCosts(
                                    costs -> costs.
                                        withWorkingDaysPerYear(123).
                                        withGrantClaim(456).
                                        withOtherFunding("Lottery", LocalDate.of(2016, 04, 01), bd("1234")).
                                        withLabourEntry("Role 1", 100, 200).
                                        withLabourEntry("Role 2", 200, 300).
                                        withLabourEntry("Role 3", 300, 365).
                                        withAdministrationSupportCostsCustomRate(25).
                                        withMaterials("Generator", bd("5010"), 10).
                                        withCapitalUsage(12, "Depreciating Stuff", true, bd("1060"), bd("600"), 60).
                                        withSubcontractingCost("Developers", "UK", "To develop stuff", bd("45000")).
                                        withTravelAndSubsistence("To visit colleagues", 15, bd("199")).
                                        withOtherCosts("Some more costs", bd("550")).
                                        withOrganisationSize(OrganisationSize.MEDIUM)
                                ),
                            finance -> finance.
                                withOrganisation("Ludlow").
                                withUser(applicant2).
                                withIndustrialCosts(
                                    costs -> costs.withLabourEntry("Role 1", 100, 200)),
                            finance -> finance.
                                withOrganisation("EGGS").
                                withUser(applicant4).
                                withAcademicCosts(
                                    costs -> costs.withLabourEntry("Role 1", 100, 200)),
                            finance -> finance.
                                withOrganisation("HIVE IT LIMITED").
                                withUser(applicant5).
                                withIndustrialCosts(
                                    costs -> costs.withLabourEntry("Role 1", 100, 200))
                        ).
                        submitApplication()
                ).
                moveCompetitionIntoFundersPanelStatus().
                sendFundingDecisions(FundingDecision.FUNDED).
                restoreOriginalMilestones().
                build();
    }

    private void createReadyToOpenCompetition(CsvUtils.CompetitionLine line) {
        competitionBuilderWithBasicInformation(line, Optional.empty()).build();
    }

    private void createInPreparationCompetition(CsvUtils.CompetitionLine line) {
        competitionBuilderWithBasicInformation(line, Optional.empty()).build();
    }

    private CompetitionDataBuilder competitionBuilderWithBasicInformation(CsvUtils.CompetitionLine line, Optional<Long> existingCompetitionId) {

        CompetitionDataBuilder basicInformation =
                existingCompetitionId.map(id -> competitionDataBuilder.
                        withExistingCompetition(1L).
                        withBasicData(line.name, line.description, line.type, line.innovationArea, line.innovationSector, line.researchCategory)
                ).orElse(competitionDataBuilder.
                        createCompetition().
                        withBasicData(line.name, line.description, line.type, line.innovationArea, line.innovationSector, line.researchCategory).
                        withApplicationFormFromTemplate().
                        withNewMilestones()).
                withOpenDate(line.openDate).
                withSubmissionDate(line.submissionDate).
                withFundersPanelDate(line.fundersPanelDate).
                withFundersPanelEndDate(line.fundersPanelEndDate).
                withAssessorAcceptsDate(line.assessorAcceptsDate).
                withAssessorEndDate(line.assessorEndDate);

        return line.setupComplete ? basicInformation.withSetupComplete() : basicInformation;
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

    private <T extends BaseUserData, S extends BaseUserDataBuilder<T, S>> void createUser(S baseBuilder, CsvUtils.UserLine line) {

        Function<S, S> creatOrgIfNecessary = builder -> {

            boolean newOrganisation = organisationRepository.findOneByName(line.organisationName) == null;

            if (!newOrganisation) {
                return builder;
            }

            OrganisationDataBuilder organisation = organisationBuilder.
                    createOrganisation(line.organisationName, lookupOrganisationType(line.organisationType));

            if (line.addressType != null) {
                organisation = organisation.withAddress(line.addressType, line.addressLine1, line.addressLine2,
                        line.addressLine3, line.town, line.postcode, line.county);
            }

            return builder.withNewOrganisation(organisation);
        };

        Function<S, S> registerUser = builder ->
                builder.registerUser(line.firstName, line.lastName, line.emailAddress, line.organisationName);

        Function<S, S> verifyEmailIfNecessary = builder ->
                line.emailVerified ? builder.verifyEmail() : builder;

        creatOrgIfNecessary.andThen(registerUser).andThen(verifyEmailIfNecessary).apply(baseBuilder).build();
    }

    private OrganisationTypeEnum lookupOrganisationType(String organisationType) {
        switch (organisationType) {
            case "University (HEI)" : return OrganisationTypeEnum.ACADEMIC;
            default : return OrganisationTypeEnum.valueOf(organisationType.toUpperCase().replace(" ", "_"));
        }
    }


    private BigDecimal bd(String value) {
        return new BigDecimal(value);
    }
}
