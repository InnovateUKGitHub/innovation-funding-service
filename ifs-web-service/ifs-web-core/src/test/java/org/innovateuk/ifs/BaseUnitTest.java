package org.innovateuk.ifs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.alert.service.AlertRestService;
import org.innovateuk.ifs.application.UserApplicationRole;
import org.innovateuk.ifs.application.builder.QuestionResourceBuilder;
import org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder;
import org.innovateuk.ifs.application.builder.SectionResourceBuilder;
import org.innovateuk.ifs.application.finance.model.UserRole;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.view.*;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.assessment.service.AssessorRestService;
import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionKeyStatisticsRestService;
import org.innovateuk.ifs.competition.service.CompetitionsRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.category.GrantClaimCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.GrantClaim;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.FinanceRowRestService;
import org.innovateuk.ifs.finance.service.OrganisationDetailsRestService;
import org.innovateuk.ifs.finance.service.ProjectFinanceRowRestService;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteOrganisationRestService;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.invite.service.RejectionReasonRestService;
import org.innovateuk.ifs.organisation.service.OrganisationAddressRestService;
import org.innovateuk.ifs.populator.OrganisationDetailsModelPopulator;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.bankdetails.service.BankDetailsRestService;
import org.innovateuk.ifs.project.finance.ProjectFinanceService;
import org.innovateuk.ifs.project.financecheck.FinanceCheckService;
import org.innovateuk.ifs.project.grantofferletter.ProjectGrantOfferService;
import org.innovateuk.ifs.project.monitoringofficer.ProjectMonitoringOfficerService;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.project.service.ProjectStatusRestService;
import org.innovateuk.ifs.project.spendprofile.service.SpendProfileService;
import org.innovateuk.ifs.project.util.FinanceUtil;
import org.innovateuk.ifs.user.resource.*;
import org.innovateuk.ifs.user.service.*;
import org.innovateuk.ifs.util.CookieUtil;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.application.service.Futures.settable;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.*;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.form.resource.FormInputType.FILEUPLOAD;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.OrganisationTypeResourceBuilder.newOrganisationTypeResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

public class BaseUnitTest {

    public MockMvc mockMvc;
    public UserResource loggedInUser;
    public UserResource assessor;
    public UserResource applicant;
    public UserResource assessorAndApplicant;

    public UserResource assessorUser;
    public UserResource applicantUser;

    public UserAuthentication loggedInUserAuthentication;
    public TextEncryptor encryptor;

    protected final Log log = LogFactory.getLog(getClass());

    @Mock
    protected ApplicationAssessmentSummaryRestService applicationAssessmentSummaryRestService;
    @Mock
    protected ApplicationFinanceRestService applicationFinanceRestService;
    @Mock
    protected InviteOrganisationRestService inviteOrganisationRestService;
    @Mock
    protected UserAuthenticationService userAuthenticationService;
    @Mock
    public FormInputResponseService formInputResponseService;
    @Mock
    public FormInputResponseRestService formInputResponseRestService;
    @Mock
    public ApplicationService applicationService;
    @Mock
    protected CompetitionsRestService competitionRestService;
    @Mock
    protected FormInputRestService formInputRestService;
    @Mock
    public ProcessRoleService processRoleService;
    @Mock
    public UserService userService;
    @Mock
    protected AlertRestService alertRestService;
    @Mock
    protected FinanceService financeService;
    @Mock
    public FinanceRowRestService financeRowRestService;
    @Mock
    protected ApplicationRestService applicationRestService;
    @Mock
    public QuestionService questionService;
    @Mock
    public OrganisationService organisationService;
    @Mock
    protected OrganisationRestService organisationRestService;
    @Mock
    private OrganisationTypeRestService organisationTypeRestService;
    @Mock
    protected OrganisationAddressRestService organisationAddressRestService;
    @Mock
    protected PartnerOrganisationRestService partnerOrganisationRestService;
    @Mock
    public SectionService sectionService;
    @Mock
    protected SectionRestService sectionRestService;
    @Mock
    public CompetitionService competitionService;
    @Mock
    public InviteRestService inviteRestService;
    @Mock
    protected CompetitionInviteRestService competitionInviteRestService;
    @Mock
    public FinanceModelManager financeModelManager;
    @Mock
    protected DefaultFinanceModelManager defaultFinanceModelManager;
    @Mock
    protected DefaultFinanceFormHandler defaultFinanceFormHandler;
    @Mock
    protected DefaultProjectFinanceModelManager defaultProjectFinanceModelManager;
    @Mock
    public FinanceHandler financeHandler;
    @Mock
    protected ApplicationFinanceOverviewModelManager applicationFinanceOverviewModelManager;
    @Mock
    public FinanceFormHandler financeFormHandler;
    @Mock
    protected AssessorFeedbackRestService assessorFeedbackRestService;
    @Mock
    public ProjectService projectService;
    @Mock
    public ProjectMonitoringOfficerService projectMonitoringOfficerService;
    @Mock
    public ProjectFinanceService projectFinanceService;
    @Mock
    protected ProjectFinanceRowRestService projectFinanceRowRestService;
    @Mock
    public ProjectRestService projectRestService;
    @Mock
    public SpendProfileService spendProfileService;
    @Mock
    protected BankDetailsRestService bankDetailsRestService;
    @Mock
    protected RejectionReasonRestService rejectionReasonRestService;
    @Mock
    protected FinanceCheckService financeCheckServiceMock;
    @Mock
    protected FinanceUtil financeUtilMock;
    @Mock
    protected ProjectStatusRestService projectStatusRestService;
    @Mock
    private CookieUtil cookieUtil;
    @Mock
    protected UserRestService userRestServiceMock;
    @Mock
    protected AssessmentRestService assessmentRestService;
    @Mock
    protected AssessorRestService assessorRestService;
    @Mock
    protected ApplicationSummaryRestService applicationSummaryRestService;
    @Mock
    protected CompetitionKeyStatisticsRestService competitionKeyStatisticsRestServiceMock;
    @Mock
    protected AssessorFormInputResponseRestService assessorFormInputResponseRestService;
    @Mock
    protected ApplicationInnovationAreaRestService applicationInnovationAreaRestService;
    @Mock
    protected CategoryRestService categoryRestServiceMock;
    @Mock
    protected OrganisationDetailsRestService organisationDetailsRestService;
    @Mock
    protected ApplicationResearchCategoryRestService applicationResearchCategoryRestService;

