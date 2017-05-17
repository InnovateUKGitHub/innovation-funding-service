package org.innovateuk.ifs.testdata.builders;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.affiliation.transactional.AffiliationService;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.mapper.IneligibleOutcomeMapper;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.repository.QuestionRepository;
import org.innovateuk.ifs.application.repository.SectionRepository;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.transactional.*;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.transactional.AssessmentService;
import org.innovateuk.ifs.assessment.transactional.AssessorFormInputResponseService;
import org.innovateuk.ifs.assessment.transactional.AssessorService;
import org.innovateuk.ifs.assessment.transactional.CompetitionInviteService;
import org.innovateuk.ifs.assessment.workflow.configuration.AssessmentWorkflowHandler;
import org.innovateuk.ifs.category.repository.CategoryRepository;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.category.repository.InnovationSectorRepository;
import org.innovateuk.ifs.category.repository.ResearchCategoryRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionFunderRepository;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.CompetitionTypeRepository;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupService;
import org.innovateuk.ifs.competition.transactional.MilestoneService;
import org.innovateuk.ifs.file.repository.FileEntryRepository;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.transactional.FinanceRowService;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.repository.FormInputResponseRepository;
import org.innovateuk.ifs.form.transactional.FormInputService;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.invite.repository.CompetitionInviteRepository;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.invite.transactional.InviteService;
import org.innovateuk.ifs.invite.transactional.RejectionReasonService;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.profile.transactional.ProfileService;
import org.innovateuk.ifs.project.bankdetails.transactional.BankDetailsService;
import org.innovateuk.ifs.project.financechecks.service.FinanceCheckService;
import org.innovateuk.ifs.project.monitoringofficer.transactional.ProjectMonitoringOfficerService;
import org.innovateuk.ifs.project.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.spendprofile.transactional.SpendProfileService;
import org.innovateuk.ifs.project.transactional.ProjectService;
import org.innovateuk.ifs.publiccontent.repository.ContentEventRepository;
import org.innovateuk.ifs.publiccontent.repository.ContentGroupRepository;
import org.innovateuk.ifs.publiccontent.repository.PublicContentRepository;
import org.innovateuk.ifs.publiccontent.transactional.ContentGroupService;
import org.innovateuk.ifs.publiccontent.transactional.PublicContentService;
import org.innovateuk.ifs.token.repository.TokenRepository;
import org.innovateuk.ifs.token.transactional.TokenService;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.*;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.transactional.*;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.BaseIntegrationTest.setLoggedInUser;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.ASSESSOR;
import static org.innovateuk.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * Base Builder for persistent data builders.  Wraps each build step in a transaction.  Provides a location for
 * service lookup.
 */
public abstract class BaseDataBuilder<T, S> extends BaseBuilder<T, S> {

    public static final String COMP_ADMIN_EMAIL = "john.doe@innovateuk.test";
    public static final String IFS_SYSTEM_MAINTENANCE_USER_EMAIL = "ifs_system_maintenance_user@innovateuk.org";

    protected static boolean initialisedServices = false;
    
    protected static ServiceLocator serviceLocator;
    protected static BaseUserService baseUserService;
    protected static UserService userService;
    protected static CompetitionService competitionService;
    protected static CompetitionTypeRepository competitionTypeRepository;
    protected static CategoryRepository categoryRepository;
    protected static InnovationAreaRepository innovationAreaRepository;
    protected static InnovationSectorRepository innovationSectorRepository;
    protected static ResearchCategoryRepository researchCategoryRepository;
    protected static CompetitionSetupService competitionSetupService;
    protected static PublicContentService publicContentService;
    protected static PublicContentRepository publicContentRepository;
    protected static ContentGroupRepository contentGroupRepository;
    protected static ContentGroupService contentGroupService;
    protected static ContentEventRepository contentEventRepository;
    protected static OrganisationService organisationService;
    protected static OrganisationTypeService organisationTypeService;
    protected static UserRepository userRepository;
    protected static ProfileRepository profileRepository;
    protected static RegistrationService registrationService;
    protected static RoleRepository roleRepository;
    protected static OrganisationRepository organisationRepository;
    protected static TokenRepository tokenRepository;
    protected static TokenService tokenService;
    protected static InviteService inviteService;
    protected static CompAdminEmailRepository compAdminEmailRepository;
    protected static MilestoneService milestoneService;
    protected static ApplicationService applicationService;
    protected static QuestionService questionService;
    protected static FormInputService formInputService;
    protected static FormInputResponseRepository formInputResponseRepository;
    protected static ApplicationRepository applicationRepository;
    protected static ApplicationFundingService applicationFundingService;
    protected static ProjectService projectService;
    protected static ProjectMonitoringOfficerService projectMonitoringOfficerService;
    protected static FinanceRowService financeRowService;
    protected static SectionService sectionService;
    protected static ProjectFinanceEmailRepository projectFinanceEmailRepository;
    protected static UsersRolesService usersRolesService;
    protected static ApplicationInviteRepository applicationInviteRepository;
    protected static EthnicityRepository ethnicityRepository;
    protected static RoleService roleService;
    protected static CompetitionInviteRepository competitionInviteRepository;
    protected static CompetitionRepository competitionRepository;
    protected static CompetitionFunderRepository competitionFunderRepository;
    protected static AssessorService assessorService;
    protected static CompetitionParticipantRepository competitionParticipantRepository;
    protected static CompetitionInviteService competitionInviteService;
    protected static TestService testService;
    protected static AssessmentRepository assessmentRepository;
    protected static AssessmentService assessmentService;
    protected static AssessmentWorkflowHandler assessmentWorkflowHandler;
    protected static ProcessRoleRepository processRoleRepository;
    protected static ActivityStateRepository activityStateRepository;
    protected static SectionRepository sectionRepository;
    protected static QuestionRepository questionRepository;
    protected static FormInputRepository formInputRepository;
    protected static FileEntryRepository fileEntryRepository;
    protected static ApplicationFinanceRepository applicationFinanceRepository;
    protected static ProjectUserRepository projectUserRepository;
    protected static BankDetailsService bankDetailsService;
    protected static SpendProfileService spendProfileService;
    protected static FinanceCheckService financeCheckService;
    protected static RejectionReasonService rejectionReasonService;
    protected static ProfileService profileService;
    protected static AffiliationService affiliationService;
    protected static ApplicationInnovationAreaService applicationInnovationAreaService;
    protected static AssessorFormInputResponseService assessorFormInputResponseService;
    protected static IneligibleOutcomeMapper ineligibleOutcomeMapper;

