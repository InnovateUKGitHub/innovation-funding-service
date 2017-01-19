package org.innovateuk.ifs;

import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.address.repository.AddressRepository;
import org.innovateuk.ifs.address.repository.AddressTypeRepository;
import org.innovateuk.ifs.address.transactional.AddressLookupService;
import org.innovateuk.ifs.address.transactional.AddressService;
import org.innovateuk.ifs.alert.mapper.AlertMapper;
import org.innovateuk.ifs.alert.repository.AlertRepository;
import org.innovateuk.ifs.alert.transactional.AlertService;
import org.innovateuk.ifs.application.mapper.ApplicationCountSummaryMapper;
import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.application.mapper.QuestionMapper;
import org.innovateuk.ifs.application.mapper.SectionMapper;
import org.innovateuk.ifs.application.repository.*;
import org.innovateuk.ifs.application.transactional.*;
import org.innovateuk.ifs.assessment.mapper.AssessmentMapper;
import org.innovateuk.ifs.assessment.mapper.AssessorFormInputResponseMapper;
import org.innovateuk.ifs.assessment.mapper.AssessorInviteToSendMapper;
import org.innovateuk.ifs.assessment.mapper.CompetitionInviteMapper;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.repository.AssessorFormInputResponseRepository;
import org.innovateuk.ifs.assessment.transactional.*;
import org.innovateuk.ifs.assessment.workflow.configuration.AssessmentWorkflowHandler;
import org.innovateuk.ifs.authentication.service.IdentityProviderService;
import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.category.mapper.InnovationSectorMapper;
import org.innovateuk.ifs.category.mapper.ResearchCategoryMapper;
import org.innovateuk.ifs.category.repository.*;
import org.innovateuk.ifs.category.transactional.CompetitionCategoryLinkService;
import org.innovateuk.ifs.category.transactional.CategoryService;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.commons.test.BaseTest;
import org.innovateuk.ifs.competition.mapper.AssessorCountOptionMapper;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.repository.AssessorCountOptionRepository;
import org.innovateuk.ifs.competition.repository.CompetitionFunderRepository;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.email.service.EmailService;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.file.repository.FileEntryRepository;
import org.innovateuk.ifs.file.service.FileTemplateRenderer;
import org.innovateuk.ifs.file.transactional.FileHttpHeadersValidator;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.finance.handler.OrganisationFinanceDelegate;
import org.innovateuk.ifs.finance.mapper.ApplicationFinanceMapper;
import org.innovateuk.ifs.finance.repository.*;
import org.innovateuk.ifs.finance.transactional.FinanceRowService;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.repository.FormInputResponseRepository;
import org.innovateuk.ifs.form.transactional.FormInputService;
import org.innovateuk.ifs.invite.mapper.*;
import org.innovateuk.ifs.invite.repository.*;
import org.innovateuk.ifs.invite.transactional.EthnicityService;
import org.innovateuk.ifs.invite.transactional.InviteProjectService;
import org.innovateuk.ifs.invite.transactional.RejectionReasonService;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.notifications.service.senders.NotificationSender;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapper;
import org.innovateuk.ifs.organisation.repository.OrganisationAddressRepository;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.project.bankdetails.mapper.BankDetailsMapper;
import org.innovateuk.ifs.project.bankdetails.repository.BankDetailsRepository;
import org.innovateuk.ifs.project.bankdetails.transactional.BankDetailsService;
import org.innovateuk.ifs.project.finance.repository.*;
import org.innovateuk.ifs.project.finance.transactional.FinanceCheckService;
import org.innovateuk.ifs.project.finance.transactional.ProjectFinanceService;
import org.innovateuk.ifs.project.finance.workflow.financechecks.configuration.FinanceCheckWorkflowHandler;
import org.innovateuk.ifs.project.gol.workflow.configuration.GOLWorkflowHandler;
import org.innovateuk.ifs.project.mapper.MonitoringOfficerMapper;
import org.innovateuk.ifs.project.mapper.ProjectMapper;
import org.innovateuk.ifs.project.mapper.ProjectUserMapper;
import org.innovateuk.ifs.project.repository.MonitoringOfficerRepository;
import org.innovateuk.ifs.project.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.repository.ProjectRepository;
import org.innovateuk.ifs.project.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.transactional.ProjectGrantOfferService;
import org.innovateuk.ifs.project.transactional.ProjectService;
import org.innovateuk.ifs.project.transactional.ProjectStatusService;
import org.innovateuk.ifs.project.users.ProjectUsersHelper;
import org.innovateuk.ifs.project.util.SpendProfileTableCalculator;
import org.innovateuk.ifs.project.workflow.configuration.ProjectWorkflowHandler;
import org.innovateuk.ifs.project.workflow.projectdetails.configuration.ProjectDetailsWorkflowHandler;
import org.innovateuk.ifs.sil.experian.service.SilExperianEndpoint;
import org.innovateuk.ifs.token.repository.TokenRepository;
import org.innovateuk.ifs.token.transactional.TokenService;
import org.innovateuk.ifs.user.mapper.*;
import org.innovateuk.ifs.user.repository.*;
import org.innovateuk.ifs.user.transactional.*;
import org.innovateuk.ifs.workflow.mapper.ProcessOutcomeMapper;
import org.innovateuk.ifs.workflow.transactional.ProcessOutcomeService;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * This is a convenience subclass for all tests that require Mockito support.  At its simplest this class is simply a
 * place to store and initialise Mockito mocks.  Mocks can then be injected into particular attributes using the @InjectMocks
 * annotation.
 */
