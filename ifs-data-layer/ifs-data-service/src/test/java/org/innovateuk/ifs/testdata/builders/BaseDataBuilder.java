package org.innovateuk.ifs.testdata.builders;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.affiliation.transactional.AffiliationService;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.mapper.IneligibleOutcomeMapper;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.repository.FormInputResponseRepository;
import org.innovateuk.ifs.application.transactional.*;
import org.innovateuk.ifs.assessment.repository.AssessmentInviteRepository;
import org.innovateuk.ifs.assessment.repository.AssessmentParticipantRepository;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.transactional.AssessmentInviteService;
import org.innovateuk.ifs.assessment.transactional.AssessmentService;
import org.innovateuk.ifs.assessment.transactional.AssessorFormInputResponseService;
import org.innovateuk.ifs.assessment.transactional.AssessorService;
import org.innovateuk.ifs.assessment.workflow.configuration.AssessmentWorkflowHandler;
import org.innovateuk.ifs.category.repository.CategoryRepository;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.category.repository.InnovationSectorRepository;
import org.innovateuk.ifs.category.repository.ResearchCategoryRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.*;
import org.innovateuk.ifs.competition.transactional.CompetitionAssessmentConfigService;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.competition.transactional.MilestoneService;
import org.innovateuk.ifs.competitionsetup.repository.CompetitionDocumentConfigRepository;
import org.innovateuk.ifs.competitionsetup.transactional.CompetitionSetupFinanceService;
import org.innovateuk.ifs.competitionsetup.transactional.CompetitionSetupService;
import org.innovateuk.ifs.file.repository.FileEntryRepository;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceRowService;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceService;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceService;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.innovateuk.ifs.form.repository.SectionRepository;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.transactional.FormInputService;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.innovateuk.ifs.form.transactional.SectionService;
import org.innovateuk.ifs.fundingdecision.transactional.ApplicationFundingService;
import org.innovateuk.ifs.interview.repository.InterviewInviteRepository;
import org.innovateuk.ifs.interview.transactional.InterviewAllocationService;
import org.innovateuk.ifs.interview.transactional.InterviewAssignmentService;
import org.innovateuk.ifs.interview.transactional.InterviewInviteService;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.invite.transactional.AcceptApplicationInviteService;
import org.innovateuk.ifs.invite.transactional.ApplicationInviteService;
import org.innovateuk.ifs.invite.transactional.RejectionReasonService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationInitialCreationService;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.organisation.transactional.OrganisationTypeService;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.profile.transactional.ProfileService;
import org.innovateuk.ifs.project.bankdetails.transactional.BankDetailsService;
import org.innovateuk.ifs.project.core.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.core.transactional.ProjectService;
import org.innovateuk.ifs.project.documents.mapper.ProjectDocumentsMapper;
import org.innovateuk.ifs.project.documents.repository.ProjectDocumentRepository;
import org.innovateuk.ifs.project.documents.transactional.DocumentsService;
import org.innovateuk.ifs.project.financechecks.service.FinanceCheckService;
import org.innovateuk.ifs.project.grantofferletter.transactional.GrantOfferLetterService;
import org.innovateuk.ifs.project.monitoringofficer.transactional.LegacyMonitoringOfficerService;
import org.innovateuk.ifs.project.projectdetails.transactional.ProjectDetailsService;
import org.innovateuk.ifs.project.spendprofile.transactional.SpendProfileService;
import org.innovateuk.ifs.project.state.transactional.ProjectStateService;
import org.innovateuk.ifs.publiccontent.repository.ContentEventRepository;
import org.innovateuk.ifs.publiccontent.repository.ContentGroupRepository;
import org.innovateuk.ifs.publiccontent.repository.PublicContentRepository;
import org.innovateuk.ifs.publiccontent.transactional.ContentGroupService;
import org.innovateuk.ifs.publiccontent.transactional.PublicContentService;
import org.innovateuk.ifs.question.transactional.template.QuestionSetupTemplateService;
import org.innovateuk.ifs.review.repository.ReviewInviteRepository;
import org.innovateuk.ifs.review.transactional.ReviewInviteService;
import org.innovateuk.ifs.review.transactional.ReviewService;
import org.innovateuk.ifs.testdata.services.TestService;
import org.innovateuk.ifs.token.repository.TokenRepository;
import org.innovateuk.ifs.token.transactional.TokenService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.RoleProfileStatusRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.*;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.BaseIntegrationTest.setLoggedInUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * Base Builder for persistent data builders.  Wraps each build step in a transaction.  Provides a location for
 * service lookup.
 */
