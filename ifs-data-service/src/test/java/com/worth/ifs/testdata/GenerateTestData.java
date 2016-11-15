package com.worth.ifs.testdata;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.authentication.service.IdentityProviderService;
import com.worth.ifs.commons.BaseIntegrationTest;
import com.worth.ifs.email.resource.EmailAddress;
import com.worth.ifs.email.service.EmailService;
import com.worth.ifs.notifications.service.senders.NotificationSender;
import com.worth.ifs.notifications.service.senders.email.EmailNotificationSender;
import com.worth.ifs.organisation.transactional.OrganisationService;
import com.worth.ifs.testdata.builders.*;
import com.worth.ifs.testdata.builders.data.BaseUserData;
import com.worth.ifs.user.repository.OrganisationRepository;
import com.worth.ifs.user.resource.*;
import com.worth.ifs.user.transactional.RegistrationService;
import com.worth.ifs.user.transactional.UserService;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.Flyway;
import org.junit.Before;
import org.junit.BeforeClass;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.testdata.CsvUtils.*;
import static com.worth.ifs.testdata.builders.BaseDataBuilder.COMP_ADMIN_EMAIL;
import static com.worth.ifs.testdata.builders.CompetitionDataBuilder.newCompetitionData;
import static com.worth.ifs.testdata.builders.ExternalUserDataBuilder.newExternalUserData;
import static com.worth.ifs.testdata.builders.InternalUserDataBuilder.newInternalUserData;
import static com.worth.ifs.testdata.builders.OrganisationDataBuilder.newOrganisationData;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.COMP_EXEC;
import static com.worth.ifs.user.resource.UserRoleType.COMP_TECHNOLOGIST;
import static com.worth.ifs.user.resource.UserRoleType.SYSTEM_REGISTRATION_USER;
import static com.worth.ifs.util.CollectionFunctions.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles({"integration-test,seeding-db"})
public class GenerateTestData extends BaseIntegrationTest {

    private static final Log LOG = LogFactory.getLog(GenerateTestData.class);

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
    private OrganisationService organisationService;

    @Autowired
    private NotificationSender emailNotificationSender;

    private CompetitionDataBuilder competitionDataBuilder;

    private ExternalUserDataBuilder externalUserBuilder;

    private InternalUserDataBuilder internalUserBuilder;

    private OrganisationDataBuilder organisationBuilder;

    private static List<OrganisationLine> organisationLines;

    /**
     * select "Competition", "Application", "Question", "Answer", "File upload", "Answered by", "Assigned to", "Marked as complete" UNION ALL select c.name, a.name, q.name, fir.value, fir.file_entry_id, updater.email, assignee.email, qs.marked_as_complete from competition c join application a on a.competition = c.id join question q on q.competition_id = c.id join form_input fi on fi.question_id = q.id join form_input_type fit on fi.form_input_type_id = fit.id left join form_input_response fir on fir.form_input_id = fi.id left join process_role updaterrole on updaterrole.id = fir.updated_by_id left join user updater on updater.id = updaterrole.user_id join question_status qs on qs.application_id = a.id and qs.question_id = q.id left join process_role assigneerole on assigneerole.id = qs.assignee_id left join user assignee on assignee.id = assigneerole.user_id where fit.title in ('textinput','textarea','date','fileupload','percentage') INTO OUTFILE '/var/lib/mysql-files/application-questions3.csv' FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\n';
     */
    private static List<CompetitionLine> competitionLines;

    private static List<ApplicationLine> applicationLines;

    /**
     * select "Email", "Hash", "Name", "Status", "Type", "Target", "Owner" UNION ALL select i.email, i.hash, i.name, i.status, i.type, i.target_id, i.owner_id from invite i where not exists (select 1 from user where email = i.email) into OUTFILE '/tmp/invites3.csv' FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '';
     */
    private static List<InviteLine> inviteLines;

