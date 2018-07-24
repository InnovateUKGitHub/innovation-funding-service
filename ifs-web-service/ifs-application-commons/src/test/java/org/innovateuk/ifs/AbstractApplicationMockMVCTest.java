package org.innovateuk.ifs;

import org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder;
import org.innovateuk.ifs.application.populator.finance.service.FinanceService;
import org.innovateuk.ifs.application.populator.finance.view.DefaultFinanceFormHandler;
import org.innovateuk.ifs.application.populator.finance.view.DefaultFinanceModelManager;
import org.innovateuk.ifs.application.populator.finance.view.FinanceViewHandlerProvider;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.category.GrantClaimCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.GrantClaim;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.builder.QuestionResourceBuilder;
import org.innovateuk.ifs.form.builder.SectionResourceBuilder;
import org.innovateuk.ifs.form.resource.*;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.service.*;
import org.mockito.Mock;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.application.service.Futures.settable;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.*;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.resource.OrganisationSize.SMALL;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.form.resource.FormInputType.FILEUPLOAD;
import static org.innovateuk.ifs.form.resource.FormInputType.TEXTAREA;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeResourceBuilder.newOrganisationTypeResource;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

public abstract class AbstractApplicationMockMVCTest<ControllerType> extends AbstractInviteMockMVCTest<ControllerType> {
    @Mock
    protected ApplicationService applicationService;
    @Mock
    protected SectionService sectionService;
    @Mock
    protected ApplicationRestService applicationRestService;
    @Mock
    protected OrganisationService organisationService;
    @Mock
    protected CompetitionService competitionService;
    @Mock
    protected QuestionService questionService;
    @Mock
    protected QuestionRestService questionRestService;
    @Mock
    protected FormInputResponseRestService formInputResponseRestService;
    @Mock
    protected ProcessRoleService processRoleService;
    @Mock
    protected DefaultFinanceModelManager defaultFinanceModelManager;
    @Mock
    protected FormInputRestService formInputRestService;
    @Mock
    protected FinanceViewHandlerProvider financeViewHandlerProvider;
    @Mock
    protected FinanceService financeService;
    @Mock
    protected ApplicationFinanceRestService applicationFinanceRestService;
    @Mock
    protected DefaultFinanceFormHandler defaultFinanceFormHandler;
    @Mock
    protected UserService userService;

    @Mock
    private OrganisationTypeRestService organisationTypeRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private FormInputResponseService formInputResponseService;

    public List<ApplicationResource> applications = new ArrayList<>();
    public List<SectionResource> sectionResources;
    public Map<Long, QuestionResource> questionResources = new HashMap<>();

    public Map<Long, FormInputResponseResource> formInputsToFormInputResponses;
    public List<CompetitionResource> competitionResources;
    public CompetitionResource competitionResource;
    public List<OrganisationResource> organisations = new ArrayList<>();
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

    public OrganisationTypeResource businessOrganisationTypeResource;
    public OrganisationTypeResource researchOrganisationTypeResource;
    public OrganisationTypeResource rtoOrganisationTypeResource;
    public OrganisationTypeResource businessOrganisationType;
    public OrganisationTypeResource researchOrganisationType;
    public OrganisationTypeResource academicOrganisationType;
    public ApplicationInviteResource invite;
    public ApplicationInviteResource acceptedInvite;
    public ApplicationInviteResource existingUserInvite;

    public static final String INVITE_HASH =
            "b157879c18511630f220325b7a64cf3eb782759326d3cbb85e546e0d03e663ec711ec7ca65827a96";
    public static final String INVITE_HASH_EXISTING_USER =
            "cccccccccc630f220325b7a64cf3eb782759326d3cbb85e546e0d03e663ec711ec7ca65827a96";
    public static final String INVALID_INVITE_HASH = "aaaaaaa7a64cf3eb782759326d3cbb85e546e0d03e663ec711ec7ca65827a96";
    public static final String ACCEPTED_INVITE_HASH =
            "BBBBBBBBB7a64cf3eb782759326d3cbb85e546e0d03e663ec711ec7ca65827a96";