public abstract class BaseDataBuilder<T, S> extends BaseBuilder<T, S> {

    public static final String IFS_SYSTEM_MAINTENANCE_USER_EMAIL = "ifs_system_maintenance_user@innovateuk.org";
    public static final String IFS_SYSTEM_REGISTRAR_USER_EMAIL = "ifs_web_user@innovateuk.org";

    protected ServiceLocator serviceLocator;
    protected String compAdminEmail;
    protected String projectFinanceEmail;
    protected BaseUserService baseUserService;
    protected UserService userService;
    protected CompetitionService competitionService;
    protected CompetitionTypeRepository competitionTypeRepository;
    protected CategoryRepository categoryRepository;
    protected InnovationAreaRepository innovationAreaRepository;
    protected InnovationSectorRepository innovationSectorRepository;
    protected ResearchCategoryRepository researchCategoryRepository;
    protected CompetitionSetupService competitionSetupService;
    protected QuestionSetupService questionSetupService;
    protected QuestionSetupTemplateService questionSetupTemplateService;
    protected PublicContentService publicContentService;
    protected CompetitionSetupFinanceService competitionSetupFinanceService;
    protected PublicContentRepository publicContentRepository;
    protected ContentGroupRepository contentGroupRepository;
    protected ContentGroupService contentGroupService;
    protected ContentEventRepository contentEventRepository;
    protected OrganisationService organisationService;
    protected OrganisationInitialCreationService organisationInitialCreationService;
    protected OrganisationTypeService organisationTypeService;
    protected UserRepository userRepository;
    protected ProfileRepository profileRepository;
    protected RegistrationService registrationService;
    protected OrganisationRepository organisationRepository;
    protected TokenRepository tokenRepository;
    protected TokenService tokenService;
    protected ApplicationInviteService applicationInviteService;
    protected AcceptApplicationInviteService acceptApplicationInviteService;
    protected MilestoneService milestoneService;
    protected ApplicationService applicationService;
    protected ApplicationNotificationService applicationNotificationService;
    protected QuestionService questionService;
    protected QuestionStatusService questionStatusService;
    protected TestQuestionService testQuestionService;
    protected FormInputService formInputService;
    protected FormInputResponseService formInputResponseService;
    protected FormInputResponseRepository formInputResponseRepository;
    protected ApplicationRepository applicationRepository;
    protected ApplicationFundingService applicationFundingService;
    protected ProjectService projectService;
    protected ProjectStateService projectStateService;
    protected ProjectDetailsService projectDetailsService;
    protected LegacyMonitoringOfficerService monitoringOfficerService;
    protected ApplicationFinanceRowService financeRowCostsService;
    protected SectionService sectionService;
    protected SectionStatusService sectionStatusService;
    protected UsersRolesService usersRolesService;
    protected ApplicationInviteRepository applicationInviteRepository;
    protected AssessmentInviteRepository assessmentInviteRepository;
    protected CompetitionRepository competitionRepository;
    protected CompetitionFunderRepository competitionFunderRepository;
    protected AssessorService assessorService;
    protected AssessmentParticipantRepository assessmentParticipantRepository;
    protected AssessmentInviteService assessmentInviteService;
    protected TestService testService;
    protected AssessmentRepository assessmentRepository;
    protected AssessmentService assessmentService;
    protected AssessmentWorkflowHandler assessmentWorkflowHandler;
    protected ReviewInviteRepository reviewInviteRepository;
    protected ReviewInviteService reviewInviteService;
    protected ReviewService reviewService;
    protected InterviewInviteRepository interviewInviteRepository;
    protected InterviewInviteService interviewInviteService;
    protected InterviewAssignmentService interviewAssignmentService;
    protected InterviewAllocationService interviewAllocationService;
    protected ProcessRoleRepository processRoleRepository;
    protected SectionRepository sectionRepository;
    protected QuestionRepository questionRepository;
    protected FormInputRepository formInputRepository;
    protected FileEntryRepository fileEntryRepository;
    protected ProjectDocumentRepository projectDocumentRepository;
    protected DocumentsService documentsService;
    protected ProjectRepository projectRepository;
    protected CompetitionDocumentConfigRepository competitionDocumentConfigRepository;
    protected PartnerOrganisationRepository partnerOrganisationRepository;
    protected ApplicationFinanceRepository applicationFinanceRepository;
    protected ProjectUserRepository projectUserRepository;
    protected BankDetailsService bankDetailsService;
    protected SpendProfileService spendProfileService;
    protected ProjectFinanceService projectFinanceService;
    protected FinanceCheckService financeCheckService;
    protected RejectionReasonService rejectionReasonService;
    protected ProfileService profileService;
    protected AffiliationService affiliationService;
    protected ApplicationInnovationAreaService applicationInnovationAreaService;
    protected AssessorFormInputResponseService assessorFormInputResponseService;
    protected IneligibleOutcomeMapper ineligibleOutcomeMapper;
    protected ProjectDocumentsMapper projectDocumentsMapper;
    protected ApplicationResearchCategoryService applicationResearchCategoryService;
    protected ApplicationFinanceService financeService;
    protected GrantOfferLetterService grantOfferLetterService;
    protected RoleProfileStatusService roleProfileStatusService;
    protected RoleProfileStatusRepository roleProfileStatusRepository;
    protected CompetitionOrganisationConfigRepository competitionOrganisationConfigRepository;
    protected CompetitionAssessmentConfigService competitionAssessmentConfigService;