public abstract class BaseUnitTestMocksTest extends BaseTest {

    @Mock
    protected AffiliationMapper affiliationMapperMock;

    @Mock
    protected AlertService alertServiceMock;

    @Mock
    protected AlertRepository alertRepositoryMock;

    @Mock
    protected AlertMapper alertMapperMock;

    @Mock
    protected AddressRepository addressRepositoryMock;

    @Mock
    protected ApplicationRepository applicationRepositoryMock;

    @Mock
    protected ApplicationMapper applicationMapperMock;

    @Mock
    protected ApplicationAssessmentSummaryService applicationAssessmentSummaryServiceMock;

    @Mock
    protected ApplicationFinanceMapper applicationFinanceMapperMock;

    @Mock
    protected ApplicationFinanceRepository applicationFinanceRepositoryMock;

    @Mock
    protected AssessmentMapper assessmentMapperMock;

    @Mock
    protected AssessmentService assessmentServiceMock;

    @Mock
    protected AssessmentWorkflowHandler assessmentWorkflowHandlerMock;

    @Mock
    protected AssessorFormInputResponseMapper assessorFormInputResponseMapperMock;

    @Mock
    protected AssessorFormInputResponseService assessorFormInputResponseServiceMock;

    @Mock
    protected FormInputResponseRepository formInputResponseRepositoryMock;

    @Mock
    protected UserRepository userRepositoryMock;

    @Mock
    protected ProfileRepository profileRepositoryMock;

    @Mock
    protected CompAdminEmailRepository compAdminEmailRepositoryMock;

    @Mock
    protected ProjectFinanceEmailRepository projectFinanceEmailRepositoryMock;

    @Mock
    protected RoleRepository roleRepositoryMock;

    @Mock
    protected RoleService roleServiceMock;

    @Mock
    protected ProcessRoleRepository processRoleRepositoryMock;

    @Mock
    protected CompetitionRepository competitionRepositoryMock;

    @Mock
    protected CompetitionService competitionServiceMock;

    @Mock
    protected OrganisationRepository organisationRepositoryMock;

    @Mock
    protected ApplicationStatusRepository applicationStatusRepositoryMock;

    @Mock
    protected ApplicationStatisticsRepository applicationStatisticsRepositoryMock;

    @Mock
    protected ApplicationCountSummaryMapper applicationCountSummaryMapperMock;

    @Mock
    protected FormInputRepository formInputRepositoryMock;

    @Mock
    protected SectionMapper sectionMapperMock;

    @Mock
    protected SectionRepository sectionRepositoryMock;

    @Mock
    protected MonitoringOfficerRepository monitoringOfficerRepositoryMock;

    @Mock
    protected MonitoringOfficerMapper monitoringOfficerMapper;

    @Mock
    protected ApplicationService applicationServiceMock;

    @Mock
    protected ApplicationCountSummaryService applicationCountSummaryServiceMock;