    @Mock
    public ProjectGrantOfferService projectGrantOfferService;

    @Spy
    @InjectMocks
    private OrganisationDetailsModelPopulator organisationDetailsModelPopulator;

    @Mock
    public Environment env;

    @Mock
    public MessageSource messageSource;

    public List<ApplicationResource> applications;
    public List<SectionResource> sectionResources;
    public Map<Long, QuestionResource> questionResources;

    public Map<Long, FormInputResponseResource> formInputsToFormInputResponses;
    public List<CompetitionResource> competitionResources;
    public CompetitionResource competitionResource;
    private Long competitionId = 1l;
    public List<UserResource> users;
    public List<OrganisationResource> organisations;
    TreeSet<OrganisationResource> organisationSet;
    public List<ProcessRoleResource> assessorProcessRoleResources;
    public List<ProcessRoleResource> applicantRoles;
    public ApplicationFinanceResource applicationFinanceResource;
    public List<ProcessRoleResource> processRoles;

    public List<ProcessRoleResource> application1ProcessRoles;
    public List<ProcessRoleResource> application2ProcessRoles;
    public List<ProcessRoleResource> application3ProcessRoles;
    public List<ProcessRoleResource> application4ProcessRoles;
    public List<ProcessRoleResource> application5ProcessRoles;

    public List<OrganisationResource> application1Organisations;
    public List<OrganisationResource> application2Organisations;
    public List<OrganisationResource> application3Organisations;
    public List<OrganisationResource> application4Organisations;
    public List<OrganisationResource> application5Organisations;


    private Random randomGenerator;
    public OrganisationTypeResource businessOrganisationTypeResource;
    public OrganisationTypeResource researchOrganisationTypeResource;
    public OrganisationTypeResource rtoOrganisationTypeResource;
    public OrganisationTypeResource businessOrganisationType;
    public OrganisationTypeResource researchOrganisationType;
    public OrganisationTypeResource academicOrganisationType;
    public ApplicationInviteResource invite;
    public ApplicationInviteResource acceptedInvite;
    public ApplicationInviteResource existingUserInvite;

    public static final String INVITE_HASH = "b157879c18511630f220325b7a64cf3eb782759326d3cbb85e546e0d03e663ec711ec7ca65827a96";
    public static final String INVITE_HASH_EXISTING_USER = "cccccccccc630f220325b7a64cf3eb782759326d3cbb85e546e0d03e663ec711ec7ca65827a96";
    public static final String INVALID_INVITE_HASH = "aaaaaaa7a64cf3eb782759326d3cbb85e546e0d03e663ec711ec7ca65827a96";
    public static final String ACCEPTED_INVITE_HASH = "BBBBBBBBB7a64cf3eb782759326d3cbb85e546e0d03e663ec711ec7ca65827a96";
    ;