    public void setupOrganisationTypes() {

        businessOrganisationTypeResource = newOrganisationTypeResource().with(id(1L)).with(name("Business")).build();
        researchOrganisationTypeResource = newOrganisationTypeResource().with(id(2L)).with(name("Research")).build();
        rtoOrganisationTypeResource = newOrganisationTypeResource().with(id(3L)).with(name("Research and technology " +
                "organisations (RTOs)")).build();

        // TODO DW - INFUND-1604 - remove when process roles are converted to DTOs
        businessOrganisationType = newOrganisationTypeResource().with(id(1L)).with(name("Business")).build();
        researchOrganisationType = newOrganisationTypeResource().with(id(2L)).with(name("Research")).build();
        academicOrganisationType = newOrganisationTypeResource().with(id(3L)).with(name("Research and technology " +
                "organisations (RTOs)")).build();

        ArrayList<OrganisationTypeResource> organisationTypes = new ArrayList<>();
        organisationTypes.add(businessOrganisationTypeResource);
        organisationTypes.add(researchOrganisationTypeResource);
        organisationTypes.add(rtoOrganisationTypeResource);

        organisationTypes.add(new OrganisationTypeResource(4L, "Public sector organisation or charity", null));

        when(organisationTypeRestService.getAll()).thenReturn(restSuccess(organisationTypes));
        when(organisationTypeRestService.findOne(anyLong())).thenReturn(restSuccess(new OrganisationTypeResource(99L,
                "Unknown organisation type", null)));
        when(organisationTypeRestService.findOne(1L)).thenReturn(restSuccess(businessOrganisationTypeResource));
        when(organisationTypeRestService.findOne(2L)).thenReturn(restSuccess(researchOrganisationTypeResource));
        when(organisationTypeRestService.findOne(3L)).thenReturn(restSuccess(rtoOrganisationTypeResource));
    }