    @Mock
    protected QuestionService questionServiceMock;

    @Mock
    protected QuestionRepository questionRepositoryMock;

    @Mock
    protected QuestionStatusRepository questionStatusRepositoryMock;

    @Mock
    protected QuestionMapper questionMapperMock;

    @Mock
    protected FileService fileServiceMock;

    @Mock
    protected EmailService emailServiceMock;

    @Mock
    protected NotificationService notificationServiceMock;

    @Mock
    protected InviteProjectMapper inviteProjectMapperMock;

    @Mock
    protected InviteOrganisationRepository inviteOrganisationRepositoryMock;

    @Mock
    protected ApplicationInviteRepository applicationInviteRepositoryMock;

    @Mock
    protected CompetitionInviteRepository competitionInviteRepositoryMock;

    @Mock
    protected CompetitionParticipantRepository competitionParticipantRepositoryMock;

    @Mock
    protected CompetitionParticipantService competitionParticipantServiceMock;

    @Mock
    protected CompetitionInviteMapper competitionInviteMapperMock;

    @Mock
    protected CompetitionMapper competitionMapperMock;

    @Mock
    protected AssessorInviteToSendMapper assessorInviteToSendMapperMock;

    @Mock
    protected CompetitionInviteService competitionInviteServiceMock;

    @Mock
    protected CompetitionParticipantMapper competitionParticipantMapperMock;

    @Mock
    protected CompetitionParticipantRoleMapper competitionParticipantRoleMapperMock;

    @Mock
    protected ParticipantStatusMapper participantStatusMapperMock;

    @Mock
    protected InviteProjectRepository inviteProjectRepositoryMock;

    @Mock
    protected InviteProjectService inviteProjectServiceMock;

    @Mock
    protected AddressLookupService addressLookupServiceMock;

    @Mock
    protected AddressService addressService;

    @Mock
    protected ProcessOutcomeService processOutcomeServiceMock;

    @Mock
    protected ProcessOutcomeMapper processOutcomeMapperMock;

    @Mock
    protected OrganisationService organisationServiceMock;

    @Mock
    protected BaseUserService baseUserServiceMock;

    @Mock
    protected UserService userServiceMock;

    @Mock
    protected UserProfileService userProfileServiceMock;

    @Mock
    protected ApplicationFinanceRowRepository applicationFinanceRowRepositoryMock;

    @Mock
    protected AssessmentRepository assessmentRepositoryMock;

    @Mock
    protected AssessorFormInputResponseRepository assessorFormInputResponseRepositoryMock;

    @Mock
    protected RegistrationService registrationServiceMock;

    @Mock
    protected IdentityProviderService idpServiceMock;

    @Mock
    protected RoleMapper roleMapperMock;

    @Mock
    protected TokenService tokenServiceMock;

    @Mock
    protected TokenRepository tokenRepositoryMock;

    @Mock
    protected UserMapper userMapperMock;

    @Mock
    protected PasswordPolicyValidator passwordPolicyValidatorMock;

    @Mock
    protected FormInputService formInputServiceMock;

    @Mock
    protected ApplicationFundingService applicationFundingServiceMock;

    @Mock
    protected SystemNotificationSource systemNotificationSourceMock;

    @Mock
    protected FileHttpHeadersValidator fileValidatorMock;

    @Mock
    protected FileEntryRepository fileEntryRepositoryMock;

    @Mock
    protected FileEntryMapper fileEntryMapperMock;

    @Mock
    protected AddressMapper addressMapperMock;

    @Mock
    protected AddressTypeRepository addressTypeRepositoryMock;

    @Mock
    protected OrganisationAddressRepository organisationAddressRepositoryMock;

    @Mock
    protected AssessorFeedbackService assessorFeedbackServiceMock;

    @Mock
    protected ProjectService projectServiceMock;

    @Mock
    protected ProjectGrantOfferService projectGrantOfferServiceMock;

    @Mock
    protected ProjectStatusService projectStatusServiceMock;

    @Mock
    protected ProjectMapper projectMapperMock;

    @Mock
    protected ProjectUserMapper projectUserMapperMock;

    @Mock
    protected ProjectRepository projectRepositoryMock;

    @Mock
    protected ProjectUserRepository projectUserRepositoryMock;

