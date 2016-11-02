package com.worth.ifs;

import com.worth.ifs.address.mapper.AddressMapper;
import com.worth.ifs.address.repository.AddressRepository;
import com.worth.ifs.address.repository.AddressTypeRepository;
import com.worth.ifs.address.transactional.AddressLookupService;
import com.worth.ifs.address.transactional.AddressService;
import com.worth.ifs.alert.mapper.AlertMapper;
import com.worth.ifs.alert.repository.AlertRepository;
import com.worth.ifs.alert.transactional.AlertService;
import com.worth.ifs.application.mapper.ApplicationMapper;
import com.worth.ifs.application.mapper.QuestionMapper;
import com.worth.ifs.application.mapper.SectionMapper;
import com.worth.ifs.application.repository.*;
import com.worth.ifs.application.transactional.ApplicationFundingService;
import com.worth.ifs.application.transactional.ApplicationService;
import com.worth.ifs.application.transactional.AssessorFeedbackService;
import com.worth.ifs.application.transactional.QuestionService;
import com.worth.ifs.assessment.mapper.AssessmentMapper;
import com.worth.ifs.assessment.mapper.AssessorFormInputResponseMapper;
import com.worth.ifs.assessment.mapper.CompetitionInviteMapper;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.assessment.repository.AssessorFormInputResponseRepository;
import com.worth.ifs.assessment.transactional.AssessmentService;
import com.worth.ifs.assessment.transactional.AssessorFormInputResponseService;
import com.worth.ifs.assessment.transactional.AssessorService;
import com.worth.ifs.assessment.transactional.CompetitionInviteService;
import com.worth.ifs.authentication.service.IdentityProviderService;
import com.worth.ifs.bankdetails.mapper.BankDetailsMapper;
import com.worth.ifs.bankdetails.repository.BankDetailsRepository;
import com.worth.ifs.bankdetails.transactional.BankDetailsService;
import com.worth.ifs.category.mapper.CategoryLinkMapper;
import com.worth.ifs.category.mapper.CategoryMapper;
import com.worth.ifs.category.repository.CategoryLinkRepository;
import com.worth.ifs.category.repository.CategoryRepository;
import com.worth.ifs.category.transactional.CategoryLinkService;
import com.worth.ifs.category.transactional.CategoryService;
import com.worth.ifs.commons.test.BaseTest;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.competition.repository.CompetitionFunderRepository;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.email.service.EmailService;
import com.worth.ifs.file.mapper.FileEntryMapper;
import com.worth.ifs.file.transactional.FileHttpHeadersValidator;
import com.worth.ifs.file.transactional.FileService;
import com.worth.ifs.finance.mapper.ApplicationFinanceMapper;
import com.worth.ifs.finance.repository.ApplicationFinanceRepository;
import com.worth.ifs.finance.repository.FinanceRowRepository;
import com.worth.ifs.finance.transactional.FinanceRowService;
import com.worth.ifs.form.repository.FormInputRepository;
import com.worth.ifs.form.repository.FormInputResponseRepository;
import com.worth.ifs.form.transactional.FormInputService;
import com.worth.ifs.invite.mapper.*;
import com.worth.ifs.invite.repository.*;
import com.worth.ifs.invite.transactional.EthnicityService;
import com.worth.ifs.invite.transactional.InviteProjectService;
import com.worth.ifs.invite.transactional.RejectionReasonService;
import com.worth.ifs.notifications.resource.SystemNotificationSource;
import com.worth.ifs.notifications.service.NotificationService;
import com.worth.ifs.organisation.repository.OrganisationAddressRepository;
import com.worth.ifs.organisation.transactional.OrganisationService;
import com.worth.ifs.project.finance.repository.CostCategoryRepository;
import com.worth.ifs.project.finance.repository.CostCategoryTypeRepository;
import com.worth.ifs.project.finance.repository.FinanceCheckProcessRepository;
import com.worth.ifs.project.finance.repository.SpendProfileRepository;
import com.worth.ifs.project.finance.transactional.FinanceCheckService;
import com.worth.ifs.project.finance.transactional.ProjectFinanceService;
import com.worth.ifs.project.finance.workflow.financechecks.configuration.FinanceCheckWorkflowHandler;
import com.worth.ifs.project.mapper.MonitoringOfficerMapper;
import com.worth.ifs.project.mapper.ProjectMapper;
import com.worth.ifs.project.mapper.ProjectUserMapper;
import com.worth.ifs.project.repository.MonitoringOfficerRepository;
import com.worth.ifs.project.repository.PartnerOrganisationRepository;
import com.worth.ifs.project.repository.ProjectRepository;
import com.worth.ifs.project.repository.ProjectUserRepository;
import com.worth.ifs.project.transactional.ProjectGrantOfferService;
import com.worth.ifs.project.transactional.ProjectService;
import com.worth.ifs.project.transactional.ProjectStatusService;
import com.worth.ifs.project.users.ProjectUsersHelper;
import com.worth.ifs.project.workflow.projectdetails.configuration.ProjectDetailsWorkflowHandler;
import com.worth.ifs.sil.experian.service.SilExperianEndpoint;
import com.worth.ifs.token.repository.TokenRepository;
import com.worth.ifs.token.transactional.TokenService;
import com.worth.ifs.user.mapper.*;
import com.worth.ifs.user.repository.*;
import com.worth.ifs.user.transactional.*;
import com.worth.ifs.workflow.mapper.ProcessOutcomeMapper;
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
    protected ApplicationFinanceMapper applicationFinanceMapperMock;

    @Mock
    protected ApplicationFinanceRepository applicationFinanceRepositoryMock;

    @Mock
    protected AssessmentMapper assessmentMapperMock;

    @Mock
    protected AssessmentService assessmentServiceMock;

    @Mock
    protected AssessorFormInputResponseMapper assessorFormInputResponseMapperMock;

    @Mock
    protected AssessorFormInputResponseService assessorFormInputResponseServiceMock;

    @Mock
    protected FormInputResponseRepository formInputResponseRepositoryMock;

    @Mock
    protected UserRepository userRepositoryMock;

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
    protected OrganisationRepository organisationRepositoryMock;

    @Mock
    protected ApplicationStatusRepository applicationStatusRepositoryMock;

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
    protected CompetitionInviteMapper competitionInviteMapperMock;

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
    protected ProcessOutcomeMapper processOutcomeMapperMock;

    @Mock
    protected OrganisationService organisationServiceMock;

    @Mock
    protected UserService userServiceMock;

    @Mock
    protected UserProfileService userProfileServiceMock;

    @Mock
    protected FinanceRowRepository financeRowRepositoryMock;

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
    protected CategoryMapper categoryMapperMock;

    @Mock
    protected CategoryLinkService categoryLinkServiceMock;

    @Mock
    protected CategoryLinkRepository categoryLinkRepositoryMock;

    @Mock
    protected CategoryLinkMapper categoryLinkMapperMock;

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
    protected PartnerOrganisationRepository partnerOrganisationRepositoryMock;

    @Mock
    protected FinanceCheckService financeCheckServiceMock;

    @Mock
    protected FinanceCheckProcessRepository financeCheckProcessRepository;

    @Mock
    protected ContractService contractServiceMock;

    @Mock
    protected ContractRepository contractRepositoryMock;

    @Mock
    protected ContractMapper contractMapperMock;

    @Before
    public void setupMockInjection() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);
    }
}