    private static Cache<Long, List<QuestionResource>> questionsByCompetitionId = CacheBuilder.newBuilder().build();

    private static Cache<String, UserResource> usersByEmailAddress = CacheBuilder.newBuilder().build();

    private static Cache<String, UserResource> usersByEmailAddressInternal = CacheBuilder.newBuilder().build();

    private static Cache<Long, UserResource> usersById = CacheBuilder.newBuilder().build();

    private static Cache<Pair<Long, String>, ProcessRoleResource> applicantsByApplicationIdAndEmail = CacheBuilder.newBuilder().build();

    private static Cache<Long, ProcessRoleResource> leadApplicantsByApplicationId = CacheBuilder.newBuilder().build();

    private static Cache<String, OrganisationResource> organisationsByName = CacheBuilder.newBuilder().build();

    public BaseDataBuilder(List<BiConsumer<Integer, T>> newActions, ServiceLocator serviceLocator) {

        super(newActions);
        
        if (!initialisedServices) {

            initialisedServices = true;

            BaseDataBuilder.serviceLocator = serviceLocator;
            userService = serviceLocator.getBean(UserService.class);
            competitionService = serviceLocator.getBean(CompetitionService.class);
            competitionTypeRepository = serviceLocator.getBean(CompetitionTypeRepository.class);
            categoryRepository = serviceLocator.getBean(CategoryRepository.class);
            competitionSetupService = serviceLocator.getBean(CompetitionSetupService.class);
            organisationService = serviceLocator.getBean(OrganisationService.class);
            organisationTypeService = serviceLocator.getBean(OrganisationTypeService.class);
            userRepository = serviceLocator.getBean(UserRepository.class);
            registrationService = serviceLocator.getBean(RegistrationService.class);
            roleRepository = serviceLocator.getBean(RoleRepository.class);
            organisationRepository = serviceLocator.getBean(OrganisationRepository.class);
            tokenRepository = serviceLocator.getBean(TokenRepository.class);
            tokenService = serviceLocator.getBean(TokenService.class);
            inviteService = serviceLocator.getBean(InviteService.class);
            compAdminEmailRepository = serviceLocator.getBean(CompAdminEmailRepository.class);
            milestoneService = serviceLocator.getBean(MilestoneService.class);
            applicationService = serviceLocator.getBean(ApplicationService.class);
            questionService = serviceLocator.getBean(QuestionService.class);
            formInputService = serviceLocator.getBean(FormInputService.class);
            formInputResponseRepository = serviceLocator.getBean(FormInputResponseRepository.class);
            applicationRepository = serviceLocator.getBean(ApplicationRepository.class);
            applicationFundingService = serviceLocator.getBean(ApplicationFundingService.class);
            projectService = serviceLocator.getBean(ProjectService.class);
            projectMonitoringOfficerService = serviceLocator.getBean(ProjectMonitoringOfficerService.class);
            financeRowService = serviceLocator.getBean(FinanceRowService.class);
            sectionService = serviceLocator.getBean(SectionService.class);
            projectFinanceEmailRepository = serviceLocator.getBean(ProjectFinanceEmailRepository.class);
            usersRolesService = serviceLocator.getBean(UsersRolesService.class);
            applicationInviteRepository = serviceLocator.getBean(ApplicationInviteRepository.class);
            ethnicityRepository = serviceLocator.getBean(EthnicityRepository.class);
            roleService = serviceLocator.getBean(RoleService.class);
            competitionInviteRepository = serviceLocator.getBean(CompetitionInviteRepository.class);
            competitionRepository = serviceLocator.getBean(CompetitionRepository.class);
            assessorService = serviceLocator.getBean(AssessorService.class);
            competitionParticipantRepository = serviceLocator.getBean(CompetitionParticipantRepository.class);
            competitionInviteService = serviceLocator.getBean(CompetitionInviteService.class);
            testService = serviceLocator.getBean(TestService.class);
            assessmentRepository = serviceLocator.getBean(AssessmentRepository.class);
            assessmentService = serviceLocator.getBean(AssessmentService.class);
            assessmentWorkflowHandler = serviceLocator.getBean(AssessmentWorkflowHandler.class);
            processRoleRepository = serviceLocator.getBean(ProcessRoleRepository.class);
            activityStateRepository = serviceLocator.getBean(ActivityStateRepository.class);
            sectionRepository = serviceLocator.getBean(SectionRepository.class);
            questionRepository = serviceLocator.getBean(QuestionRepository.class);
            formInputRepository = serviceLocator.getBean(FormInputRepository.class);
            fileEntryRepository = serviceLocator.getBean(FileEntryRepository.class);
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
            publicContentRepository = serviceLocator.getBean(PublicContentRepository.class);
            contentEventRepository = serviceLocator.getBean(ContentEventRepository.class);
            contentGroupRepository = serviceLocator.getBean(ContentGroupRepository.class);
            contentGroupService = serviceLocator.getBean(ContentGroupService.class);
            assessorFormInputResponseService = serviceLocator.getBean(AssessorFormInputResponseService.class);
            applicationInnovationAreaService = serviceLocator.getBean(ApplicationInnovationAreaService.class);
            ineligibleOutcomeMapper = serviceLocator.getBean(IneligibleOutcomeMapper.class);
        }
    }