    public void setupCompetition() {
        competitionResource = newCompetitionResource()
                .with(id(competitionId))
                .with(name("Competition x"))
                .withStartDate(ZonedDateTime.now().minusDays(2))
                .withEndDate(ZonedDateTime.now().plusDays(5))
                .withCompetitionStatus(CompetitionStatus.OPEN)
                .withMinProjectDuraction(1)
                .withMaxProjectDuraction(36)
                .withUseNewApplicantMenu(true)
                .build();

        QuestionResourceBuilder questionResourceBuilder = newQuestionResource().withCompetition(competitionResource
                .getId());

        SectionResourceBuilder sectionResourceBuilder = newSectionResource().withCompetition(competitionResource
                .getId());

        QuestionResource q01Resource = setupQuestionResource(1L, "Application details", questionResourceBuilder);

        SectionResource sectionResource1 = sectionResourceBuilder.
                with(id(1L)).
                with(name("Application details")).
                withQuestions(simpleMap(singletonList(q01Resource), QuestionResource::getId)).
                withPriority(1).
                withType(SectionType.GENERAL).
                build();

        QuestionResource q02Resource = setupQuestionResource(1L, "Research category", questionResourceBuilder);

        QuestionResource q10Resource = setupQuestionResource(10L, "How does your project align with the scope of this" +
                " competition?", questionResourceBuilder);

        SectionResource sectionResource2 = sectionResourceBuilder.
                with(id(2L)).
                with(name("Scope (Gateway question)")).
                withQuestions(simpleMap(singletonList(q10Resource), QuestionResource::getId)).
                withPriority(2).
                withType(SectionType.GENERAL).
                build();

        QuestionResource q20Resource = setupQuestionResource(20L, "1. What is the business opportunity that this " +
                "project addresses?", questionResourceBuilder);

        QuestionResource q21Resource = setupQuestionResource(21L, "2. What is the size of the market opportunity that" +
                " this project might open up?", questionResourceBuilder);

        QuestionResource q22Resource = setupQuestionResource(22L, "3. How will the results of the project be " +
                "exploited and disseminated?", questionResourceBuilder);

        QuestionResource q23Resource = setupQuestionResource(23L, "4. What economic, social and environmental " +
                "benefits is the project expected to deliver?", questionResourceBuilder);

        SectionResource sectionResource3 = sectionResourceBuilder.
                with(id(3L)).
                with(name("Business proposition (Q1 - Q4)")).
                withQuestions(simpleMap(asList(q20Resource, q21Resource, q22Resource, q23Resource),
                        QuestionResource::getId)).
                withPriority(3).
                withType(SectionType.GENERAL).
                build();


        QuestionResource q30Resource = setupQuestionResource(30L, "5. What technical approach will be adopted and how" +
                " will the project be managed?", questionResourceBuilder);

        QuestionResource q31Resource = setupFileQuestionResource(31L, "6. What is innovative about this project?",
                questionResourceBuilder);

        QuestionResource q32Resource = setupQuestionResource(32L, "7. What are the risks (technical, commercial and " +
                "environmental) to project success? What is the project's risk management strategy?",
                questionResourceBuilder);

        QuestionResource q33Resource = setupQuestionResource(33L, "8. Does the project team have the right skills and" +
                " experience and access to facilities to deliver the identified benefits?", questionResourceBuilder);

        SectionResource sectionResource4 = sectionResourceBuilder.
                with(id(4L)).
                with(name("Project approach (Q5 - Q8)")).
                withQuestions(simpleMap(asList(q30Resource, q31Resource, q32Resource, q33Resource),
                        QuestionResource::getId)).
                withPriority(4).
                withType(SectionType.GENERAL).
                build();

        SectionResource sectionResource5 = sectionResourceBuilder.with(id(5L)).with(name("Funding (Q9 - Q10)"))
                .withPriority(5).withType(SectionType.GENERAL).build();
        SectionResource sectionResource6 = sectionResourceBuilder.with(id(6L)).with(name("Finances")).withPriority(6).withType
                (SectionType.GENERAL).build();
        SectionResource sectionResource7 = sectionResourceBuilder.with(id(7L)).with(name("Your finances")).withPriority(7).withType
                (SectionType.FINANCE).build();
        SectionResource sectionResource8 = sectionResourceBuilder.with(id(8L)).with(name("Your project costs"))
                .withPriority(8).withType(SectionType.PROJECT_COST_FINANCES).withParentSection(sectionResource7.getId()).build();
        SectionResource sectionResource9 = sectionResourceBuilder.with(id(9L)).with(name("Your organisation"))
                .withPriority(9).withType(SectionType.ORGANISATION_FINANCES).withParentSection(sectionResource7.getId()).build();
        SectionResource sectionResource10 = sectionResourceBuilder.with(id(10L)).with(name("Your funding")).withPriority(10).withType
                (SectionType.FUNDING_FINANCES).withParentSection(sectionResource7.getId()).build();

        sectionResource6.setChildSections(Arrays.asList(sectionResource7.getId()));
        sectionResource7.setChildSections(Arrays.asList(sectionResource8.getId(), sectionResource9.getId(),
                sectionResource10.getId()));

        sectionResources = asList(sectionResource1, sectionResource2, sectionResource3, sectionResource4,
                sectionResource5, sectionResource6, sectionResource7, sectionResource8, sectionResource9,
                sectionResource10);
        sectionResources.forEach(s -> {
                    s.setQuestionGroup(false);
                    s.setChildSections(new ArrayList<>());
                    when(sectionService.getById(s.getId())).thenReturn(s);
                }
        );
        when(sectionService.getSectionsForCompetitionByType(1L, SectionType.FINANCE)).thenReturn(Arrays.asList
                (sectionResource7));
        when(sectionService.getFinanceSection(1L)).thenReturn(sectionResource7);
        when(sectionService.getSectionsForCompetitionByType(1L, SectionType.ORGANISATION_FINANCES)).thenReturn(Arrays
                .asList(sectionResource9));
        when(sectionService.getSectionsForCompetitionByType(1L, SectionType.FUNDING_FINANCES)).thenReturn(Arrays
                .asList(sectionResource10));

        when(questionService.getQuestionsBySectionIdAndType(7L, QuestionType.COST)).thenReturn(Arrays.asList
                (q21Resource, q22Resource, q23Resource));
        when(questionService.getQuestionByCompetitionIdAndFormInputType(1L, FormInputType.APPLICATION_DETAILS))
                .thenReturn(ServiceResult.serviceSuccess(q01Resource));
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(1L, RESEARCH_CATEGORY)).thenReturn
                (restSuccess(q02Resource));

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
                        .thenReturn(new HashSet<>(questionList.stream().map(QuestionResource::getId).collect
                                (Collectors.toList())));
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
        when(competitionRestService.getCompetitionById(competitionResource.getId())).thenReturn(restSuccess
                (competitionResource));
        when(competitionRestService.getAll()).thenReturn(restSuccess(competitionResources));
        when(competitionService.getById(any(Long.class))).thenReturn(competitionResource);