    /**
     * select "Email","First name","Last name","Status","Organisation name","Organisation type","Organisation address line 1", "Line 2","Line3","Town or city","Postcode","County","Address Type" UNION ALL SELECT u.email, u.first_name, u.last_name, u.status, o.name, ot.name, a.address_line1, a.address_line2, a.address_line3, a.town, a.postcode, a.county, at.name from user u join user_organisation uo on uo.user_id = u.id join organisation o on o.id = uo.organisation_id join organisation_type ot on ot.id = o.organisation_type_id join user_role ur on ur.user_id = u.id and ur.role_id = 4 left join organisation_address oa on oa.organisation_id = o.id left join address a on oa.address_id = a.id left join address_type at on at.id = oa.address_type_id INTO OUTFILE '/var/lib/mysql-files/external-users8.csv' FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\n';
     */
    private static List<ExternalUserLine> externalUserLines;

    /**
     * select "Email","First name","Last name","Status","Organisation name","Organisation type","Organisation address line 1", "Line 2","Line3","Town or city","Postcode","County","Address Type","Role" UNION ALL SELECT u.email, u.first_name, u.last_name, u.status, o.name, ot.name, a.address_line1, a.address_line2, a.address_line3, a.town, a.postcode, a.county, at.name, r.name from user u join user_organisation uo on uo.user_id = u.id join organisation o on o.id = uo.organisation_id join organisation_type ot on ot.id = o.organisation_type_id left join organisation_address oa on oa.organisation_id = o.id left join address a on oa.address_id = a.id left join address_type at on at.id = oa.address_type_id join user_role ur on ur.user_id = u.id and ur.role_id != 4 join role r on ur.role_id = r.id INTO OUTFILE '/var/lib/mysql-files/internal-users3.csv' FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\n';
     **/
    private static List<InternalUserLine> internalUserLines;

    /**
     * select "Competition", "Application", "Question", "Answer", "File upload", "Answered by", "Assigned to", "Marked as complete" UNION ALL select c.name, a.name, q.name, fir.value, fir.file_entry_id, updater.email, assignee.email, qs.marked_as_complete from competition c join application a on a.competition = c.id join question q on q.competition_id = c.id join form_input fi on fi.question_id = q.id join form_input_type fit on fi.form_input_type_id = fit.id left join form_input_response fir on fir.form_input_id = fi.id left join process_role updaterrole on updaterrole.id = fir.updated_by_id left join user updater on updater.id = updaterrole.user_id left join question_status qs on qs.application_id = a.id and qs.question_id = q.id left join process_role assigneerole on assigneerole.id = qs.assignee_id left join user assignee on assignee.id = assigneerole.user_id where fit.title in ('textinput','textarea','date','fileupload','percentage') order by 1,2,3 INTO OUTFILE '/var/lib/mysql-files/application-questions5.csv' FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\n';
     * <p>
     * And applied search-and-replace for:
     * <p>
     * How many balls can you juggle?
     * What is the size of the potential market for your project?
     * <p>
     * What do you wear when juggling?
     * How will you exploit and market your project?
     * <p>
     * What is your preferred juggling pattern?
     * What economic, social and environmental benefits do you expect your project to deliver and when?
     * <p>
     * What mediums can you juggle with?
     * What technical approach will you use and how will you manage your project?
     * <p>
     * Fifth Question
     * What is innovative about your project?
     */
    private static List<ApplicationQuestionResponseLine> questionResponseLines;

    /**
     * select "Competition name", "Application name", "Organisation name", "Category" union all select row.compname, row.appname, row.orgname, '' from (select distinct o.name as orgname, a.name as appname, c.name as compname, '' from competition c join application a on a.competition = c.id join process_role pr on pr.role_id in (1,2) and pr.application_id = a.id join organisation o on o.id = pr.organisation_id group by c.name, a.name, o.name) as row  INTO OUTFILE '/var/lib/mysql-files/application-finances.csv' FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\n';
     */
    private static List<ApplicationOrganisationFinanceBlock> applicationFinanceLines;