    private static Cache<Long, List<QuestionResource>> questionsByCompetitionId = CacheBuilder.newBuilder().build();

    private static Cache<Long, List<FormInputResource>> formInputsByQuestionId = CacheBuilder.newBuilder().build();

    private static Cache<String, UserResource> usersByEmailAddress = CacheBuilder.newBuilder().build();

    private static Cache<String, UserResource> usersByEmailAddressInternal = CacheBuilder.newBuilder().build();

    private static Cache<Long, UserResource> usersById = CacheBuilder.newBuilder().build();

    private static Cache<Pair<Long, String>, ProcessRoleResource> applicantsByApplicationIdAndEmail = CacheBuilder.newBuilder().build();

    private static Cache<Long, ProcessRoleResource> leadApplicantsByApplicationId = CacheBuilder.newBuilder().build();

    private static Cache<String, OrganisationResource> organisationsByName = CacheBuilder.newBuilder().build();

    public BaseDataBuilder(List<BiConsumer<Integer, T>> newActions, ServiceLocator serviceLocator) {

        super(newActions);

        this.serviceLocator = serviceLocator;
        userService = serviceLocator.getBean(UserService.class);
        competitionService = serviceLocator.getBean(CompetitionService.class);
        competitionTypeRepository = serviceLocator.getBean(CompetitionTypeRepository.class);
        categoryRepository = serviceLocator.getBean(CategoryRepository.class);
        competitionSetupService = serviceLocator.getBean(CompetitionSetupService.class);
        organisationService = serviceLocator.getBean(OrganisationService.class);
        organisationInitialCreationService = serviceLocator.getBean(OrganisationInitialCreationService.class);
        organisationTypeService = serviceLocator.getBean(OrganisationTypeService.class);
        userRepository = serviceLocator.getBean(UserRepository.class);
        registrationService = serviceLocator.getBean(RegistrationService.class);
        organisationRepository = serviceLocator.getBean(OrganisationRepository.class);
        tokenRepository = serviceLocator.getBean(TokenRepository.class);
        tokenService = serviceLocator.getBean(TokenService.class);
        applicationInviteService = serviceLocator.getBean(ApplicationInviteService.class);
        acceptApplicationInviteService = serviceLocator.getBean(AcceptApplicationInviteService.class);
        milestoneService = serviceLocator.getBean(MilestoneService.class);
        applicationService = serviceLocator.getBean(ApplicationService.class);
        applicationNotificationService = serviceLocator.getBean(ApplicationNotificationService.class);
        questionService = serviceLocator.getBean(QuestionService.class);
        questionStatusService = serviceLocator.getBean(QuestionStatusService.class);
        testQuestionService = serviceLocator.getBean(TestQuestionService.class);
        formInputService = serviceLocator.getBean(FormInputService.class);
        formInputResponseService = serviceLocator.getBean(FormInputResponseService.class);
        formInputResponseRepository = serviceLocator.getBean(FormInputResponseRepository.class);
        applicationRepository = serviceLocator.getBean(ApplicationRepository.class);
        applicationFundingService = serviceLocator.getBean(ApplicationFundingService.class);
        projectService = serviceLocator.getBean(ProjectService.class);
        projectStateService = serviceLocator.getBean(ProjectStateService.class);
        projectDetailsService = serviceLocator.getBean(ProjectDetailsService.class);
        monitoringOfficerService = serviceLocator.getBean(LegacyMonitoringOfficerService.class);
        financeRowCostsService = serviceLocator.getBean(ApplicationFinanceRowService.class);
        financeService = serviceLocator.getBean(ApplicationFinanceService.class);
        sectionService = serviceLocator.getBean(SectionService.class);
        sectionStatusService = serviceLocator.getBean(SectionStatusService.class);
        usersRolesService = serviceLocator.getBean(UsersRolesService.class);
        applicationInviteRepository = serviceLocator.getBean(ApplicationInviteRepository.class);
        assessmentInviteRepository = serviceLocator.getBean(AssessmentInviteRepository.class);
        competitionRepository = serviceLocator.getBean(CompetitionRepository.class);
        assessorService = serviceLocator.getBean(AssessorService.class);
        assessmentParticipantRepository = serviceLocator.getBean(AssessmentParticipantRepository.class);
        assessmentInviteService = serviceLocator.getBean(AssessmentInviteService.class);
        testService = serviceLocator.getBean(TestService.class);
        assessmentRepository = serviceLocator.getBean(AssessmentRepository.class);
        assessmentService = serviceLocator.getBean(AssessmentService.class);
        assessmentWorkflowHandler = serviceLocator.getBean(AssessmentWorkflowHandler.class);
        reviewInviteRepository = serviceLocator.getBean(ReviewInviteRepository.class);
        reviewInviteService = serviceLocator.getBean(ReviewInviteService.class);
        reviewService = serviceLocator.getBean(ReviewService.class);
        interviewInviteRepository = serviceLocator.getBean(InterviewInviteRepository.class);
        interviewInviteService = serviceLocator.getBean(InterviewInviteService.class);
        interviewAssignmentService = serviceLocator.getBean(InterviewAssignmentService.class);
        interviewAllocationService = serviceLocator.getBean(InterviewAllocationService.class);
        processRoleRepository = serviceLocator.getBean(ProcessRoleRepository.class);
        sectionRepository = serviceLocator.getBean(SectionRepository.class);
        questionRepository = serviceLocator.getBean(QuestionRepository.class);
        questionSetupService = serviceLocator.getBean(QuestionSetupService.class);
        questionSetupTemplateService = serviceLocator.getBean(QuestionSetupTemplateService.class);
        formInputRepository = serviceLocator.getBean(FormInputRepository.class);
        fileEntryRepository = serviceLocator.getBean(FileEntryRepository.class);
        documentsService = serviceLocator.getBean(DocumentsService.class);
        projectDocumentRepository = serviceLocator.getBean(ProjectDocumentRepository.class);
        projectRepository = serviceLocator.getBean(ProjectRepository.class);
        competitionDocumentConfigRepository = serviceLocator.getBean(CompetitionDocumentConfigRepository.class);
        partnerOrganisationRepository = serviceLocator.getBean(PartnerOrganisationRepository.class);
        applicationFinanceRepository = serviceLocator.getBean(ApplicationFinanceRepository.class);
        projectUserRepository = serviceLocator.getBean(ProjectUserRepository.class);
        bankDetailsService = serviceLocator.getBean(BankDetailsService.class);
        spendProfileService = serviceLocator.getBean(SpendProfileService.class);
        financeCheckService = serviceLocator.getBean(FinanceCheckService.class);
        competitionFunderRepository = serviceLocator.getBean(CompetitionFunderRepository.class);
        innovationAreaRepository = serviceLocator.getBean(InnovationAreaRepository.class);
        innovationSectorRepository = serviceLocator.getBean(InnovationSectorRepository.class);
        researchCategoryRepository = serviceLocator.getBean(ResearchCategoryRepository.class);
        rejectionReasonService = serviceLocator.getBean(RejectionReasonService.class);
        profileService = serviceLocator.getBean(ProfileService.class);
        affiliationService = serviceLocator.getBean(AffiliationService.class);
        baseUserService = serviceLocator.getBean(BaseUserService.class);
        profileRepository = serviceLocator.getBean(ProfileRepository.class);
        publicContentService = serviceLocator.getBean(PublicContentService.class);
        competitionSetupFinanceService = serviceLocator.getBean(CompetitionSetupFinanceService.class);
        publicContentRepository = serviceLocator.getBean(PublicContentRepository.class);
        contentEventRepository = serviceLocator.getBean(ContentEventRepository.class);
        contentGroupRepository = serviceLocator.getBean(ContentGroupRepository.class);
        contentGroupService = serviceLocator.getBean(ContentGroupService.class);
        assessorFormInputResponseService = serviceLocator.getBean(AssessorFormInputResponseService.class);
        applicationInnovationAreaService = serviceLocator.getBean(ApplicationInnovationAreaService.class);
        ineligibleOutcomeMapper = serviceLocator.getBean(IneligibleOutcomeMapper.class);
        projectDocumentsMapper = serviceLocator.getBean(ProjectDocumentsMapper.class);
        applicationResearchCategoryService = serviceLocator.getBean(ApplicationResearchCategoryService.class);
        grantOfferLetterService = serviceLocator.getBean(GrantOfferLetterService.class);
        roleProfileStatusService = serviceLocator.getBean(RoleProfileStatusService.class);
        roleProfileStatusRepository = serviceLocator.getBean((RoleProfileStatusRepository.class));
        compAdminEmail = serviceLocator.getCompAdminEmail();
        projectFinanceEmail = serviceLocator.getProjectFinanceEmail();
        competitionOrganisationConfigRepository = serviceLocator.getBean((CompetitionOrganisationConfigRepository.class));
        competitionAssessmentConfigService = serviceLocator.getBean(CompetitionAssessmentConfigService.class);
        projectFinanceService = serviceLocator.getBean(ProjectFinanceService.class);
    }