    public static InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/resources");
        viewResolver.setSuffix(".html");
        return viewResolver;
    }

    public <T> T attributeFromMvcResultModel(MvcResult result, String key) {
        return (T) result.getModelAndView().getModel().entrySet().stream()
                .filter(entry -> entry.getKey().equals(key))
                .map(entry -> entry.getValue())
                .findFirst().orElse(null);
    }

    @Before
    public void setup() {

        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        // start with fresh ids when using builders
        clearUniqueIds();

        applications = new ArrayList<>();
        questionResources = new HashMap<>();
        organisations = new ArrayList<>();

        setupUsers();
        setupOrganisationTypes();
        setupUserRoles();
    }

    private void setupUsers() {
        applicant = newUserResource().withId(1L)
                .withFirstName("James")
                .withLastName("Watts")
                .withEmail("james.watts@email.co.uk")
                .withUID("2aerg234-aegaeb-23aer").build();
        loggedInUser = applicant;

        UserResource user2 = newUserResource().withId(2L)
                .withFirstName("John")
                .withLastName("Patricks")
                .withEmail("john.patricks@email.co.uk")
                .withUID("6573ag-aeg32aeb-23aerr").build();

        assessor = newUserResource().withId(3L)
                .withFirstName("Clark")
                .withLastName("Baker")
                .withEmail("clark.baker@email.co.uk")
                .withUID("2522-34y34ah-hrt4420").build();

        assessorAndApplicant = newUserResource().withId(4L)
                .withFirstName("Fred")
                .withLastName("Smith")
                .withEmail("fred.smith@email.co.uk")
                .withUID("1234-abcdefgh-abc1234").build();

        users = asList(loggedInUser, user2, assessorAndApplicant);

        applicantUser = newUserResource().withId(1L).withFirstName("James").withLastName("Watts").withEmail("james.watts@email.co.uk").withUID("6573ag-aeg32aeb-23aerr").build();
        assessorUser = newUserResource().withId(3L).withFirstName("Clark").withLastName("Baker").withEmail("clark.baker@email.co.uk").withUID("2522-34y34ah-hrt4420").build();

        loggedInUserAuthentication = new UserAuthentication(loggedInUser);
    }

    public void setupOrganisationTypes() {

        businessOrganisationTypeResource = newOrganisationTypeResource().with(id(1L)).with(name("Business")).build();
        researchOrganisationTypeResource = newOrganisationTypeResource().with(id(2L)).with(name("Research")).build();
        rtoOrganisationTypeResource = newOrganisationTypeResource().with(id(3L)).with(name("Research and technology organisations (RTOs)")).build();

        // TODO DW - INFUND-1604 - remove when process roles are converted to DTOs
        businessOrganisationType = newOrganisationTypeResource().with(id(1L)).with(name("Business")).build();
        researchOrganisationType = newOrganisationTypeResource().with(id(2L)).with(name("Research")).build();
        academicOrganisationType = newOrganisationTypeResource().with(id(3L)).with(name("Research and technology organisations (RTOs)")).build();

        ArrayList<OrganisationTypeResource> organisationTypes = new ArrayList<>();
        organisationTypes.add(businessOrganisationTypeResource);
        organisationTypes.add(researchOrganisationTypeResource);
        organisationTypes.add(rtoOrganisationTypeResource);

        organisationTypes.add(new OrganisationTypeResource(4L, "Public sector organisation or charity", null));

        when(organisationTypeRestService.getAll()).thenReturn(restSuccess(organisationTypes));
        when(organisationTypeRestService.findOne(anyLong())).thenReturn(restSuccess(new OrganisationTypeResource(99L, "Unknown organisation type", null)));
        when(organisationTypeRestService.findOne(1L)).thenReturn(restSuccess(businessOrganisationTypeResource));
        when(organisationTypeRestService.findOne(2L)).thenReturn(restSuccess(researchOrganisationTypeResource));
        when(organisationTypeRestService.findOne(3L)).thenReturn(restSuccess(rtoOrganisationTypeResource));
    }

    public void loginDefaultUser() {
        when(userAuthenticationService.getAuthentication(any(HttpServletRequest.class))).thenReturn(loggedInUserAuthentication);
        when(userAuthenticationService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(loggedInUser);
    }

    public void loginUser(UserResource user) {
        UserAuthentication userAuthentication = new UserAuthentication(user);
        when(userAuthenticationService.getAuthentication(any(HttpServletRequest.class))).thenReturn(userAuthentication);
        when(userAuthenticationService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(user);
    }

    public void setupCompetition() {
        competitionResource = newCompetitionResource().with(id(competitionId)).with(name("Competition x")).with(description("Description afds")).
                withStartDate(ZonedDateTime.now().minusDays(2)).withEndDate(ZonedDateTime.now().plusDays(5)).withCompetitionStatus(CompetitionStatus.OPEN)
                .build();

        QuestionResourceBuilder questionResourceBuilder = newQuestionResource().withCompetition(competitionResource.getId());

        SectionResourceBuilder sectionResourceBuilder = newSectionResource().withCompetition(competitionResource.getId());

        QuestionResource q01Resource = setupQuestionResource(1L, "Application details", questionResourceBuilder);

        SectionResource sectionResource1 = sectionResourceBuilder.
                with(id(1L)).
                with(name("Application details")).
                withQuestions(simpleMap(singletonList(q01Resource), QuestionResource::getId)).
                withType(SectionType.GENERAL).
                build();

        QuestionResource q10Resource = setupQuestionResource(10L, "How does your project align with the scope of this competition?", questionResourceBuilder);

        SectionResource sectionResource2 = sectionResourceBuilder.
                with(id(2L)).
                with(name("Scope (Gateway question)")).
                withQuestions(simpleMap(singletonList(q10Resource), QuestionResource::getId)).
                withType(SectionType.GENERAL).
                build();

        QuestionResource q20Resource = setupQuestionResource(20L, "1. What is the business opportunity that this project addresses?", questionResourceBuilder);

        QuestionResource q21Resource = setupQuestionResource(21L, "2. What is the size of the market opportunity that this project might open up?", questionResourceBuilder);

        QuestionResource q22Resource = setupQuestionResource(22L, "3. How will the results of the project be exploited and disseminated?", questionResourceBuilder);

        QuestionResource q23Resource = setupQuestionResource(23L, "4. What economic, social and environmental benefits is the project expected to deliver?", questionResourceBuilder);

        SectionResource sectionResource3 = sectionResourceBuilder.
                with(id(3L)).
                with(name("Business proposition (Q1 - Q4)")).
                withQuestions(simpleMap(asList(q20Resource, q21Resource, q22Resource, q23Resource), QuestionResource::getId)).
                withType(SectionType.GENERAL).
                build();


        QuestionResource q30Resource = setupQuestionResource(30L, "5. What technical approach will be adopted and how will the project be managed?", questionResourceBuilder);

        QuestionResource q31Resource = setupFileQuestionResource(31L, "6. What is innovative about this project?", questionResourceBuilder);

        QuestionResource q32Resource = setupQuestionResource(32L, "7. What are the risks (technical, commercial and environmental) to project success? What is the project's risk management strategy?", questionResourceBuilder);

        QuestionResource q33Resource = setupQuestionResource(33L, "8. Does the project team have the right skills and experience and access to facilities to deliver the identified benefits?", questionResourceBuilder);

        SectionResource sectionResource4 = sectionResourceBuilder.
                with(id(4L)).
                with(name("Project approach (Q5 - Q8)")).
                withQuestions(simpleMap(asList(q30Resource, q31Resource, q32Resource, q33Resource), QuestionResource::getId)).
                withType(SectionType.GENERAL).
                build();

        SectionResource sectionResource5 = sectionResourceBuilder.with(id(5L)).with(name("Funding (Q9 - Q10)")).withType(SectionType.GENERAL).build();
        SectionResource sectionResource6 = sectionResourceBuilder.with(id(6L)).with(name("Finances")).withType(SectionType.GENERAL).build();
        SectionResource sectionResource7 = sectionResourceBuilder.with(id(7L)).with(name("Your finances")).withType(SectionType.FINANCE).build();
        SectionResource sectionResource8 = sectionResourceBuilder.with(id(8L)).with(name("Your project costs")).withType(SectionType.PROJECT_COST_FINANCES).withParentSection(sectionResource7.getId()).build();
        SectionResource sectionResource9 = sectionResourceBuilder.with(id(9L)).with(name("Your organisation")).withType(SectionType.ORGANISATION_FINANCES).withParentSection(sectionResource7.getId()).build();
        SectionResource sectionResource10 = sectionResourceBuilder.with(id(10L)).with(name("Your funding")).withType(SectionType.FUNDING_FINANCES).withParentSection(sectionResource7.getId()).build();

        sectionResource6.setChildSections(Arrays.asList(sectionResource7.getId()));
        sectionResource7.setChildSections(Arrays.asList(sectionResource8.getId(), sectionResource9.getId(), sectionResource10.getId()));

        sectionResources = asList(sectionResource1, sectionResource2, sectionResource3, sectionResource4, sectionResource5, sectionResource6, sectionResource7, sectionResource8, sectionResource9, sectionResource10);
        sectionResources.forEach(s -> {
                    s.setQuestionGroup(false);
                    s.setChildSections(new ArrayList<>());
                    when(sectionService.getById(s.getId())).thenReturn(s);
                }
        );
        when(sectionService.getSectionsForCompetitionByType(1L, SectionType.FINANCE)).thenReturn(Arrays.asList(sectionResource7));
        when(sectionService.getFinanceSection(1L)).thenReturn(sectionResource7);
        when(sectionService.getSectionsForCompetitionByType(1L, SectionType.ORGANISATION_FINANCES)).thenReturn(Arrays.asList(sectionResource9));
        when(sectionService.getSectionsForCompetitionByType(1L, SectionType.FUNDING_FINANCES)).thenReturn(Arrays.asList(sectionResource10));

        when(questionService.getQuestionsBySectionIdAndType(7L, QuestionType.COST)).thenReturn(Arrays.asList(q21Resource, q22Resource, q23Resource));
        when(questionService.getQuestionByCompetitionIdAndFormInputType(1L, FormInputType.APPLICATION_DETAILS)).thenReturn(ServiceResult.serviceSuccess(q01Resource));

        ArrayList<QuestionResource> questionList = new ArrayList<>();
        for (SectionResource section : sectionResources) {
            List<Long> sectionQuestions = section.getQuestions();
            if (sectionQuestions != null) {
                Map<Long, QuestionResource> questionsMap =
                        sectionQuestions.stream().collect(
                                toMap(identity(), q -> questionService.getById(q)));
                questionList.addAll(questionsMap.values());
                questionResources.putAll(questionsMap);

                when(sectionService.getQuestionsForSectionAndSubsections(eq(section.getId())))
                        .thenReturn(new HashSet<>(questionList.stream().map(QuestionResource::getId).collect(Collectors.toList())));
            }
        }

        sectionResource7.setQuestionGroup(true);

        questionResources.forEach((id, question) -> {
            when(questionService.getById(id)).thenReturn(question);
        });

        when(questionService.getPreviousQuestionBySection(any())).thenReturn(Optional.empty());
        when(questionService.getNextQuestionBySection(any())).thenReturn(Optional.empty());
        when(questionService.getNextQuestion(any())).thenReturn(Optional.empty());
        when(questionService.getPreviousQuestion(any())).thenReturn(Optional.empty());

        when(questionService.getNextQuestion(eq(q01Resource.getId()))).thenReturn(Optional.of(q10Resource));
        when(questionService.getPreviousQuestion(eq(q10Resource.getId()))).thenReturn(Optional.of(q01Resource));

        when(questionService.getNextQuestion(eq(q10Resource.getId()))).thenReturn(Optional.of(q20Resource));
        when(questionService.getPreviousQuestion(eq(q20Resource.getId()))).thenReturn(Optional.of(q10Resource));

        when(questionService.getNextQuestion(eq(q20Resource.getId()))).thenReturn(Optional.of(q21Resource));
        when(questionService.getPreviousQuestion(eq(q21Resource.getId()))).thenReturn(Optional.of(q20Resource));

        when(questionService.getNextQuestion(eq(q21Resource.getId()))).thenReturn(Optional.of(q22Resource));
        when(questionService.getPreviousQuestion(eq(q22Resource.getId()))).thenReturn(Optional.of(q21Resource));

        when(sectionService.getSectionByQuestionId(eq(q01Resource.getId()))).thenReturn(sectionResource1);
        when(sectionService.getSectionByQuestionId(eq(q10Resource.getId()))).thenReturn(sectionResource2);
        when(sectionService.getSectionByQuestionId(eq(q20Resource.getId()))).thenReturn(sectionResource3);
        when(sectionService.getSectionByQuestionId(eq(q21Resource.getId()))).thenReturn(sectionResource3);
        when(sectionService.getSectionByQuestionId(eq(q22Resource.getId()))).thenReturn(sectionResource3);

        when(sectionService.filterParentSections(anyList())).thenReturn(sectionResources);
        competitionResources = singletonList(competitionResource);
        when(questionService.findByCompetition(competitionResource.getId())).thenReturn(questionList);
        when(competitionRestService.getCompetitionById(competitionResource.getId())).thenReturn(restSuccess(competitionResource));
        when(competitionRestService.getAll()).thenReturn(restSuccess(competitionResources));
        when(competitionService.getById(any(Long.class))).thenReturn(competitionResource);

        when(formInputRestService.getByCompetitionIdAndScope(competitionResource.getId(), APPLICATION)).thenReturn(restSuccess(new ArrayList<>()));
    }

    public void setupUserRoles() {
        RoleResource assessorRole = new RoleResource(3L, UserRole.ASSESSOR.getRoleName());
        assessorRole.setUrl("assessor/dashboard");
        RoleResource applicantRole = new RoleResource(4L, UserRole.APPLICANT.getRoleName());
        applicantRole.setUrl("applicant/dashboard");
        applicant.setRoles(singletonList(applicantRole));
        assessor.setRoles(singletonList(assessorRole));
        assessorAndApplicant.setRoles(asList(applicantRole, assessorRole));
    }

    public void setupApplicationWithRoles(){
        // Build the backing applications.

        List<ApplicationResource> applicationResources = asList(
                newApplicationResource().with(id(1L)).with(name("Rovel Additive Manufacturing Process")).withStartDate(LocalDate.now().plusMonths(3))
                        .withApplicationState(ApplicationState.CREATED).withResearchCategory(newResearchCategoryResource().build()).build(),
                newApplicationResource().with(id(2L)).with(name("Providing sustainable childcare")).withStartDate(LocalDate.now().plusMonths(4))
                        .withApplicationState(ApplicationState.SUBMITTED).withResearchCategory(newResearchCategoryResource().build()).build(),
                newApplicationResource().with(id(3L)).with(name("Mobile Phone Data for Logistics Analytics")).withStartDate(LocalDate.now().plusMonths(5))
                        .withApplicationState(ApplicationState.APPROVED).withResearchCategory(newResearchCategoryResource().build()).build(),
                newApplicationResource().with(id(4L)).with(name("Using natural gas to heat homes")).withStartDate(LocalDate.now().plusMonths(6))
                        .withApplicationState(ApplicationState.REJECTED).withResearchCategory(newResearchCategoryResource().build()).build(),
                newApplicationResource().with(id(5L)).with(name("Rovel Additive Manufacturing Process Ltd")).withStartDate(LocalDate.now().plusMonths(3))
                        .withApplicationState(ApplicationState.CREATED).withResearchCategory(newResearchCategoryResource().build()).build()
        );

        Map<Long, ApplicationResource> idsToApplicationResources = applicationResources.stream().collect(toMap(a -> a.getId(), a -> a));

        RoleResource role1 = newRoleResource().withId(1L).withName(UserApplicationRole.LEAD_APPLICANT.getRoleName()).build();
        RoleResource role2 = newRoleResource().withId(2L).withName(UserApplicationRole.COLLABORATOR.getRoleName()).build();
        RoleResource assessorRole = newRoleResource().withId(3L).withName(UserRole.ASSESSOR.getRoleName()).build();

        OrganisationResource organisation1 = newOrganisationResource().withId(1L).withOrganisationType(businessOrganisationTypeResource.getId()).withName("Empire Ltd").build();
        OrganisationResource organisation2 = newOrganisationResource().withId(2L).withOrganisationType(researchOrganisationTypeResource.getId()).withName("Ludlow").build();
        OrganisationResource organisation3 = newOrganisationResource().withId(3L).withOrganisationType(rtoOrganisationTypeResource.getId()).withName("Ludlow Ltd").build();

        organisations = asList(organisation1, organisation2, organisation3);
        Comparator<OrganisationResource> compareById = Comparator.comparingLong(OrganisationResource::getId);
        organisationSet = new TreeSet<>(compareById);
        organisationSet.addAll(organisations);

        ProcessRoleResource processRole1 = newProcessRoleResource().with(id(1L)).withApplication(applicationResources.get(0).getId()).withUser(applicantUser).withRole(role1).withOrganisation(organisation1.getId()).build();
        ProcessRoleResource processRole2 = newProcessRoleResource().with(id(2L)).withApplication(applicationResources.get(0).getId()).withUser(applicantUser).withRole(role1).withOrganisation(organisation1.getId()).build();
        ProcessRoleResource processRole3 = newProcessRoleResource().with(id(3L)).withApplication(applicationResources.get(2).getId()).withUser(applicantUser).withRole(role1).withOrganisation(organisation1.getId()).build();
        ProcessRoleResource processRole4 = newProcessRoleResource().with(id(4L)).withApplication(applicationResources.get(3).getId()).withUser(applicantUser).withRole(role1).withOrganisation(organisation1.getId()).build();
        ProcessRoleResource processRole5 = newProcessRoleResource().with(id(5L)).withApplication(applicationResources.get(0).getId()).withUser(applicantUser).withRole(role2).withOrganisation(organisation2.getId()).build();
        ProcessRoleResource processRole6 = newProcessRoleResource().with(id(6L)).withApplication(applicationResources.get(1).getId()).withUser(assessorUser).withRole(assessorRole).withOrganisation(organisation1.getId()).build();
        ProcessRoleResource processRole7 = newProcessRoleResource().with(id(7L)).withApplication(applicationResources.get(2).getId()).withUser(assessorUser).withRole(assessorRole).withOrganisation(organisation1.getId()).build();
        ProcessRoleResource processRole8 = newProcessRoleResource().with(id(8L)).withApplication(applicationResources.get(0).getId()).withUser(assessorUser).withRole(assessorRole).withOrganisation(organisation1.getId()).build();
        ProcessRoleResource processRole9 = newProcessRoleResource().with(id(9L)).withApplication(applicationResources.get(3).getId()).withUser(assessorUser).withRole(assessorRole).withOrganisation(organisation1.getId()).build();
        ProcessRoleResource processRole10 = newProcessRoleResource().with(id(10L)).withApplication(applicationResources.get(1).getId()).withUser(applicantUser).withRole(role1).withOrganisation(organisation2.getId()).build();
        ProcessRoleResource processRole11 = newProcessRoleResource().with(id(11L)).withApplication(applicationResources.get(4).getId()).withUser(applicantUser).withRole(role1).withOrganisation(organisation3.getId()).build();

        assessorProcessRoleResources = asList(processRole6, processRole7, processRole8, processRole9);
        processRoles = asList(processRole1, processRole2, processRole3, processRole4, processRole5, processRole6, processRole7, processRole8, processRole9);
        applicantRoles = asList(processRole1, processRole2, processRole3, processRole4, processRole5);
        application1ProcessRoles = asList(processRole1, processRole2, processRole5);
        application2ProcessRoles = asList(processRole6, processRole10);
        application3ProcessRoles = asList(processRole3, processRole7);
        application4ProcessRoles = asList(processRole4, processRole9);
        application5ProcessRoles = asList(processRole11);

        application1Organisations = asList(organisation1, organisation2);
        application2Organisations = asList(organisation1, organisation2);
        application3Organisations = asList(organisation1);
        application4Organisations = asList(organisation1);
        application5Organisations = asList(organisation3);

        organisation1.setProcessRoles(simpleMap(asList(processRole1, processRole2, processRole3, processRole4, processRole7, processRole8, processRole8), ProcessRoleResource::getId));
        organisation2.setProcessRoles(simpleMap(singletonList(processRole5), ProcessRoleResource::getId));
        organisation3.setProcessRoles(simpleMap(singletonList(processRole11), ProcessRoleResource::getId));

        applicationResources.get(0).setCompetition(competitionResource.getId());
        applicationResources.get(1).setCompetition(competitionResource.getId());
        applicationResources.get(2).setCompetition(competitionResource.getId());
        applicationResources.get(3).setCompetition(competitionResource.getId());
        applicationResources.get(4).setCompetition(competitionResource.getId());

        applications = applicationResources;

        when(sectionService.filterParentSections(sectionResources)).thenReturn(sectionResources);
        when(sectionService.getCompleted(applicationResources.get(0).getId(), organisation1.getId())).thenReturn(asList(1L, 2L));
        when(sectionService.getInCompleted(applicationResources.get(0).getId())).thenReturn(asList(3L, 4L));
        when(processRoleService.findProcessRole(applicant.getId(), applicationResources.get(0).getId())).thenReturn(processRole1);
        when(processRoleService.findProcessRole(applicant.getId(), applicationResources.get(1).getId())).thenReturn(processRole2);
        when(processRoleService.findProcessRole(applicant.getId(), applicationResources.get(2).getId())).thenReturn(processRole3);
        when(processRoleService.findProcessRole(applicant.getId(), applicationResources.get(3).getId())).thenReturn(processRole4);
        when(processRoleService.findProcessRole(users.get(0).getId(), applicationResources.get(0).getId())).thenReturn(processRole5);
        when(processRoleService.findProcessRole(assessor.getId(), applicationResources.get(1).getId())).thenReturn(processRole6);
        when(processRoleService.findProcessRole(assessor.getId(), applicationResources.get(2).getId())).thenReturn(processRole7);
        when(processRoleService.findProcessRole(assessor.getId(), applicationResources.get(0).getId())).thenReturn(processRole8);
        when(processRoleService.findProcessRole(assessor.getId(), applicationResources.get(3).getId())).thenReturn(processRole9);
        when(processRoleService.findProcessRole(applicant.getId(), applicationResources.get(4).getId())).thenReturn(processRole11);

        when(processRoleService.findProcessRolesByApplicationId(applicationResources.get(0).getId())).thenReturn(application1ProcessRoles);
        when(processRoleService.findProcessRolesByApplicationId(applicationResources.get(1).getId())).thenReturn(application2ProcessRoles);
        when(processRoleService.findProcessRolesByApplicationId(applicationResources.get(2).getId())).thenReturn(application3ProcessRoles);
        when(processRoleService.findProcessRolesByApplicationId(applicationResources.get(3).getId())).thenReturn(application4ProcessRoles);
        when(processRoleService.findProcessRolesByApplicationId(applicationResources.get(4).getId())).thenReturn(application5ProcessRoles);

        Map<Long, Set<Long>> completedMap = new HashMap<>();
        completedMap.put(organisation1.getId(), new TreeSet<>());
        completedMap.put(organisation2.getId(), new TreeSet<>());
        when(sectionService.getCompletedSectionsByOrganisation(applicationResources.get(0).getId())).thenReturn(completedMap);
        when(sectionService.getCompletedSectionsByOrganisation(applicationResources.get(1).getId())).thenReturn(completedMap);
        when(sectionService.getCompletedSectionsByOrganisation(applicationResources.get(2).getId())).thenReturn(completedMap);

        processRoles.forEach(pr -> when(applicationService.findByProcessRoleId(pr.getId())).thenReturn(ServiceResult.serviceSuccess(idsToApplicationResources.get(pr.getApplicationId()))));

        when(applicationRestService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(restSuccess(applications));
        when(applicationService.getById(applications.get(0).getId())).thenReturn(applications.get(0));
        when(applicationService.getById(applications.get(1).getId())).thenReturn(applications.get(1));
        when(applicationService.getById(applications.get(2).getId())).thenReturn(applications.get(2));
        when(applicationService.getById(applications.get(3).getId())).thenReturn(applications.get(3));
        when(applicationService.getById(applications.get(4).getId())).thenReturn(applications.get(4));

        when(organisationService.getOrganisationById(organisationSet.first().getId())).thenReturn(organisationSet.first());
        when(organisationService.getOrganisationByIdForAnonymousUserFlow(organisationSet.first().getId())).thenReturn(organisationSet.first());
        when(organisationService.getOrganisationType(loggedInUser.getId(), applications.get(0).getId())).thenReturn(OrganisationTypeEnum.BUSINESS.getOrganisationTypeId());
        when(organisationService.getOrganisationForUser(loggedInUser.getId(), application1ProcessRoles)).thenReturn(Optional.of(organisationSet.first()));
        when(userService.isLeadApplicant(loggedInUser.getId(), applications.get(0))).thenReturn(true);
        when(userService.getLeadApplicantProcessRoleOrNull(applications.get(0))).thenReturn(processRole1);
        when(userService.getLeadApplicantProcessRoleOrNull(applications.get(1))).thenReturn(processRole2);
        when(userService.getLeadApplicantProcessRoleOrNull(applications.get(2))).thenReturn(processRole3);
        when(userService.getLeadApplicantProcessRoleOrNull(applications.get(3))).thenReturn(processRole4);
        when(userService.getLeadApplicantProcessRoleOrNull(applications.get(4))).thenReturn(processRole11);

        when(userService.findById(loggedInUser.getId())).thenReturn(loggedInUser);

        processRoles.forEach(processRole -> when(processRoleService.getById(processRole.getId())).thenReturn(settable(processRole)));

        when(sectionService.getById(1L)).thenReturn(sectionResources.get(0));
        when(sectionService.getById(3L)).thenReturn(sectionResources.get(2));

        organisations.forEach(organisation -> when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation)));
        organisations.forEach(organisation -> when(organisationRestService.getOrganisationByIdForAnonymousUserFlow(organisation.getId())).thenReturn(restSuccess(organisation)));
    }

    public void setupApplicationResponses() {
        ApplicationResource application = applications.get(0);

        when(formInputRestService.getById(anyLong())).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            return restSuccess(newFormInputResource().with(id((Long) args[0])).build());
        });

        List<Long> formInputIds = questionResources.get(1L).getFormInputs();
        List<FormInputResponseResource> formInputResponses = newFormInputResponseResource().withFormInputs(formInputIds).
                with(idBasedValues("Value "))
                .build(formInputIds.size());

        when(formInputResponseRestService.getResponsesByApplicationId(application.getId())).thenReturn(restSuccess(formInputResponses));
        formInputsToFormInputResponses = formInputResponses.stream().collect(toMap(formInputResponseResource -> formInputResponseResource.getFormInput(), identity()));
        when(formInputResponseService.mapFormInputResponsesToFormInput(formInputResponses)).thenReturn(formInputsToFormInputResponses);
    }

    public void setupFinances() {
        ApplicationResource application = applications.get(0);
        applicationFinanceResource = new ApplicationFinanceResource(1L, application.getId(), organisations.get(0).getId(), 1L);
        Map<FinanceRowType, FinanceRowCostCategory> organisationFinances = new HashMap<>();
        FinanceRowCostCategory costCategory = new GrantClaimCategory();
        costCategory.addCost(new GrantClaim(1L, 50));
        organisationFinances.put(FinanceRowType.FINANCE, costCategory);
        applicationFinanceResource.setFinanceOrganisationDetails(organisationFinances);
        when(financeService.getApplicationFinanceDetails(loggedInUser.getId(), application.getId())).thenReturn(applicationFinanceResource);
        when(financeService.getApplicationFinance(loggedInUser.getId(), application.getId())).thenReturn(applicationFinanceResource);
        when(applicationFinanceRestService.getResearchParticipationPercentage(anyLong())).thenReturn(restSuccess(0.0));
        when(financeHandler.getFinanceFormHandler(1L)).thenReturn(defaultFinanceFormHandler);
        when(financeHandler.getFinanceModelManager(1L)).thenReturn(defaultFinanceModelManager);
    }

    public void setupInvites() {
        when(inviteRestService.getInvitesByApplication(isA(Long.class))).thenReturn(restSuccess(emptyList()));
        InviteOrganisationResource inviteOrganisation = new InviteOrganisationResource(2L, "Invited Organisation Ltd", null, null);

        invite = new ApplicationInviteResource();
        invite.setStatus(InviteStatus.SENT);
        invite.setApplication(1L);
        invite.setName("Some Invitee");
        invite.setHash(INVITE_HASH);
        String email = "invited@email.com";
        invite.setEmail(email);
        invite.setInviteOrganisation(inviteOrganisation.getId());
        invite.setCompetitionId(competitionId);
        inviteOrganisation.setInviteResources(Arrays.asList(invite));

        when(inviteRestService.getInviteByHash(eq(INVITE_HASH))).thenReturn(restSuccess(invite));
        when(inviteOrganisationRestService.getByIdForAnonymousUserFlow(eq(invite.getInviteOrganisation()))).thenReturn(restSuccess(inviteOrganisation));
        when(inviteOrganisationRestService.put(any())).thenReturn(restSuccess());
        when(inviteRestService.checkExistingUser(eq(INVITE_HASH))).thenReturn(restSuccess(false));
        when(inviteRestService.checkExistingUser(eq(INVALID_INVITE_HASH))).thenReturn(restFailure(notFoundError(UserResource.class, email)));
        when(inviteRestService.getInviteByHash(eq(INVALID_INVITE_HASH))).thenReturn(restFailure(notFoundError(ApplicationResource.class, INVALID_INVITE_HASH)));
        when(inviteRestService.getInviteOrganisationByHash(INVITE_HASH)).thenReturn(restSuccess(new InviteOrganisationResource()));

        acceptedInvite = new ApplicationInviteResource();
        acceptedInvite.setStatus(InviteStatus.OPENED);
        acceptedInvite.setApplication(1L);
        acceptedInvite.setName("Some Invitee");
        acceptedInvite.setHash(ACCEPTED_INVITE_HASH);
        acceptedInvite.setEmail(email);
        when(inviteRestService.getInviteByHash(eq(ACCEPTED_INVITE_HASH))).thenReturn(restSuccess(acceptedInvite));

        existingUserInvite = new ApplicationInviteResource();
        existingUserInvite.setStatus(InviteStatus.SENT);
        existingUserInvite.setApplication(1L);
        existingUserInvite.setName("Some Invitee");
        existingUserInvite.setHash(INVITE_HASH_EXISTING_USER);
        existingUserInvite.setEmail("existing@email.com");
        when(inviteRestService.checkExistingUser(eq(INVITE_HASH_EXISTING_USER))).thenReturn(restSuccess(true));
        when(inviteRestService.getInviteByHash(eq(INVITE_HASH_EXISTING_USER))).thenReturn(restSuccess(existingUserInvite));

        when(inviteRestService.getInvitesByApplication(isA(Long.class))).thenReturn(restSuccess(emptyList()));
        when(inviteRestService.getInviteOrganisationByHash(INVITE_HASH_EXISTING_USER)).thenReturn(restSuccess(new InviteOrganisationResource()));

    }

    public void setupQuestionStatus(ApplicationResource application) {
        List<QuestionStatusResource> questionStatusResources = QuestionStatusResourceBuilder.newQuestionStatusResource()
                .withApplication(application)
                .with(questionStatusResource -> {
                    questionStatusResource.setAssigneeUserId(1L);
                    questionStatusResource.setMarkedAsComplete(false);
                }).build(1);

        when(questionService.findQuestionStatusesByQuestionAndApplicationId(1l, application.getId())).thenReturn(questionStatusResources);
    }

    public void setupCookieUtil() {
        String password = "mysecretpassword";
        String salt = "109240124012412412";
        encryptor = Encryptors.text(password, salt);

        ReflectionTestUtils.setField(cookieUtil, "cookieSecure", TRUE);
        ReflectionTestUtils.setField(cookieUtil, "cookieHttpOnly", FALSE);
        ReflectionTestUtils.setField(cookieUtil, "encryptionPassword", password);
        ReflectionTestUtils.setField(cookieUtil, "encryptionSalt", salt);
        ReflectionTestUtils.setField(cookieUtil, "encryptor", encryptor);

        doCallRealMethod().when(cookieUtil).saveToCookie(any(HttpServletResponse.class), any(String.class), any(String.class));
        doCallRealMethod().when(cookieUtil).getCookie(any(HttpServletRequest.class), any(String.class));
        doCallRealMethod().when(cookieUtil).getCookieValue(any(HttpServletRequest.class), any(String.class));
        doCallRealMethod().when(cookieUtil).removeCookie(any(HttpServletResponse.class), any(String.class));
    }

    public String getDecryptedCookieValue(Cookie[] cookies, String cookieName) {
        Optional<Cookie> cookieFound = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findAny();

        if (cookieFound.isPresent()) {
            return encryptor.decrypt(cookieFound.get().getValue());
        }

        return null;
    }

    private QuestionResource setupQuestionResource(Long id, String name, QuestionResourceBuilder questionResourceBuilder) {
        List<FormInputResource> formInputs = newFormInputResource().with(incrementingIds(1)).build(1);
        QuestionResource questionResource = questionResourceBuilder.with(id(id)).with(name(name)).
                withFormInputs(simpleMap(formInputs, FormInputResource::getId)).
                build();
        when(questionService.getById(questionResource.getId())).thenReturn(questionResource);
        when(formInputRestService.getByQuestionIdAndScope(questionResource.getId(), APPLICATION)).thenReturn(restSuccess(formInputs));
        return questionResource;
    }

    private QuestionResource setupFileQuestionResource(Long id, String name, QuestionResourceBuilder questionResourceBuilder) {
        List<FormInputResource> formInputs = newFormInputResource()
                .with(incrementingIds(1))
                .withType(null, FILEUPLOAD)
                .build(2);
        QuestionResource questionResource = questionResourceBuilder.with(id(id)).with(name(name)).
                withFormInputs(simpleMap(formInputs, FormInputResource::getId)).
                build();
        when(questionService.getById(questionResource.getId())).thenReturn(questionResource);
        when(formInputRestService.getByQuestionIdAndScope(questionResource.getId(), APPLICATION)).thenReturn(restSuccess(formInputs));
        return questionResource;
    }
}