    @Before
    public void setup() throws Exception {
        freshDb();
    }

    @BeforeClass
    public static void readCsvs() throws Exception {
        organisationLines = readOrganisations();
        competitionLines = readCompetitions();
        applicationLines = readApplications();
        inviteLines = readInvites();
        externalUserLines = readExternalUsers();
        internalUserLines = readInternalUsers();
        questionResponseLines = readApplicationQuestionResponses();
        applicationFinanceLines = readApplicationFinances();
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
    }

    @PostConstruct
    public void setupBaseBuilders() {

        ServiceLocator serviceLocator = new ServiceLocator(applicationContext);

        competitionDataBuilder = newCompetitionData(serviceLocator);
        externalUserBuilder = newExternalUserData(serviceLocator);
        internalUserBuilder = newInternalUserData(serviceLocator);
        organisationBuilder = newOrganisationData(serviceLocator);
    }

    @Test
    public void generateTestData() {

        long before = System.currentTimeMillis();

        createInternalUsers();
        createExternalUsers();
        createCompetitions();

        long after = System.currentTimeMillis();

        LOG.info("Finished generating data in " + ((after - before) / 1000) + " seconds");
        System.out.println("Finished generating data in " + ((after - before) / 1000) + " seconds");
    }

    private void createExternalUsers() {
        externalUserLines.forEach(line -> createUser(externalUserBuilder, line, true));
    }

    private void createInternalUsers() {
        internalUserLines.forEach(line -> {

            UserRoleType role = UserRoleType.fromName(line.role);

            InternalUserDataBuilder baseBuilder = internalUserBuilder.
                    withRole(role).
                    createPreRegistrationEntry(line.emailAddress);

            createUser(baseBuilder, line, !asList(COMP_TECHNOLOGIST, COMP_EXEC).contains(role));
        });
    }

    private void createCompetitions() {

        competitionLines.forEach(line -> {
            if ("Connected digital additive manufacturing".equals(line.name)) {
                createCompetitionWithApplications(line, Optional.of(1L));
            } else {
                createCompetitionWithApplications(line, Optional.empty());
            }
        });
    }

    private List<Pair<String, FundingDecision>> createFundingDecisionsFromCsv(String competitionName) {
        List<ApplicationLine> matchingApplications = simpleFilter(applicationLines, a -> a.competitionName.equals(competitionName));
        List<ApplicationLine> applicationsWithDecisions = simpleFilter(matchingApplications, a -> asList(ApplicationStatusConstants.APPROVED, ApplicationStatusConstants.REJECTED).contains(a.status));
        return simpleMap(applicationsWithDecisions, ma -> Pair.of(ma.title, ma.status == ApplicationStatusConstants.APPROVED ? FundingDecision.FUNDED : FundingDecision.UNFUNDED));
    }

    private void createCompetitionWithApplications(CompetitionLine competitionLine, Optional<Long> competitionId) {

        CompetitionDataBuilder basicCompetitionInformation = competitionBuilderWithBasicInformation(competitionLine, competitionId);

        List<ApplicationLine> competitionApplications = simpleFilter(applicationLines, app -> app.competitionName.equals(competitionLine.name));

        List<UnaryOperator<ApplicationDataBuilder>> applicationBuilders = simpleMap(competitionApplications,
                applicationLine -> builder ->
                        createApplicationFromCsv(builder, applicationLine).
                                withQuestionResponses(questionResponsesFromCsv(competitionLine.name, applicationLine.title)));

        if (applicationBuilders.isEmpty()) {
            basicCompetitionInformation.build();
        } else {

            CompetitionDataBuilder withApplications = basicCompetitionInformation.
                    moveCompetitionIntoOpenStatus().
                    withApplications(applicationBuilders).
                    restoreOriginalMilestones();

            if (competitionLine.fundersPanelEndDate != null && competitionLine.fundersPanelEndDate.isBefore(LocalDateTime.now())) {

                withApplications = withApplications.
                        moveCompetitionIntoFundersPanelStatus().
                        sendFundingDecisions(createFundingDecisionsFromCsv(competitionLine.name)).
                        restoreOriginalMilestones();
            }

            withApplications.build();
        }
    }

