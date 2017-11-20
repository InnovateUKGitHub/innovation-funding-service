package org.innovateuk.ifs;

import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.address.repository.AddressRepository;
import org.innovateuk.ifs.address.repository.AddressTypeRepository;
import org.innovateuk.ifs.address.transactional.AddressLookupService;
import org.innovateuk.ifs.address.transactional.AddressService;
import org.innovateuk.ifs.affiliation.transactional.AffiliationService;
import org.innovateuk.ifs.alert.mapper.AlertMapper;
import org.innovateuk.ifs.alert.repository.AlertRepository;
import org.innovateuk.ifs.alert.transactional.AlertService;
import org.innovateuk.ifs.application.mapper.*;
import org.innovateuk.ifs.application.repository.*;
import org.innovateuk.ifs.application.transactional.*;
import org.innovateuk.ifs.application.workflow.configuration.ApplicationWorkflowHandler;
import org.innovateuk.ifs.assessment.mapper.*;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.repository.AssessorFormInputResponseRepository;
import org.innovateuk.ifs.assessment.transactional.*;
import org.innovateuk.ifs.assessment.workflow.configuration.AssessmentWorkflowHandler;
import org.innovateuk.ifs.authentication.service.IdentityProviderService;
import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.category.mapper.InnovationSectorMapper;
import org.innovateuk.ifs.category.mapper.ResearchCategoryMapper;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.category.repository.InnovationSectorRepository;
import org.innovateuk.ifs.category.repository.ResearchCategoryRepository;
import org.innovateuk.ifs.category.transactional.CategoryService;
import org.innovateuk.ifs.commons.competitionsetup.CompetitionSetupTransactionalService;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.commons.test.BaseTest;
import org.innovateuk.ifs.competition.mapper.AssessorCountOptionMapper;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.repository.AssessorCountOptionRepository;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.transactional.CompetitionKeyStatisticsService;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupQuestionService;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupService;
import org.innovateuk.ifs.competition.transactional.template.QuestionNumberOrderService;
import org.innovateuk.ifs.competition.transactional.template.QuestionPriorityOrderService;
import org.innovateuk.ifs.email.service.EmailService;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.file.repository.FileEntryRepository;
import org.innovateuk.ifs.file.service.FileTemplateRenderer;
import org.innovateuk.ifs.file.transactional.FileEntryService;
import org.innovateuk.ifs.file.transactional.FileHttpHeadersValidator;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.finance.handler.OrganisationFinanceDelegate;
import org.innovateuk.ifs.finance.mapper.ApplicationFinanceMapper;
import org.innovateuk.ifs.finance.mapper.ProjectFinanceMapper;
import org.innovateuk.ifs.finance.mapper.ProjectFinanceRowMapper;
import org.innovateuk.ifs.finance.repository.*;
import org.innovateuk.ifs.finance.transactional.FinanceRowService;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceRowService;
import org.innovateuk.ifs.form.mapper.FormInputResponseMapper;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.repository.FormInputResponseRepository;
import org.innovateuk.ifs.form.transactional.FormInputService;
import org.innovateuk.ifs.invite.mapper.*;
import org.innovateuk.ifs.invite.repository.*;
import org.innovateuk.ifs.invite.transactional.*;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer;
import org.innovateuk.ifs.notifications.service.senders.NotificationSender;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapper;
import org.innovateuk.ifs.organisation.repository.OrganisationAddressRepository;
import org.innovateuk.ifs.organisation.transactional.OrganisationInitialCreationService;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.profile.transactional.ProfileService;
import org.innovateuk.ifs.project.bankdetails.mapper.BankDetailsMapper;
import org.innovateuk.ifs.project.bankdetails.repository.BankDetailsRepository;
import org.innovateuk.ifs.project.bankdetails.transactional.BankDetailsService;
import org.innovateuk.ifs.project.financechecks.repository.CostCategoryRepository;
import org.innovateuk.ifs.project.financechecks.repository.CostCategoryTypeRepository;
import org.innovateuk.ifs.project.financechecks.repository.FinanceCheckRepository;
import org.innovateuk.ifs.project.financechecks.service.FinanceCheckService;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.EligibilityWorkflowHandler;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.ViabilityWorkflowHandler;
import org.innovateuk.ifs.project.grantofferletter.configuration.workflow.GrantOfferLetterWorkflowHandler;
import org.innovateuk.ifs.project.grantofferletter.transactional.GrantOfferLetterService;
import org.innovateuk.ifs.project.mapper.ProjectMapper;
import org.innovateuk.ifs.project.mapper.ProjectUserMapper;
import org.innovateuk.ifs.project.monitoringofficer.mapper.MonitoringOfficerMapper;
import org.innovateuk.ifs.project.monitoringofficer.repository.MonitoringOfficerRepository;
import org.innovateuk.ifs.project.monitoringofficer.transactional.MonitoringOfficerService;
import org.innovateuk.ifs.project.notes.service.FinanceCheckNotesService;
import org.innovateuk.ifs.project.otherdocuments.transactional.OtherDocumentsService;
import org.innovateuk.ifs.project.projectdetails.transactional.ProjectDetailsService;
import org.innovateuk.ifs.project.projectdetails.workflow.configuration.ProjectDetailsWorkflowHandler;
import org.innovateuk.ifs.project.queries.transactional.FinanceCheckQueriesService;
import org.innovateuk.ifs.project.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.repository.ProjectRepository;
import org.innovateuk.ifs.project.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.spendprofile.configuration.workflow.SpendProfileWorkflowHandler;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileRepository;
import org.innovateuk.ifs.project.spendprofile.transactional.SpendProfileService;
import org.innovateuk.ifs.project.status.transactional.StatusService;
import org.innovateuk.ifs.project.transactional.PartnerOrganisationService;
import org.innovateuk.ifs.project.transactional.ProjectService;
import org.innovateuk.ifs.project.users.ProjectUsersHelper;
import org.innovateuk.ifs.project.util.FinanceUtil;
import org.innovateuk.ifs.project.workflow.configuration.ProjectWorkflowHandler;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.sil.experian.service.SilExperianEndpoint;
import org.innovateuk.ifs.threads.attachments.mapper.AttachmentMapper;
import org.innovateuk.ifs.threads.attachments.repository.AttachmentRepository;
import org.innovateuk.ifs.threads.attachments.service.ProjectFinanceAttachmentService;
import org.innovateuk.ifs.threads.mapper.NoteMapper;
import org.innovateuk.ifs.threads.mapper.PostMapper;
import org.innovateuk.ifs.threads.mapper.QueryMapper;
import org.innovateuk.ifs.threads.repository.NoteRepository;
import org.innovateuk.ifs.threads.repository.QueryRepository;
import org.innovateuk.ifs.token.repository.TokenRepository;
import org.innovateuk.ifs.token.transactional.TokenService;
import org.innovateuk.ifs.user.mapper.*;
import org.innovateuk.ifs.user.repository.*;
import org.innovateuk.ifs.user.transactional.*;
import org.innovateuk.ifs.validator.util.ValidationUtil;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
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
    protected ProjectFinanceMapper projectFinanceMapperMock;

    @Mock
    protected ApplicationFinanceRepository applicationFinanceRepositoryMock;

    @Mock
    protected AssessmentRejectOutcomeMapper assessmentRejectOutcomeMapperMock;

    @Mock
    protected AssessmentMapper assessmentMapperMock;

    @Mock
    protected AssessmentService assessmentServiceMock;

    @Mock
    protected AssessmentWorkflowHandler assessmentWorkflowHandlerMock;

    @Mock
    protected AssessmentPanelInviteRepository assessmentPanelInviteRepositoryMock;

    @Mock
    protected AssessmentPanelInviteService assessmentPanelInviteServiceMock;

    @Mock
    protected AssessmentPanelService assessmentPanelServiceMock;

    @Mock
    protected AssessmentPanelParticipantRepository assessmentPanelParticipantRepositoryMock;

    @Mock
    protected AssessmentFundingDecisionOutcomeMapper assessmentFundingDecisionOutcomeMapperMock;

    @Mock
    protected AssessorFormInputResponseMapper assessorFormInputResponseMapperMock;

    @Mock
    protected AssessorFormInputResponseService assessorFormInputResponseServiceMock;

    @Mock
    protected AttachmentMapper attachmentMapperMock;

    @Mock
    protected AttachmentRepository attachmentRepositoryMock;

    @Mock
    protected FormInputResponseMapper formInputResponseMapperMock;

    @Mock
    protected FormInputResponseRepository formInputResponseRepositoryMock;

    @Mock
    protected UserRepository userRepositoryMock;

    @Mock
    protected ProfileRepository profileRepositoryMock;

    @Mock
    protected RoleRepository roleRepositoryMock;

    @Mock
    protected InviteRoleRepository inviteRoleRepositoryMock;

    @Mock
    protected RoleService roleServiceMock;

    @Mock
    protected ProcessRoleRepository processRoleRepositoryMock;

    @Mock
    protected CompetitionRepository competitionRepositoryMock;

    @Mock
    protected CompetitionService competitionServiceMock;

    @Mock
    protected CompetitionSetupService competitionSetupServiceMock;

    @Mock
    protected CompetitionSetupQuestionService competitionSetupQuestionServiceMock;

    @Mock
    protected CompetitionKeyStatisticsService competitionKeyStatisticsServiceMock;

    @Mock
    protected OrganisationRepository organisationRepositoryMock;

    @Mock
    protected ApplicationStatisticsRepository applicationStatisticsRepositoryMock;

    @Mock
    protected ApplicationCountSummaryPageMapper applicationCountSummaryPageMapperMock;

    @Mock
    protected FormInputRepository formInputRepositoryMock;

    @Mock
    protected SectionMapper sectionMapperMock;

    @Mock
    protected SectionRepository sectionRepositoryMock;

    @Mock
    protected SectionService sectionServiceMock;

    @Mock
    protected MonitoringOfficerRepository monitoringOfficerRepositoryMock;

    @Mock
    protected MonitoringOfficerMapper monitoringOfficerMapper;

    @Mock
    protected ApplicationService applicationServiceMock;

    @Mock
    protected ApplicationCountSummaryService applicationCountSummaryServiceMock;

    @Mock
    protected AssessorCountSummaryService assessorCountSummaryServiceMock;

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
    protected InviteOrganisationMapper inviteOrganisationMapperMock;

    @Mock
    protected InviteOrganisationService inviteOrganisationServiceMock;

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
    protected AssessmentPanelInviteMapper assessmentPanelInviteMapperMock;

    @Mock
    protected CompetitionMapper competitionMapperMock;

    @Mock
    protected CompetitionInviteService competitionInviteServiceMock;

    @Mock
    protected CompetitionAssessmentParticipantMapper competitionAssessmentParticipantMapperMock;

    @Mock
    protected CompetitionParticipantRoleMapper competitionParticipantRoleMapperMock;

    @Mock
    protected ParticipantStatusMapper participantStatusMapperMock;

    @Mock
    protected AssessmentPanelParticipantMapper assessmentPanelParticipantMapperMock;

    @Mock
    protected InviteProjectRepository inviteProjectRepositoryMock;

    @Mock
    protected InviteProjectService inviteProjectServiceMock;

    @Mock
    protected InviteUserService inviteUserServiceMock;

    @Mock
    protected AddressLookupService addressLookupServiceMock;

    @Mock
    protected AddressService addressService;

    @Mock
    protected OrganisationService organisationServiceMock;

    @Mock
    protected BaseUserService baseUserServiceMock;

    @Mock
    protected UserService userServiceMock;

    @Mock
    protected ProfileService profileServiceMock;

    @Mock
    protected AffiliationService affiliationServiceMock;

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
    protected ProcessRoleMapper processRoleMapperMock;

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
    protected FileEntryService fileEntryServiceMock;

    @Mock
    protected FileEntryMapper fileEntryMapperMock;

    @Mock
    protected AddressMapper addressMapperMock;

    @Mock
    protected AddressTypeRepository addressTypeRepositoryMock;

    @Mock
    protected OrganisationAddressRepository organisationAddressRepositoryMock;

    @Mock
    protected FinanceCheckQueriesService financeCheckQueriesService;

    @Mock
    protected FinanceCheckNotesService financeCheckNotesService;

    @Mock
    protected ProjectFinanceAttachmentService projectFinanceAttachmentServiceMock;

    @Mock
    protected ProjectService projectServiceMock;

    @Mock
    protected ProjectDetailsService projectDetailsServiceMock;

    @Mock
    protected MonitoringOfficerService monitoringOfficerServiceMock;

    @Mock
    protected OtherDocumentsService otherDocumentsServiceMock;

    @Mock
    protected GrantOfferLetterService grantOfferLetterServiceMock;

    @Mock
    protected StatusService statusServiceMock;

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
    protected BankDetailsMapper bankDetailsMapperMock;

    @Mock
    protected BankDetailsRepository bankDetailsRepositoryMock;

    @Mock
    protected BankDetailsService bankDetailsServiceMock;

    @Mock
    protected SilExperianEndpoint silExperianEndpointMock;

    @Mock
    protected SpendProfileRepository spendProfileRepositoryMock;

    @Mock
    protected SpendProfileService spendProfileServiceMock;

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
    protected ProjectFinanceRowService projectFinanceRowServiceMock;

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
    protected AssessorProfileMapper assessorProfileMapperMock;

    @Mock
    protected ProjectUsersHelper projectUsersHelperMock;

    @Mock
    protected ViabilityWorkflowHandler viabilityWorkflowHandlerMock;

    @Mock
    protected EligibilityWorkflowHandler eligibilityWorkflowHandlerMock;

    @Mock
    protected GrantOfferLetterWorkflowHandler golWorkflowHandlerMock;

    @Mock
    protected ProjectWorkflowHandler projectWorkflowHandlerMock;

    @Mock
    protected PartnerOrganisationRepository partnerOrganisationRepositoryMock;

    @Mock
    protected FinanceCheckService financeCheckServiceMock;

    @Mock
    protected FinanceCheckRepository financeCheckRepositoryMock;

    @Mock
    protected AgreementService agreementServiceMock;

    @Mock
    protected AgreementRepository agreementRepositoryMock;

    @Mock
    protected AgreementMapper agreementMapperMock;

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
    protected FinanceUtil financeUtilMock;

    @Mock
    protected OrganisationMapper organisationMapperMock;

    @Mock
    protected OrganisationTypeMapper organisationTypeMapperMock;

    @Mock
    protected NotificationSender notificationSenderMock;

    @Mock
    protected NotificationTemplateRenderer notificationTemplateRendererMock;

    @Mock
    protected ActivityStateRepository activityStateRepositoryMock;

    @Mock
    protected UsersRolesService usersRolesServiceMock;

    @Mock
    protected LoggedInUserSupplier loggedInUserSupplierMock;

    @Mock
    protected ProjectFinanceRowMapper projectFinanceRowMapperMock;

    @Mock
    protected QueryRepository queryRepositoryMock;

    @Mock
    protected NoteRepository noteRepositoryMock;

    @Mock
    protected QueryMapper queryMapper;

    @Mock
    protected NoteMapper noteMapper;

    @Mock
    protected PostMapper postMapper;

    @Mock
    protected ApplicationInnovationAreaService applicationInnovationAreaService;

    @Mock
    protected ApplicationAssessorMapper applicationAssessorMapperMock;

    @Mock
    protected ApplicationAssessorPageMapper applicationAssessorPageMapperMock;

    @Mock
    protected ValidationUtil validationUtilMock;

    @Mock
    protected ApplicationResearchCategoryService applicationResearchCategoryService;

    @Mock
    protected CompetitionSetupTransactionalService competitionSetupTransactionalServiceMock;

    @Mock
    protected ApplicationWorkflowHandler applicationWorkflowHandlerMock;

    @Mock
    protected IneligibleOutcomeMapper ineligibleOutcomeMapperMock;

    @Mock
    protected PartnerOrganisationService partnerOrganisationServiceMock;

    @Mock
    protected RoleInviteMapper roleInviteMapperMock;

    @Mock
    protected InviteService inviteServiceMock;

    @Mock
    protected OrganisationInitialCreationService organisationInitialCreationServiceMock;

    @Mock
    protected QuestionPriorityOrderService questionPriorityOrderServiceMock;

    @Mock
    protected QuestionNumberOrderService questionNumberOrderServiceMock;

    @Mock
    protected SpendProfileWorkflowHandler spendProfileWorkflowHandlerMock;

    @Before
    public void setupMockInjection() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);
    }
}