    protected UserResource compAdmin() {
        return retrieveUserByEmailInternal(compAdminEmail, COMP_ADMIN);
    }

    protected UserResource systemRegistrar() {
        return retrieveUserByEmailInternal(IFS_SYSTEM_REGISTRAR_USER_EMAIL, SYSTEM_REGISTRATION_USER);
    }

    protected UserResource projectFinanceUser() {
        return retrieveUserByEmail(projectFinanceEmail);
    }

    protected UserResource ifsAdmin() {
        return retrieveUserByEmailInternal("arden.pimenta@innovateuk.test", IFS_ADMINISTRATOR);
    }

    protected UserResource retrieveUserByEmail(String emailAddress) {
        return fromCache(emailAddress, usersByEmailAddress, () ->
                doAs(systemRegistrar(), () -> userService.findByEmail(emailAddress).getSuccess()));
    }

    protected UserResource retrieveUserById(Long id) {
        return fromCache(id, usersById, () -> doAs(systemRegistrar(), () -> baseUserService.getUserById(id).getSuccess()));
    }

    protected ProcessRoleResource retrieveApplicantByEmail(String emailAddress, Long applicationId) {
        return fromCache(Pair.of(applicationId, emailAddress), applicantsByApplicationIdAndEmail, () -> {
            UserResource user = retrieveUserByEmail(emailAddress);
            return doAs(user, () ->
                    usersRolesService.getProcessRoleByUserIdAndApplicationId(user.getId(), applicationId).
                            getSuccess());
        });
    }