    private List<UnaryOperator<ResponseDataBuilder>> questionResponsesFromCsv(String competitionName, String applicationName) {

        List<CsvUtils.ApplicationQuestionResponseLine> responsesForApplication =
                simpleFilter(questionResponseLines, r -> r.competitionName.equals(competitionName) && r.applicationName.equals(applicationName));

        return simpleMap(responsesForApplication, line -> baseBuilder -> {

            UnaryOperator<ResponseDataBuilder> withQuestion = builder -> builder.forQuestion(line.questionName);

            UnaryOperator<ResponseDataBuilder> answerIfNecessary = builder ->
                    !isBlank(line.value) ? builder.withAssignee(line.answeredBy).withAnswer(line.value, line.answeredBy)
                            : builder;

            UnaryOperator<ResponseDataBuilder> assignIfNecessary = builder ->
                    !isBlank(line.assignedTo) ? builder.withAssignee(line.assignedTo) : builder;

            UnaryOperator<ResponseDataBuilder> markAsCompleteIfNecessary = builder ->
                    line.markedAsComplete ? builder.markAsComplete() : builder;

            return withQuestion.andThen(answerIfNecessary).andThen(assignIfNecessary).andThen(markAsCompleteIfNecessary).
                    apply(baseBuilder);
        });
    }

    private ApplicationDataBuilder createApplicationFromCsv(ApplicationDataBuilder builder, ApplicationLine line) {

        UserResource leadApplicant = retrieveUserByEmail(line.leadApplicant);

        ApplicationDataBuilder baseBuilder = builder.
                withBasicDetails(leadApplicant, line.title).
                withStartDate(line.startDate).
                withDurationInMonths(line.durationInMonths);

        for (String collaborator : line.collaborators) {
            baseBuilder = baseBuilder.inviteCollaborator(retrieveUserByEmail(collaborator));
        }

        List<InviteLine> pendingInvites = simpleFilter(GenerateTestData.inviteLines,
                invite -> "APPLICATION".equals(invite.type) && line.title.equals(invite.targetName));

        for (InviteLine invite : pendingInvites) {
            baseBuilder = baseBuilder.inviteCollaboratorNotYetRegistered(invite.email, invite.hash, invite.name,
                    invite.status, invite.ownerName);
        }

        if (line.status != ApplicationStatusConstants.CREATED) {
            baseBuilder = baseBuilder.beginApplication();
        }

        if (line.submittedDate != null) {
            baseBuilder = baseBuilder.submitApplication();
        }

        List<String> applicants = combineLists(line.leadApplicant, line.collaborators);

        List<Triple<String, String, OrganisationTypeEnum>> organisations = simpleMap(applicants, email -> {
            UserResource user = retrieveUserByEmail(email);
            OrganisationResource organisation = retrieveOrganisationById(user.getOrganisations().get(0));
            return Triple.of(user.getEmail(), organisation.getName(), OrganisationTypeEnum.getFromId(organisation.getOrganisationType()));
        });

        List<UnaryOperator<ApplicationFinanceDataBuilder>> financeBuilders = simpleMap(organisations, orgDetails -> {

            String user = orgDetails.getLeft();
            String organisationName = orgDetails.getMiddle();
            OrganisationTypeEnum organisationType = orgDetails.getRight();

            Optional<ApplicationOrganisationFinanceBlock> organisationFinances = simpleFindFirst(applicationFinanceLines, finances ->
                    finances.competitionName.equals(line.competitionName) &&
                            finances.applicationName.equals(line.title) &&
                            finances.organisationName.equals(organisationName));

            if (organisationType.equals(OrganisationTypeEnum.ACADEMIC)) {

                if (organisationFinances.isPresent()) {
                    return generateAcademicFinancesFromSuppliedData(user, organisationName, organisationFinances.get());
                } else {
                    return generateAcademicFinances(user, organisationName);
                }
            } else {
                if (organisationFinances.isPresent()) {
                    return generateIndustrialCostsFromSuppliedData(user, organisationName, organisationFinances.get());
                } else {
                    return generateIndustrialCosts(user, organisationName);
                }
            }
        });

        return baseBuilder.withFinances(financeBuilders);
    }

