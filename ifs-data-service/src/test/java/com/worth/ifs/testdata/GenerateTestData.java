package com.worth.ifs.testdata;

import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.authentication.service.IdentityProviderService;
import com.worth.ifs.commons.BaseIntegrationTest;
import com.worth.ifs.email.resource.EmailAddress;
import com.worth.ifs.email.service.EmailService;
import com.worth.ifs.notifications.service.senders.NotificationSender;
import com.worth.ifs.notifications.service.senders.email.EmailNotificationSender;
import com.worth.ifs.user.repository.OrganisationRepository;
import com.worth.ifs.user.resource.OrganisationSize;
import com.worth.ifs.user.resource.OrganisationTypeEnum;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import com.worth.ifs.user.transactional.RegistrationService;
import com.worth.ifs.user.transactional.UserService;
import org.apache.commons.lang3.tuple.Pair;
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
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
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

    @Autowired
    private NotificationSender emailNotificationSender;

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

        IdentityProviderService idpServiceMock = mock(IdentityProviderService.class);
        EmailService emailServiceMock = mock(EmailService.class);

        when(idpServiceMock.createUserRecordWithUid(isA(String.class), isA(String.class))).thenAnswer(
                user -> serviceSuccess(UUID.randomUUID().toString()));

        when(emailServiceMock.sendEmail(isA(EmailAddress.class), isA(List.class), isA(String.class), isA(String.class), isA(String.class))).
                thenReturn(serviceSuccess(emptyList()));

        RegistrationService registrationServiceUnwrapped = (RegistrationService) unwrapProxy(registrationService);
        ReflectionTestUtils.setField(registrationServiceUnwrapped, "idpService", idpServiceMock);

        EmailNotificationSender notificationSenderUnwrapped = (EmailNotificationSender) unwrapProxy(emailNotificationSender);
        ReflectionTestUtils.setField(notificationSenderUnwrapped, "emailService", emailServiceMock);

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
     * select "Competition", "Application", "Question", "Answer", "File upload", "Answered by", "Assigned to", "Marked as complete" UNION ALL select c.name, a.name, q.name, fir.value, fir.file_entry_id, updater.email, assignee.email, qs.marked_as_complete from competition c join application a on a.competition = c.id join question q on q.competition_id = c.id join form_input fi on fi.question_id = q.id join form_input_type fit on fi.form_input_type_id = fit.id left join form_input_response fir on fir.form_input_id = fi.id left join process_role updaterrole on updaterrole.id = fir.updated_by_id left join user updater on updater.id = updaterrole.user_id join question_status qs on qs.application_id = a.id and qs.question_id = q.id left join process_role assigneerole on assigneerole.id = qs.assignee_id left join user assignee on assignee.id = assigneerole.user_id where fit.title in ('textinput','textarea','date','fileupload','percentage') INTO OUTFILE '/var/lib/mysql-files/application-questions3.csv' FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\n';
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
        createCompetitionWithApplications(line, Optional.of(1L));
    }

    private void createInAssessmentCompetition(CsvUtils.CompetitionLine line) {
        createCompetitionWithApplications(line, Optional.empty());
    }

    private void createFundersPanelCompetition(CsvUtils.CompetitionLine line) {
        createCompetitionWithApplications(line, Optional.empty());
    }

    private void createInAssessorFeedbackCompetition(CsvUtils.CompetitionLine line) {
        createCompetitionWithApplications(line, Optional.empty());
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
                        withQuestionResponse("Project summary", PROJECT_SUMMARY).
                        withQuestionResponse("Public description", PUBLIC_DESCRIPTION).
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
        createCompetitionWithApplications(line, Optional.empty());
    }

    private void createInPreparationCompetition(CsvUtils.CompetitionLine line) {
        createCompetitionWithApplications(line, Optional.empty());
    }

    private void createCompetitionWithApplications(CompetitionLine competitionLine, Optional<Long> competitionId) {
        CompetitionDataBuilder basicCompetitionInformation = competitionBuilderWithBasicInformation(competitionLine, competitionId);

        List<ApplicationLine> applicationLines = readApplications();

        List<ApplicationLine> competitionApplications = simpleFilter(applicationLines, app -> app.competitionName.equals(competitionLine.name));

        List<Function<ApplicationDataBuilder, ApplicationDataBuilder>> applicationBuilders = simpleMap(competitionApplications,
                applicationLine -> builder -> createApplicationFromCsv(builder, applicationLine).
                        withQuestionResponses(questionResponsesFromCsv(competitionLine.name, applicationLine.title)));

        if (applicationBuilders.isEmpty()) {
            basicCompetitionInformation.build();
        } else {
            basicCompetitionInformation.
                    moveCompetitionIntoOpenStatus().
                    withApplications(applicationBuilders.toArray(new Function[]{})).
                    restoreOriginalMilestones().
                    build();
        }
    }

    /**
     * select "Competition", "Application", "Question", "Answer", "File upload", "Answered by", "Assigned to", "Marked as complete" UNION ALL select c.name, a.name, q.name, fir.value, fir.file_entry_id, updater.email, assignee.email, qs.marked_as_complete from competition c join application a on a.competition = c.id join question q on q.competition_id = c.id join form_input fi on fi.question_id = q.id join form_input_type fit on fi.form_input_type_id = fit.id left join form_input_response fir on fir.form_input_id = fi.id left join process_role updaterrole on updaterrole.id = fir.updated_by_id left join user updater on updater.id = updaterrole.user_id left join question_status qs on qs.application_id = a.id and qs.question_id = q.id left join process_role assigneerole on assigneerole.id = qs.assignee_id left join user assignee on assignee.id = assigneerole.user_id where fit.title in ('textinput','textarea','date','fileupload','percentage') order by 1,2,3 INTO OUTFILE '/var/lib/mysql-files/application-questions5.csv' FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\n';
     *
     * And applied search-and-replace for:
     *
     * How many balls can you juggle?
     What is the size of the potential market for your project?

     What do you wear when juggling?
     How will you exploit and market your project?

     What is your preferred juggling pattern?
     What economic, social and environmental benefits do you expect your project to deliver and when?

     What mediums can you juggle with?
     What technical approach will you use and how will you manage your project?

     Fifth Question
     What is innovative about your project?
     
     */
    private List<Pair<String, String>> questionResponsesFromCsv(String competitionName, String applicationName) {

        List<CsvUtils.ApplicationQuestionResponseLine> responses = readApplicationQuestionResponses();

        List<CsvUtils.ApplicationQuestionResponseLine> responsesForApplication =
                simpleFilter(responses, r -> r.competitionName.equals(competitionName) && r.applicationName.equals(applicationName));

        return simpleMap(responsesForApplication, line -> Pair.of(line.questionName, line.value));
    }

    private ApplicationDataBuilder createApplicationFromCsv(ApplicationDataBuilder builder, ApplicationLine  line) {

        UserResource leadApplicant = retrieveUserByEmail(line.leadApplicant);

        ApplicationDataBuilder baseBuilder = builder.
                withBasicDetails(leadApplicant, line.title).
                withQuestionResponse("Project summary", PROJECT_SUMMARY).
                withQuestionResponse("Public description", PUBLIC_DESCRIPTION).
                withStartDate(line.startDate).
                withDurationInMonths(line.durationInMonths);

        for (String collaborator : line.collaborators) {
            baseBuilder = baseBuilder.inviteCollaborator(retrieveUserByEmail(collaborator));
        }

        return line.submittedDate != null ? baseBuilder.submitApplication() : baseBuilder;
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