    protected ProcessRoleResource retrieveLeadApplicant(Long applicationId) {

        return fromCache(applicationId, leadApplicantsByApplicationId, () ->
                doAs(compAdmin(), () ->
                simpleFindFirst(usersRolesService.getProcessRolesByApplicationId(applicationId).
                        getSuccess(), pr -> pr.getRole() == LEADAPPLICANT).get()));
    }

    protected Organisation retrieveOrganisationByName(String organisationName) {
        return organisationRepository.findOneByName(organisationName);
    }

    protected Competition retrieveCompetitionByName(String competitionName) {
        return competitionRepository.findByName(competitionName).get(0);
    }

    protected QuestionResource retrieveQuestionByCompetitionAndName(String questionName, Long competitionId) {
        return simpleFindFirst(retrieveQuestionsByCompetitionId(competitionId), q -> questionName.equals(q.getName())).get();
    }

    protected List<QuestionResource> retrieveQuestionsByCompetitionId(Long competitionId) {
        return fromCache(competitionId, questionsByCompetitionId, () -> doAs(compAdmin(), () ->
                questionService.findByCompetition(competitionId).getSuccess()));
    }

    protected List<FormInputResource> retrieveFormInputsByQuestionId(QuestionResource question) {
        return fromCache(question.getId(), formInputsByQuestionId, () ->
                formInputService.findByQuestionId(question.getId()).getSuccess());
    }