    private UnaryOperator<ApplicationFinanceDataBuilder> generateIndustrialCostsFromSuppliedData(String user, String organisationName, ApplicationOrganisationFinanceBlock organisationFinances) {
        return finance -> {

            List<ApplicationFinanceRow> financeRows = organisationFinances.rows;

            ApplicationFinanceDataBuilder baseBuilder = finance.
                    withOrganisation(organisationName).
                    withUser(user);

            UnaryOperator<IndustrialCostDataBuilder> costBuilder = costs -> {

                IndustrialCostDataBuilder costsWithData = costs;

                for (ApplicationFinanceRow financeRow : financeRows) {
                    costsWithData = addFinanceRow(costsWithData, financeRow);
                }

                return costsWithData;
            };


            return baseBuilder.withIndustrialCosts(costBuilder);
        };
    }

    private IndustrialCostDataBuilder addFinanceRow(IndustrialCostDataBuilder builder, ApplicationFinanceRow financeRow) {

        switch (financeRow.category) {
            case "Working days per year": return builder.withWorkingDaysPerYear(Integer.valueOf(financeRow.metadata.get(0)));
            case "Grant claim": return builder.withGrantClaim(Integer.valueOf(financeRow.metadata.get(0)));
            case "Organisation size": return builder.withOrganisationSize(OrganisationSize.valueOf(financeRow.metadata.get(0).toUpperCase()));
            case "Labour": return builder.withLabourEntry(financeRow.metadata.get(0), Integer.valueOf(financeRow.metadata.get(1)), Integer.valueOf(financeRow.metadata.get(2)));
            case "Overheads": switch (financeRow.metadata.get(0).toLowerCase()) {
                case "custom": return builder.withAdministrationSupportCostsCustomRate(Integer.valueOf(financeRow.metadata.get(1)));
                case "default": return builder.withAdministrationSupportCostsDefaultRate();
                case "none": return builder.withAdministrationSupportCostsNone();
                default: throw new RuntimeException("Unknown rate type " + financeRow.metadata.get(0).toLowerCase());
            }
            case "Materials": return builder.withMaterials(financeRow.metadata.get(0), bd(financeRow.metadata.get(1)), Integer.valueOf(financeRow.metadata.get(2)));
            case "Capital usage": return builder.withCapitalUsage(Integer.valueOf(financeRow.metadata.get(4)),
                    financeRow.metadata.get(0), Boolean.parseBoolean(financeRow.metadata.get(1)),
                    bd(financeRow.metadata.get(2)), bd(financeRow.metadata.get(3)), Integer.valueOf(financeRow.metadata.get(5)));
            case "Subcontracting": return builder.withSubcontractingCost(financeRow.metadata.get(0), financeRow.metadata.get(1), financeRow.metadata.get(2), bd(financeRow.metadata.get(3)));
            case "Travel and subsistence": return builder.withTravelAndSubsistence(financeRow.metadata.get(0), Integer.valueOf(financeRow.metadata.get(1)), bd(financeRow.metadata.get(2)));
            case "Other costs": return builder.withOtherCosts(financeRow.metadata.get(0), bd(financeRow.metadata.get(1)));
            case "Other funding": return builder.withOtherFunding(financeRow.metadata.get(0), LocalDate.parse(financeRow.metadata.get(1), DATE_PATTERN), bd(financeRow.metadata.get(2)));
            default: throw new RuntimeException("Unknown category " + financeRow.category);
        }
    }