    protected UserResource compAdmin() {
        return retrieveUserByEmailInternal(COMP_ADMIN_EMAIL, UserRoleType.COMP_ADMIN);
    }

    protected UserResource systemRegistrar() {
        return retrieveUserByEmailInternal(IFS_SYSTEM_MAINTENANCE_USER_EMAIL, UserRoleType.SYSTEM_REGISTRATION_USER);
    }

    protected UserResource retrieveUserByEmail(String emailAddress) {
        return fromCache(emailAddress, usersByEmailAddress, () ->
                doAs(systemRegistrar(), () -> userService.findByEmail(emailAddress).getSuccessObjectOrThrowException()));
    }

    protected UserResource retrieveUserById(Long id) {
        return fromCache(id, usersById, () -> doAs(systemRegistrar(), () -> baseUserService.getUserById(id).getSuccessObjectOrThrowException()));
    }

    protected ProcessRoleResource retrieveApplicantByEmail(String emailAddress, Long applicationId) {
        return fromCache(Pair.of(applicationId, emailAddress), applicantsByApplicationIdAndEmail, () -> {
            UserResource user = retrieveUserByEmail(emailAddress);
            return doAs(user, () ->
                    usersRolesService.getProcessRoleByUserIdAndApplicationId(user.getId(), applicationId).
                            getSuccessObjectOrThrowException());
        });
    }

    protected ProcessRoleResource retrieveLeadApplicant(Long applicationId) {

        return fromCache(applicationId, leadApplicantsByApplicationId, () ->
                doAs(compAdmin(), () ->
                simpleFindFirst(usersRolesService.getProcessRolesByApplicationId(applicationId).
                        getSuccessObjectOrThrowException(), pr -> pr.getRoleName().equals(LEADAPPLICANT.getName())).get()));
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

    private List<QuestionResource> retrieveQuestionsByCompetitionId(Long competitionId) {
        return fromCache(competitionId, questionsByCompetitionId, () ->
                questionService.findByCompetition(competitionId).getSuccessObjectOrThrowException());
    }

    protected OrganisationResource retrieveOrganisationResourceByName(String organisationName) {
        return fromCache(organisationName, organisationsByName, () -> doAs(systemRegistrar(), () -> {
            Organisation organisation = retrieveOrganisationByName(organisationName);
            return organisationService.findById(organisation.getId()).getSuccessObjectOrThrowException();
        }));
    }

    protected ProcessRole retrieveAssessorByApplicationNameAndUser(String applicationName, UserResource user) {

        return testService.doWithinTransaction(() -> {

            Application application = applicationRepository.findByName(applicationName).get(0);

            return processRoleRepository.findByUserAndApplicationId(userRepository.findOne(user.getId()),
                    application.getId())
                    .stream()
                    .filter(x -> x.getRole().getName().equals(ASSESSOR.getName()))
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

    protected UserResource retrieveUserByEmailInternal(String email, UserRoleType role) {
        return fromCache(email, usersByEmailAddressInternal, () -> {
            User user = userRepository.findByEmail(email).get();
            return newUserResource().
                    withRolesGlobal(newRoleResource().withType(role).build(1)).
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
}