    protected OrganisationResource retrieveOrganisationResourceByName(String organisationName) {
        return fromCache(organisationName, organisationsByName, () -> doAs(systemRegistrar(), () -> {
            Organisation organisation = retrieveOrganisationByName(organisationName);
            return organisationService.findById(organisation.getId()).getSuccess();
        }));
    }

    protected ProcessRole retrieveAssessorByApplicationNameAndUser(String applicationName, UserResource user) {

        return testService.doWithinTransaction(() -> {

            Application application = applicationRepository.findByName(applicationName).get(0);

            return processRoleRepository.findByUserAndApplicationId(userRepository.findById(user.getId()).get(),
                    application.getId())
                    .stream()
                    .filter(x -> x.getRole() == ASSESSOR)
                    .findFirst()
                    .get();
        });
    }

    protected <T> T doAs(UserResource user, Supplier<T> action) {
        UserResource currentUser = setLoggedInUser(user);
        try {
            return action.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            setLoggedInUser(currentUser);
        }
    }

    protected void doAs(UserResource user, Runnable action) {
        UserResource currentUser = setLoggedInUser(user);
        try {
            action.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            setLoggedInUser(currentUser);
        }
    }

    protected S asCompAdmin(Consumer<T> action) {
        return with(data -> doAs(compAdmin(), () -> action.accept(data)));
    }

    protected S asIfsAdmin(Consumer<T> action) {
        return with(data -> doAs(ifsAdmin(), () -> action.accept(data)));
    }

    protected UserResource retrieveUserByEmailInternal(String email, Role role) {
        return fromCache(email, usersByEmailAddressInternal, () -> {
            User user = userRepository.findByEmail(email).get();
            return newUserResource().
                    withRolesGlobal(asList(role)).
                    withId(user.getId()).
                    build();
        });
    }

    protected<K, V> V fromCache(K key, Cache<K, V> cache, Callable<V> loadingFunction) {
        try {
            return cache.get(key, loadingFunction);
        } catch (ExecutionException e) {
            throw new RuntimeException("Exception encountered whilst reading from Cache", e);
        }
    }

    protected List<FinanceRowItem> getCostItems(long applicationFinanceId, FinanceRowType type) {
        return financeService.getApplicationFinanceById(applicationFinanceId).andOnSuccess(applicationFinance -> {
            return financeService.financeDetails(applicationFinance.getApplication(), applicationFinance.getOrganisation()).andOnSuccessReturn(finance ->
                    finance.getFinanceOrganisationDetails().values().stream().flatMap(category -> category.getCosts().stream())
                            .filter(row -> row.getCostType() == type)
                            .collect(toList())
            );
        }).getSuccess();
    }
}