        when(formInputRestService.getByCompetitionIdAndScope(competitionResource.getId(), APPLICATION)).thenReturn
                (restSuccess(new ArrayList<>()));
    }

    public void setupApplicationWithRoles() {
        setupOrganisationTypes();
        // Build the backing applications.

        applications = asList(
                newApplicationResource().with(id(1L)).with(name("Rovel Additive Manufacturing Process"))
                        .withStartDate(LocalDate.now().plusMonths(3))
                        .withApplicationState(ApplicationState.CREATED).withResearchCategory
                        (newResearchCategoryResource().build()).withCompetition(competitionId).build(),
                newApplicationResource().with(id(2L)).with(name("Providing sustainable childcare")).withStartDate
                        (LocalDate.now().plusMonths(4))
                        .withApplicationState(ApplicationState.SUBMITTED).withResearchCategory
                        (newResearchCategoryResource().build()).withCompetition(competitionId).build(),
                newApplicationResource().with(id(3L)).with(name("Mobile Phone Data for Logistics Analytics"))
                        .withStartDate(LocalDate.now().plusMonths(5))
                        .withApplicationState(ApplicationState.APPROVED).withResearchCategory
                        (newResearchCategoryResource().build()).withCompetition(competitionId).build(),
                newApplicationResource().with(id(4L)).with(name("Using natural gas to heat homes")).withStartDate
                        (LocalDate.now().plusMonths(6))
                        .withApplicationState(ApplicationState.REJECTED).withResearchCategory
                        (newResearchCategoryResource().build()).withCompetition(competitionId).build(),
                newApplicationResource().with(id(5L)).with(name("Rovel Additive Manufacturing Process Ltd"))
                        .withStartDate(LocalDate.now().plusMonths(3))
                        .withApplicationState(ApplicationState.CREATED).withResearchCategory
                        (newResearchCategoryResource().build()).withCompetition(competitionId).build()
        );

        Map<Long, ApplicationResource> idsToApplicationResources = applications.stream().collect(toMap(a -> a.getId()
                , a -> a));

        Role role1 = Role.LEADAPPLICANT;
        Role role2 = Role.COLLABORATOR;
        Role assessorRole = Role.ASSESSOR;

        OrganisationResource organisation1 = newOrganisationResource().withId(1L).withOrganisationType
                (businessOrganisationTypeResource.getId()).withName("Empire Ltd").build();
        OrganisationResource organisation2 = newOrganisationResource().withId(2L).withOrganisationType
                (researchOrganisationTypeResource.getId()).withName("Ludlow").build();
        OrganisationResource organisation3 = newOrganisationResource().withId(3L).withOrganisationType
                (rtoOrganisationTypeResource.getId()).withName("Ludlow Ltd").build();

        organisations = asList(organisation1, organisation2, organisation3);
        Comparator<OrganisationResource> compareById = Comparator.comparingLong(OrganisationResource::getId);
        organisationSet = new TreeSet<>(compareById);
        organisationSet.addAll(organisations);

        ProcessRoleResource processRole1 = newProcessRoleResource().with(id(1L)).withApplication(applications.get(0)
                .getId()).withUser(applicant).withRole(role1).withOrganisation(organisation1.getId()).build();
        ProcessRoleResource processRole2 = newProcessRoleResource().with(id(2L)).withApplication(applications.get(0)
                .getId()).withUser(applicant).withRole(role1).withOrganisation(organisation1.getId()).build();
        ProcessRoleResource processRole3 = newProcessRoleResource().with(id(3L)).withApplication(applications.get(2)
                .getId()).withUser(applicant).withRole(role1).withOrganisation(organisation1.getId()).build();
        ProcessRoleResource processRole4 = newProcessRoleResource().with(id(4L)).withApplication(applications.get(3)
                .getId()).withUser(applicant).withRole(role1).withOrganisation(organisation1.getId()).build();
        ProcessRoleResource processRole5 = newProcessRoleResource().with(id(5L)).withApplication(applications.get(0)
                .getId()).withUser(applicant).withRole(role2).withOrganisation(organisation2.getId()).build();
        ProcessRoleResource processRole6 = newProcessRoleResource().with(id(6L)).withApplication(applications.get(1)
                .getId()).withUser(assessor).withRole(assessorRole).withOrganisation(organisation1.getId()).build();
        ProcessRoleResource processRole7 = newProcessRoleResource().with(id(7L)).withApplication(applications.get(2)
                .getId()).withUser(assessor).withRole(assessorRole).withOrganisation(organisation1.getId()).build();
        ProcessRoleResource processRole8 = newProcessRoleResource().with(id(8L)).withApplication(applications.get(0)
                .getId()).withUser(assessor).withRole(assessorRole).withOrganisation(organisation1.getId()).build();
        ProcessRoleResource processRole9 = newProcessRoleResource().with(id(9L)).withApplication(applications.get(3)
                .getId()).withUser(assessor).withRole(assessorRole).withOrganisation(organisation1.getId()).build();
        ProcessRoleResource processRole10 = newProcessRoleResource().with(id(10L)).withApplication(applications.get
                (1).getId()).withUser(applicant).withRole(role1).withOrganisation(organisation2.getId()).build();
        ProcessRoleResource processRole11 = newProcessRoleResource().with(id(11L)).withApplication(applications.get
                (4).getId()).withUser(applicant).withRole(role1).withOrganisation(organisation3.getId()).build();

        assessorProcessRoleResources = asList(processRole6, processRole7, processRole8, processRole9);
        processRoles = asList(processRole1, processRole2, processRole3, processRole4, processRole5, processRole6,
                processRole7, processRole8, processRole9);
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

        when(organisationRestService.getOrganisationsByApplicationId(applications.get(0).getId())).thenReturn
                (restSuccess(application1Organisations));
        when(organisationRestService.getOrganisationsByApplicationId(applications.get(1).getId())).thenReturn
                (restSuccess(application2Organisations));
        when(organisationRestService.getOrganisationsByApplicationId(applications.get(2).getId())).thenReturn
                (restSuccess(application3Organisations));
        when(organisationRestService.getOrganisationsByApplicationId(applications.get(3).getId())).thenReturn
                (restSuccess(application4Organisations));
        when(organisationRestService.getOrganisationsByApplicationId(applications.get(4).getId())).thenReturn
                (restSuccess(application5Organisations));

        organisation1.setProcessRoles(simpleMap(asList(processRole1, processRole2, processRole3, processRole4,
                processRole7, processRole8, processRole8), ProcessRoleResource::getId));
        organisation2.setProcessRoles(simpleMap(singletonList(processRole5), ProcessRoleResource::getId));
        organisation3.setProcessRoles(simpleMap(singletonList(processRole11), ProcessRoleResource::getId));

        when(sectionService.filterParentSections(sectionResources)).thenReturn(sectionResources);
        when(sectionService.getCompleted(applications.get(0).getId(), organisation1.getId())).thenReturn(asList(1L,
                2L));
        when(sectionService.getInCompleted(applications.get(0).getId())).thenReturn(asList(3L, 4L));
        when(processRoleService.findProcessRole(applicant.getId(), applications.get(0).getId())).thenReturn
                (processRole1);
        when(processRoleService.findProcessRole(applicant.getId(), applications.get(1).getId())).thenReturn
                (processRole2);
        when(processRoleService.findProcessRole(applicant.getId(), applications.get(2).getId())).thenReturn
                (processRole3);
        when(processRoleService.findProcessRole(applicant.getId(), applications.get(3).getId())).thenReturn
                (processRole4);
        when(processRoleService.findProcessRole(applicant.getId(), applications.get(0).getId())).thenReturn
                (processRole5);
        when(processRoleService.findProcessRole(assessor.getId(), applications.get(1).getId())).thenReturn
                (processRole6);
        when(processRoleService.findProcessRole(assessor.getId(), applications.get(2).getId())).thenReturn
                (processRole7);
        when(processRoleService.findProcessRole(assessor.getId(), applications.get(0).getId())).thenReturn
                (processRole8);
        when(processRoleService.findProcessRole(assessor.getId(), applications.get(3).getId())).thenReturn
                (processRole9);
        when(processRoleService.findProcessRole(applicant.getId(), applications.get(4).getId())).thenReturn
                (processRole11);

        when(processRoleService.findProcessRolesByApplicationId(applications.get(0).getId())).thenReturn
                (application1ProcessRoles);
        when(processRoleService.findProcessRolesByApplicationId(applications.get(1).getId())).thenReturn
                (application2ProcessRoles);
        when(processRoleService.findProcessRolesByApplicationId(applications.get(2).getId())).thenReturn
                (application3ProcessRoles);
        when(processRoleService.findProcessRolesByApplicationId(applications.get(3).getId())).thenReturn
                (application4ProcessRoles);
        when(processRoleService.findProcessRolesByApplicationId(applications.get(4).getId())).thenReturn
                (application5ProcessRoles);

        Map<Long, Set<Long>> completedMap = new HashMap<>();
        completedMap.put(organisation1.getId(), new TreeSet<>());
        completedMap.put(organisation2.getId(), new TreeSet<>());
        when(sectionService.getCompletedSectionsByOrganisation(applications.get(0).getId())).thenReturn(completedMap);
        when(sectionService.getCompletedSectionsByOrganisation(applications.get(1).getId())).thenReturn(completedMap);
        when(sectionService.getCompletedSectionsByOrganisation(applications.get(2).getId())).thenReturn(completedMap);

        when(applicationRestService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(restSuccess
                (applications));
        when(applicationService.getById(applications.get(0).getId())).thenReturn(applications.get(0));
        when(applicationService.getById(applications.get(1).getId())).thenReturn(applications.get(1));
        when(applicationService.getById(applications.get(2).getId())).thenReturn(applications.get(2));
        when(applicationService.getById(applications.get(3).getId())).thenReturn(applications.get(3));
        when(applicationService.getById(applications.get(4).getId())).thenReturn(applications.get(4));

        when(organisationService.getOrganisationById(organisationSet.first().getId())).thenReturn(organisationSet
                .first());
        when(organisationService.getOrganisationByIdForAnonymousUserFlow(organisationSet.first().getId())).thenReturn
                (organisationSet.first());
        when(organisationService.getOrganisationType(loggedInUser.getId(), applications.get(0).getId())).thenReturn
                (OrganisationTypeEnum.BUSINESS.getId());
        when(organisationService.getOrganisationForUser(loggedInUser.getId(), application1ProcessRoles)).thenReturn
                (Optional.of(organisationSet.first()));
        when(userService.isLeadApplicant(loggedInUser.getId(), applications.get(0))).thenReturn(true);
        when(userService.getLeadApplicantProcessRoleOrNull(applications.get(0).getId())).thenReturn(processRole1);
        when(userService.getLeadApplicantProcessRoleOrNull(applications.get(1).getId())).thenReturn(processRole2);
        when(userService.getLeadApplicantProcessRoleOrNull(applications.get(2).getId())).thenReturn(processRole3);
        when(userService.getLeadApplicantProcessRoleOrNull(applications.get(3).getId())).thenReturn(processRole4);
        when(userService.getLeadApplicantProcessRoleOrNull(applications.get(4).getId())).thenReturn(processRole11);

        when(userService.findById(loggedInUser.getId())).thenReturn(loggedInUser);

        processRoles.forEach(processRole -> when(processRoleService.getById(processRole.getId())).thenReturn(settable
                (processRole)));

        when(sectionService.getById(1L)).thenReturn(sectionResources.get(0));
        when(sectionService.getById(3L)).thenReturn(sectionResources.get(2));

        organisations.forEach(organisation -> when(organisationRestService.getOrganisationById(organisation.getId()))
                .thenReturn(restSuccess(organisation)));
        organisations.forEach(organisation -> when(organisationRestService.getOrganisationByIdForAnonymousUserFlow
                (organisation.getId())).thenReturn(restSuccess(organisation)));
    }

    public void setupApplicationResponses() {
        ApplicationResource application = applications.get(0);

        when(formInputRestService.getById(anyLong())).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            return restSuccess(newFormInputResource().with(id((Long) args[0])).build());
        });

        List<Long> formInputIds = questionResources.get(1L).getFormInputs();
        List<FormInputResponseResource> formInputResponses = newFormInputResponseResource().withFormInputs
                (formInputIds).
                with(idBasedValues("Value "))
                .build(formInputIds.size());

        when(formInputResponseRestService.getResponsesByApplicationId(application.getId())).thenReturn(restSuccess
                (formInputResponses));
        formInputsToFormInputResponses = formInputResponses.stream().collect(toMap(formInputResponseResource ->
                formInputResponseResource.getFormInput(), identity()));
        when(formInputResponseService.mapFormInputResponsesToFormInput(formInputResponses)).thenReturn
                (formInputsToFormInputResponses);
    }

    public void setupFinances() {
        ApplicationResource application = applications.get(0);
        applicationFinanceResource = new ApplicationFinanceResource(1L, application.getId(), organisations.get(0)
                .getId(), SMALL, "ABC 123");
        Map<FinanceRowType, FinanceRowCostCategory> organisationFinances = new HashMap<>();
        FinanceRowCostCategory costCategory = new GrantClaimCategory();
        costCategory.addCost(new GrantClaim(1L, 50));
        organisationFinances.put(FinanceRowType.FINANCE, costCategory);
        applicationFinanceResource.setFinanceOrganisationDetails(organisationFinances);
        when(financeService.getApplicationFinanceDetails(loggedInUser.getId(), application.getId())).thenReturn
                (applicationFinanceResource);
        when(financeService.getApplicationFinance(loggedInUser.getId(), application.getId())).thenReturn
                (applicationFinanceResource);
        when(applicationFinanceRestService.getResearchParticipationPercentage(anyLong())).thenReturn(restSuccess(0.0));
        when(financeViewHandlerProvider.getFinanceFormHandler(1L)).thenReturn(defaultFinanceFormHandler);
        when(financeViewHandlerProvider.getFinanceModelManager(1L)).thenReturn(defaultFinanceModelManager);
    }

    public void setupQuestionStatus(ApplicationResource application) {
        List<QuestionStatusResource> questionStatusResources = QuestionStatusResourceBuilder.newQuestionStatusResource()
                .withApplication(application)
                .with(questionStatusResource -> {
                    questionStatusResource.setAssigneeUserId(1L);
                    questionStatusResource.setMarkedAsComplete(false);
                }).build(1);

        when(questionService.findQuestionStatusesByQuestionAndApplicationId(1l, application.getId())).thenReturn
                (questionStatusResources);
    }


    private QuestionResource setupQuestionResource(Long id, String name, QuestionResourceBuilder
            questionResourceBuilder) {
        List<FormInputResource> formInputs = newFormInputResource().with(incrementingIds(1)).withType(TEXTAREA).build
                (1);
        QuestionResource questionResource = questionResourceBuilder.with(id(id)).with(name(name)).
                withFormInputs(simpleMap(formInputs, FormInputResource::getId)).
                build();
        when(questionService.getById(questionResource.getId())).thenReturn(questionResource);
        when(formInputRestService.getByQuestionIdAndScope(questionResource.getId(), APPLICATION)).thenReturn
                (restSuccess(formInputs));
        return questionResource;
    }

    private QuestionResource setupFileQuestionResource(Long id, String name, QuestionResourceBuilder
            questionResourceBuilder) {
        List<FormInputResource> formInputs = newFormInputResource()
                .with(incrementingIds(1))
                .withType(TEXTAREA, FILEUPLOAD)
                .build(2);
        QuestionResource questionResource = questionResourceBuilder.with(id(id)).with(name(name)).
                withFormInputs(simpleMap(formInputs, FormInputResource::getId)).
                build();
        when(questionService.getById(questionResource.getId())).thenReturn(questionResource);
        when(formInputRestService.getByQuestionIdAndScope(questionResource.getId(), APPLICATION)).thenReturn
                (restSuccess(formInputs));
        return questionResource;
    }
}