    @Mock
    protected CostCategoryRepository costCategoryRepositoryMock;

    @Mock
    protected CostCategoryTypeRepository costCategoryTypeRepositoryMock;

    @Mock
    protected CategoryService categoryServiceMock;

    @Mock
    protected CategoryRepository categoryRepositoryMock;

    @Mock
    protected InnovationAreaRepository innovationAreaRepositoryMock;

    @Mock
    protected InnovationSectorRepository innovationSectorRepositoryMock;

    @Mock
    protected ResearchCategoryRepository researchCategoryRepositoryMock;

    @Mock
    protected InnovationAreaMapper innovationAreaMapperMock;

    @Mock
    protected InnovationSectorMapper innovationSectorMapperMock;

    @Mock
    protected ResearchCategoryMapper researchCategoryMapperMock;

    @Mock
    protected CompetitionCategoryLinkService competitionCategoryLinkServiceMock;

    @Mock
    protected CompetitionCategoryLinkRepository competitionCategoryLinkRepositoryMock;

    @Mock
    protected BankDetailsMapper bankDetailsMapperMock;

    @Mock
    protected BankDetailsRepository bankDetailsRepositoryMock;

    @Mock
    protected BankDetailsService bankDetailsServiceMock;

    @Mock
    protected CompetitionFunderRepository competitionFunderRepositoryMock;

    @Mock
    protected SilExperianEndpoint silExperianEndpointMock;

    @Mock
    protected SpendProfileRepository spendProfileRepositoryMock;

    @Mock
    protected ProjectFinanceService projectFinanceServiceMock;

    @Mock
    protected UserAuthenticationService userAuthenticationService;

    @Mock
    protected RejectionReasonRepository rejectionReasonRepositoryMock;

    @Mock
    protected RejectionReasonMapper rejectionReasonMapperMock;

    @Mock
    protected RejectionReasonService rejectionReasonServiceMock;

    @Mock
    protected FinanceRowService financeRowServiceMock;

    @Mock
    protected ProjectDetailsWorkflowHandler projectDetailsWorkflowHandlerMock;

    @Mock
    protected EthnicityRepository ethnicityRepositoryMock;

    @Mock
    protected EthnicityMapper ethnicityMapperMock;

    @Mock
    protected EthnicityService ethnicityServiceMock;

    @Mock
    protected AssessorService assessorServiceMock;

    @Mock
    protected ProjectUsersHelper projectUsersHelperMock;

    @Mock
    protected FinanceCheckWorkflowHandler financeCheckWorkflowHandlerMock;

    @Mock
    protected GOLWorkflowHandler golWorkflowHandlerMock;

    @Mock
    protected ProjectWorkflowHandler projectWorkflowHandlerMock;

    @Mock
    protected PartnerOrganisationRepository partnerOrganisationRepositoryMock;

    @Mock
    protected FinanceCheckService financeCheckServiceMock;

    @Mock
    protected FinanceCheckProcessRepository financeCheckProcessRepository;

    @Mock
    protected FinanceCheckRepository financeCheckRepositoryMock;

    @Mock
    protected ContractService contractServiceMock;

    @Mock
    protected ContractRepository contractRepositoryMock;

    @Mock
    protected ContractMapper contractMapperMock;

    @Mock
    protected FileTemplateRenderer rendererMock;

    @Mock
    protected AssessorCountOptionMapper assessorCountOptionMapperMock;

    @Mock
    protected AssessorCountOptionRepository assessorCountOptionRepositoryMock;

    @Mock
    protected ProjectFinanceRepository projectFinanceRepositoryMock;

    @Mock
    protected ProjectFinanceRowRepository projectFinanceRowRepositoryMock;

    @Mock
    protected FinanceRowMetaValueRepository financeRowMetaValueRepositoryMock;

    @Mock
    protected FinanceRowMetaFieldRepository financeRowMetaFieldRepositoryMock;

    @Mock
    protected OrganisationFinanceDelegate organisationFinanceDelegateMock;

    @Mock
    protected OrganisationMapper organisationMapperMock;

    @Mock
    protected SpendProfileTableCalculator spendProfileTableCalculatorMock;

    @Mock
    protected NotificationSender notificationSender;

    @Before
    public void setupMockInjection() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);
    }
}
