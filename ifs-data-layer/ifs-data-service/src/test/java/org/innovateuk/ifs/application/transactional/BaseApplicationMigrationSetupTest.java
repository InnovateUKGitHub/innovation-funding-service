package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseAuthenticationAwareIntegrationTest;
import org.innovateuk.ifs.activitylog.domain.ActivityLogBuilder;
import org.innovateuk.ifs.activitylog.repository.ActivityLogRepository;
import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.address.builder.AddressBuilder;
import org.innovateuk.ifs.address.builder.AddressTypeBuilder;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.domain.AddressType;
import org.innovateuk.ifs.address.repository.AddressRepository;
import org.innovateuk.ifs.address.repository.AddressTypeRepository;
import org.innovateuk.ifs.application.builder.ApplicationOrganisationAddressBuilder;
import org.innovateuk.ifs.application.builder.FormInputResponseBuilder;
import org.innovateuk.ifs.application.builder.QuestionStatusBuilder;
import org.innovateuk.ifs.application.domain.*;
import org.innovateuk.ifs.application.repository.*;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AverageAssessorScore;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.repository.AverageAssessorScoreRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder;
import org.innovateuk.ifs.finance.builder.ApplicationFinanceRowBuilder;
import org.innovateuk.ifs.finance.builder.FinanceRowMetaFieldBuilder;
import org.innovateuk.ifs.finance.builder.FinanceRowMetaValueBuilder;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaField;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaValue;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRowRepository;
import org.innovateuk.ifs.finance.repository.FinanceRowMetaFieldRepository;
import org.innovateuk.ifs.finance.repository.FinanceRowMetaValueRepository;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.grant.domain.GrantProcess;
import org.innovateuk.ifs.grant.repository.GrantProcessRepository;
import org.innovateuk.ifs.granttransfer.builder.EuGrantTransferBuilder;
import org.innovateuk.ifs.granttransfer.repository.EuGrantTransferRepository;
import org.innovateuk.ifs.horizon.builder.HorizonWorkProgrammeBuilder;
import org.innovateuk.ifs.horizon.domain.ApplicationHorizonWorkProgramme;
import org.innovateuk.ifs.horizon.domain.HorizonWorkProgramme;
import org.innovateuk.ifs.horizon.repository.ApplicationHorizonWorkProgrammeRepository;
import org.innovateuk.ifs.horizon.repository.HorizonWorkProgrammeRepository;
import org.innovateuk.ifs.interview.domain.Interview;
import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentRepository;
import org.innovateuk.ifs.interview.repository.InterviewRepository;
import org.innovateuk.ifs.interview.resource.InterviewState;
import org.innovateuk.ifs.invite.builder.InviteOrganisationBuilder;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.ApplicationKtaInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.invite.repository.ApplicationKtaInviteRepository;
import org.innovateuk.ifs.invite.repository.InviteOrganisationRepository;
import org.innovateuk.ifs.organisation.builder.OrganisationAddressBuilder;
import org.innovateuk.ifs.organisation.builder.OrganisationBuilder;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.domain.OrganisationAddress;
import org.innovateuk.ifs.organisation.repository.OrganisationAddressRepository;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.project.core.builder.ProjectBuilder;
import org.innovateuk.ifs.project.core.domain.ProjectToBeCreated;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.core.repository.ProjectToBeCreatedRepository;
import org.innovateuk.ifs.review.domain.Review;
import org.innovateuk.ifs.review.repository.ReviewRepository;
import org.innovateuk.ifs.review.resource.ReviewState;
import org.innovateuk.ifs.supporter.domain.SupporterAssignment;
import org.innovateuk.ifs.supporter.repository.SupporterAssignmentRepository;
import org.innovateuk.ifs.user.builder.ProcessRoleBuilder;
import org.innovateuk.ifs.user.builder.UserBuilder;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.workflow.audit.ProcessHistoryRepository;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public class BaseApplicationMigrationSetupTest extends BaseAuthenticationAwareIntegrationTest {

    @Autowired
    protected ApplicationMigrationRepository applicationMigrationRepository;

    @Autowired
    protected CompetitionRepository competitionRepository;

    @Autowired
    protected ApplicationRepository applicationRepository;

    @Autowired
    protected OrganisationRepository organisationRepository;

    @Autowired
    protected ActivityLogRepository activityLogRepository;

    @Autowired
    protected ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    protected ApplicationFinanceRowRepository applicationFinanceRowRepository;

    @Autowired
    protected FinanceRowMetaValueRepository financeRowMetaValueRepository;

    @Autowired
    protected FinanceRowMetaFieldRepository financeRowMetaFieldRepository;

    @Autowired
    protected ApplicationHiddenFromDashboardRepository applicationHiddenFromDashboardRepository;

    @Autowired
    protected AddressRepository addressRepository;

    @Autowired
    protected AddressTypeRepository addressTypeRepository;

    @Autowired
    protected OrganisationAddressRepository organisationAddressRepository;

    @Autowired
    protected ApplicationOrganisationAddressRepository applicationOrganisationAddressRepository;

    @Autowired
    protected AverageAssessorScoreRepository averageAssessorScoreRepository;

    @Autowired
    protected EuGrantTransferRepository euGrantTransferRepository;

    @Autowired
    protected FormInputResponseRepository formInputResponseRepository;

    @Autowired
    protected ProcessRoleRepository processRoleRepository;

    @Autowired
    protected ProjectRepository projectRepository;

    @Autowired
    protected ProjectToBeCreatedRepository projectToBeCreatedRepository;

    @Autowired
    protected QuestionStatusRepository questionStatusRepository;

    @Autowired
    protected GrantProcessRepository grantProcessRepository;

    @Autowired
    protected ApplicationProcessRepository applicationProcessRepository;

    @Autowired
    protected ProcessHistoryRepository processHistoryRepository;

    @Autowired
    protected AssessmentRepository assessmentRepository;

    @Autowired
    protected InterviewRepository interviewRepository;

    @Autowired
    protected InterviewAssignmentRepository interviewAssignmentRepository;

    @Autowired
    protected ReviewRepository reviewRepository;

    @Autowired
    protected SupporterAssignmentRepository supporterAssignmentRepository;

    @Autowired
    protected ApplicationInviteRepository applicationInviteRepository;

    @Autowired
    protected ApplicationKtaInviteRepository applicationKtaInviteRepository;

    @Autowired
    protected InviteOrganisationRepository inviteOrganisationRepository;

    @Autowired
    protected ApplicationExpressionOfInterestConfigRepository applicationExpressionOfInterestConfigRepository;

    @Autowired
    protected HorizonWorkProgrammeRepository horizonWorkProgrammeRepository;

    @Autowired
    protected ApplicationHorizonWorkProgrammeRepository applicationHorizonWorkProgrammeRepository;

    protected Long applicationId;

    @Before
    public void setup() {
        loginIfsAdmin();

        Competition competition = competitionRepository.findById(1L).get();
        Question question = competition.getQuestions().stream().findFirst().get();
        FormInput formInput = question.getFormInputs().stream().findFirst().get();

        loginSteveSmith();

        User user = UserBuilder.newUser()
                .withId(getSteveSmith().getId())
                .build();

        Application application = new Application();
        application.setCompetition(competition);

        application = applicationRepository.save(application);
        applicationId = application.getId();

        Organisation organisation = organisationRepository.save(OrganisationBuilder.newOrganisation().build());

        ProcessRole processRole = processRoleRepository.save(ProcessRoleBuilder.newProcessRole()
                .withApplication(application)
                .withRole(ProcessRoleType.LEADAPPLICANT)
                .withOrganisation(organisation)
                .withUser(user)
                .build());

        activityLogRepository.save(ActivityLogBuilder.newActivityLog()
                .withApplication(application)
                .withType(ActivityType.APPLICATION_SUBMITTED)
                .build());

        ApplicationFinance applicationFinance = applicationFinanceRepository.save(ApplicationFinanceBuilder.newApplicationFinance()
                .withApplication(application)
                .build());

        ApplicationFinanceRow applicationFinanceRow = applicationFinanceRowRepository.save(ApplicationFinanceRowBuilder.newApplicationFinanceRow()
                .withTarget(applicationFinance)
                .build());

        FinanceRowMetaField financeRowMetaField = financeRowMetaFieldRepository.save(FinanceRowMetaFieldBuilder.newFinanceRowMetaField()
                .withTitle("country")
                .withType("String")
                .build());

        FinanceRowMetaValue financeRowMetaValue = FinanceRowMetaValueBuilder.newFinanceRowMetaValue()
                .withFinanceRow(applicationFinanceRow.getId())
                .withFinanceRowMetaField(financeRowMetaField)
                .withValue("GB")
                .build();
        financeRowMetaValueRepository.save(financeRowMetaValue);

        applicationHiddenFromDashboardRepository.save(new ApplicationHiddenFromDashboard(application, user));

        Address address = addressRepository.save(AddressBuilder.newAddress().build());
        AddressType addressType = addressTypeRepository.save(AddressTypeBuilder.newAddressType().build());
        OrganisationAddress organisationAddress = organisationAddressRepository.save(OrganisationAddressBuilder.newOrganisationAddress()
                .withAddress(address)
                .withAddressType(addressType)
                .build());
        applicationOrganisationAddressRepository.save(ApplicationOrganisationAddressBuilder.newApplicationOrganisationAddress()
                .withApplication(application)
                .withOrganisationAddress(organisationAddress)
                .build());

        averageAssessorScoreRepository.save(new AverageAssessorScore(application, BigDecimal.TEN));

        euGrantTransferRepository.save(EuGrantTransferBuilder.newEuGrantTransfer()
                .withApplication(application).build());

        formInputResponseRepository.save(FormInputResponseBuilder.newFormInputResponse()
                .withApplication(application)
                .withFormInputs(formInput)
                .build());

        projectRepository.save(ProjectBuilder.newProject()
                .withApplication(application)
                .withName("Project").build());

        projectToBeCreatedRepository.save(new ProjectToBeCreated(application, "email"));

        questionStatusRepository.save(QuestionStatusBuilder.newQuestionStatus()
                .withQuestion(question)
                        .withMarkedAsComplete(true)
                .withApplication(application).build());

        grantProcessRepository.save(new GrantProcess(application.getId()));

        applicationProcessRepository.save(new ApplicationProcess(application, processRole, ApplicationState.CREATED));
        applicationProcessRepository.save(new ApplicationProcess(application, processRole, ApplicationState.SUBMITTED));

        assessmentRepository.save(new Assessment(application, processRole));

        Interview interview = new Interview(application, processRole);
        interview.setProcessState(InterviewState.ASSIGNED);
        interviewRepository.save(interview);
        ProcessRole interviewProcessRole = processRoleRepository.save(ProcessRoleBuilder.newProcessRole()
                .withApplication(application)
                .withRole(ProcessRoleType.INTERVIEW_LEAD_APPLICANT)
                .withOrganisation(organisation)
                .withUser(user)
                .build());

        interviewAssignmentRepository.save(new InterviewAssignment(application, interviewProcessRole));

        Review review = new Review(application, processRole);
        review.setProcessState(ReviewState.CREATED);
        reviewRepository.save(review);

        supporterAssignmentRepository.save(new SupporterAssignment(application, user));

        InviteOrganisation inviteOrganisation = inviteOrganisationRepository.save(InviteOrganisationBuilder.newInviteOrganisation()
                .withOrganisation(organisation)
                .build());
        applicationInviteRepository.save(new ApplicationInvite("name", "application_invite@email.com", application, inviteOrganisation, UUID.randomUUID().toString(), InviteStatus.CREATED));

        applicationKtaInviteRepository.save(new ApplicationKtaInvite("name", "application_kta_invite@email.com", application,UUID.randomUUID().toString(), InviteStatus.CREATED));

        applicationMigrationRepository.save(new ApplicationMigration(applicationId, MigrationStatus.CREATED));

        ApplicationExpressionOfInterestConfig applicationExpressionOfInterestConfig = ApplicationExpressionOfInterestConfig.builder()
                .application(application)
                .enabledForExpressionOfInterest(true)
                .build();
        applicationExpressionOfInterestConfigRepository.save(applicationExpressionOfInterestConfig);

        HorizonWorkProgramme horizonWorkProgramme = horizonWorkProgrammeRepository.save(HorizonWorkProgrammeBuilder.newHorizonWorkProgramme()
                .withName("HorizonWorkProgramme")
                .withEnabled(true)
                .build());
        applicationHorizonWorkProgrammeRepository.save(new ApplicationHorizonWorkProgramme(applicationId, horizonWorkProgramme));
    }

    protected void setupEoiApplication() {
        Optional<Application> optionalApplication =  applicationRepository.findById(applicationId);

        optionalApplication.ifPresent(application -> {
            ApplicationExpressionOfInterestConfig applicationExpressionOfInterestConfig = ApplicationExpressionOfInterestConfig.builder()
                    .application(application)
                    .enabledForExpressionOfInterest(true)
                    .build();
            applicationExpressionOfInterestConfigRepository.save(applicationExpressionOfInterestConfig);
            application.setApplicationExpressionOfInterestConfig(applicationExpressionOfInterestConfig);
        });
    }
}