    private UnaryOperator<ApplicationFinanceDataBuilder> generateIndustrialCosts(String user, String organisationName) {
        return finance ->
                finance.withOrganisation(organisationName).
                        withUser(user).
                        withIndustrialCosts(
                                costs -> costs.
                                        withWorkingDaysPerYear(123).
                                        withGrantClaim(30).
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
                                        withOrganisationSize(OrganisationSize.MEDIUM));
    }

    private UnaryOperator<ApplicationFinanceDataBuilder> generateAcademicFinances(String user, String organisationName) {
        return finance -> finance.
                withOrganisation(organisationName).
                withUser(user).
                withAcademicCosts(costs -> costs.
                        withTsbReference("My REF").
                        withDirectlyIncurredStaff(bd("11")).
                        withDirectlyIncurredTravelAndSubsistence(bd("22")).
                        withDirectlyIncurredOtherCosts(bd("33")).
                        withDirectlyAllocatedInvestigators(bd("44")).
                        withDirectlyAllocatedEstateCosts(bd("55")).
                        withDirectlyAllocatedOtherCosts(bd("66")).
                        withIndirectCosts(bd("77")).
                        withExceptionsStaff(bd("88")).
                        withExceptionsOtherCosts(bd("99")));
    }

    private UnaryOperator<ApplicationFinanceDataBuilder> generateAcademicFinancesFromSuppliedData(String user, String organisationName, ApplicationOrganisationFinanceBlock existingFinances) {
        return finance -> finance.
                withOrganisation(organisationName).
                withUser(user).
                withAcademicCosts(costs -> costs.withTsbReference("My REF"));
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

    protected OrganisationResource retrieveOrganisationById(Long id) {
        return doAs(systemRegistrar(), () -> organisationService.findById(id).getSuccessObjectOrThrowException());
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

    private <T extends BaseUserData, S extends BaseUserDataBuilder<T, S>> void createUser(S baseBuilder, CsvUtils.UserLine line, boolean createViaRegistration) {

        Function<S, S> createOrgIfNecessary = builder -> {

            boolean newOrganisation = organisationRepository.findOneByName(line.organisationName) == null;

            if (!newOrganisation) {
                return builder;
            }

            OrganisationLine matchingOrganisationDetails = simpleFindFirst(organisationLines, orgLine -> orgLine.name.equals(line.organisationName)).get();

            OrganisationDataBuilder organisation = organisationBuilder.
                    createOrganisation(line.organisationName, matchingOrganisationDetails.companyRegistrationNumber, lookupOrganisationType(matchingOrganisationDetails.organisationType));

            if (matchingOrganisationDetails.addressType != null) {
                organisation = organisation.withAddress(matchingOrganisationDetails.addressType,
                        matchingOrganisationDetails.addressLine1, matchingOrganisationDetails.addressLine2,
                        matchingOrganisationDetails.addressLine3, matchingOrganisationDetails.town,
                        matchingOrganisationDetails.postcode, matchingOrganisationDetails.county);
            }

            return builder.withNewOrganisation(organisation);
        };

        Function<S, S> registerUserIfNecessary = builder ->
                createViaRegistration ?
                        builder.registerUser(line.firstName, line.lastName, line.emailAddress, line.organisationName, line.phoneNumber) :
                        builder.createUserDirectly(line.firstName, line.lastName, line.emailAddress, line.organisationName, line.phoneNumber);

        Function<S, S> verifyEmailIfNecessary = builder ->
                createViaRegistration && line.emailVerified ? builder.verifyEmail() : builder;

        createOrgIfNecessary.andThen(registerUserIfNecessary).andThen(verifyEmailIfNecessary).apply(baseBuilder).build();
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
