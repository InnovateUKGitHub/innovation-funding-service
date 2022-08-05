package org.innovateuk.ifs.application.overview.populator;

import org.innovateuk.ifs.application.ApplicationUrlHelper;
import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewRowViewModel;
import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewSectionViewModel;
import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewViewModel;
import org.innovateuk.ifs.application.resource.ApplicationExpressionOfInterestConfigResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionThirdPartyConfigRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.primitives.Longs.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Comparator.comparing;
import static org.innovateuk.ifs.AsyncTestExpectationHelper.setupAsyncExpectations;
import static org.innovateuk.ifs.application.builder.ApplicationExpressionOfInterestConfigResourceBuilder.newApplicationExpressionOfInterestConfigResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionThirdPartyConfigResourceBuilder.newCompetitionThirdPartyConfigResource;
import static org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsResourceBuilder.newGrantTermsAndConditionsResource;
import static org.innovateuk.ifs.competition.resource.CollaborationLevel.SINGLE;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.ASSESSED_QUESTION;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ApplicationOverviewModelPopulatorTest {

    @InjectMocks
    private ApplicationOverviewModelPopulator populator;

    @Mock
    private CompetitionRestService competitionRestService;
    @Mock
    private SectionRestService sectionRestService;
    @Mock
    private QuestionRestService questionRestService;
    @Mock
    private ProcessRoleRestService processRoleRestService;
    @Mock
    private MessageSource messageSource;
    @Mock
    private OrganisationRestService organisationRestService;
    @Mock
    private QuestionStatusRestService questionStatusRestService;
    @Mock
    private SectionStatusRestService sectionStatusRestService;
    @Mock
    private QuestionService questionService;
    @Mock
    private AsyncFuturesGenerator asyncFuturesGenerator;
    @Mock
    private ApplicationUrlHelper applicationUrlHelper;
    @Mock
    private GrantTermsAndConditionsResource grantTermsAndConditionsResource;
    @Mock
    private CompetitionThirdPartyConfigRestService competitionThirdPartyConfigRestService;

    @Before
    public void setupExpectations() {
        setupAsyncExpectations(asyncFuturesGenerator);
    }

    @Test
    public void populateModelWithoutPreRegistration() {
        GrantTermsAndConditionsResource termsAndCondition = newGrantTermsAndConditionsResource().withName("Innovate UK").build();
        CompetitionResource competition = newCompetitionResource()
                .withCollaborationLevel(SINGLE)
                .withTermsAndConditions(termsAndCondition)
                .build();
        ApplicationResource application = newApplicationResource()
                .withCompetition(competition.getId())
                .build();
        List<QuestionResource> questions = newQuestionResource()
                .withShortName("A question")
                .withQuestionSetupType(ASSESSED_QUESTION)
                .withQuestionNumber("4")
                .build(1);
        UserResource user = newUserResource().build();
        OrganisationResource organisation = newOrganisationResource().build();
        List<ProcessRoleResource> processRoles = newProcessRoleResource()
                .withUser(user, newUserResource().build())
                .withRole(ProcessRoleType.LEADAPPLICANT, ProcessRoleType.COLLABORATOR)
                .withOrganisation(organisation.getId(), 99L)
                .build(2);
        List<QuestionStatusResource> questionStatuses = newQuestionStatusResource()
                .withQuestion(questions.get(0).getId())
                .withMarkedAsComplete(false)
                .withAssignee(processRoles.get(1).getId())
                .build(1);

        SectionResource childSection = newSectionResource()
                .withName("Child finance")
                .withPriority(3)
                .build();
        List<SectionResource> sections = newSectionResource()
                .withPriority(1, 2, 3, 4)
                .withName("Section with questions", "Finances", "Project details", "Terms and conditions")
                .withType(SectionType.GENERAL, SectionType.FINANCES, SectionType.PROJECT_DETAILS, SectionType.TERMS_AND_CONDITIONS)
                .withChildSections(emptyList(), Collections.singletonList(childSection.getId()), emptyList(), emptyList())
                .withQuestions(questions.stream().map(QuestionResource::getId).collect(Collectors.toList()), emptyList(), emptyList(), emptyList())
                .build(4);
        sections.add(childSection);
        childSection.setParentSection(sections.get(1).getId());
        
        Map<Long, Set<Long>> completedSectionsByOrganisation = emptyMap();

        when(organisationRestService.getByUserAndApplicationId(user.getId(), application.getId())).thenReturn(restSuccess(organisation));
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(sectionRestService.getByCompetition(application.getCompetition())).thenReturn(restSuccess(sections));
        when(questionRestService.findByCompetition(application.getCompetition())).thenReturn(restSuccess(questions));
        when(processRoleRestService.findProcessRole(application.getId())).thenReturn(restSuccess(processRoles));
        when(questionStatusRestService.findByApplicationAndOrganisation(application.getId(), organisation.getId())).thenReturn(restSuccess(questionStatuses));
        when(sectionStatusRestService.getCompletedSectionIds(application.getId(), organisation.getId())).thenReturn(restSuccess(asList(sections.get(1).getId(), childSection.getId())));
        when(questionService.getNotificationsForUser(questionStatuses, user.getId())).thenReturn(questionStatuses);
        when(messageSource.getMessage("ifs.section.finances.description", null, Locale.getDefault())).thenReturn("Finance description");
        when(messageSource.getMessage("ifs.section.projectDetails.description", null, Locale.getDefault())).thenReturn("Project details description");
        when(messageSource.getMessage("ifs.section.termsAndConditions.description", null, Locale.getDefault())).thenReturn("T&Cs description");
        when(applicationUrlHelper.getQuestionUrl(any(), anyLong(), anyLong(), anyLong())).thenReturn(Optional.of("/the-question-url"));
        when(sectionStatusRestService.getCompletedSectionsByOrganisation(application.getId())).thenReturn(restSuccess(completedSectionsByOrganisation));
        when(grantTermsAndConditionsResource.isProcurementThirdParty()).thenReturn(false);

        ApplicationOverviewViewModel viewModel = populator.populateModel(application, user);

        assertEquals(application, viewModel.getApplication());
        assertEquals(competition, viewModel.getCompetition());
        assertEquals(processRoles.get(0), viewModel.getProcessRole());
        assertTrue(viewModel.isLead());

        assertEquals(4, viewModel.getSections().size());

        Iterator<ApplicationOverviewSectionViewModel> sectionIterator = viewModel.getSections()
                .stream()
                .sorted(comparing(ApplicationOverviewSectionViewModel::getId))
                .iterator();

        ApplicationOverviewSectionViewModel sectionWithQuestions = sectionIterator.next();
        assertEquals("Section with questions", sectionWithQuestions.getTitle());
        assertNull(sectionWithQuestions.getSubTitle());
        assertEquals((long) sections.get(0).getId(), sectionWithQuestions.getId());
        assertEquals(1, sectionWithQuestions.getRows().size());

        ApplicationOverviewRowViewModel questionRow = sectionWithQuestions.getRows().iterator().next();
        assertEquals("4. A question", questionRow.getTitle());
        assertEquals("/the-question-url", questionRow.getUrl());
        assertEquals(false, questionRow.isComplete());
        assertEquals(processRoles.get(1), questionRow.getAssignButtonsViewModel().get().getAssignee());
        assertEquals(processRoles, questionRow.getAssignButtonsViewModel().get().getAssignableApplicants());

        ApplicationOverviewSectionViewModel sectionWithChildSections = sectionIterator.next();
        assertEquals("Finances", sectionWithChildSections.getTitle());
        assertEquals("Finance description", sectionWithChildSections.getSubTitle());
        assertEquals((long) sections.get(1).getId(), sectionWithChildSections.getId());

        ApplicationOverviewRowViewModel childSectionRow = sectionWithChildSections.getRows().iterator().next();
        assertEquals("Child finance", childSectionRow.getTitle());
        assertEquals(String.format("/application/%d/form/section/%d", application.getId(), childSection.getId()), childSectionRow.getUrl());
        assertEquals(true, childSectionRow.isComplete());
        assertFalse(childSectionRow.getAssignButtonsViewModel().isPresent());

        ApplicationOverviewSectionViewModel projectDetailsSection = sectionIterator.next();
        assertEquals("Project details", projectDetailsSection.getTitle());
        assertEquals("Project details description", projectDetailsSection.getSubTitle());
        assertEquals((long) sections.get(2).getId(), projectDetailsSection.getId());

        ApplicationOverviewSectionViewModel termsAndConditionsSection = sectionIterator.next();
        assertEquals("Terms and conditions", termsAndConditionsSection.getTitle());
        assertEquals("T&Cs description", termsAndConditionsSection.getSubTitle());
        assertEquals((long) sections.get(3).getId(), termsAndConditionsSection.getId());

        verify(questionService).removeNotifications(questionStatuses);
    }

    @Test
    public void populateModelWithPreRegistration() {
        GrantTermsAndConditionsResource termsAndCondition = newGrantTermsAndConditionsResource().withName("Innovate UK").build();
        CompetitionResource competition = newCompetitionResource()
                .withCollaborationLevel(SINGLE)
                .withTermsAndConditions(termsAndCondition)
                .build();
        ApplicationResource application = newApplicationResource()
                .withCompetition(competition.getId())
                .build();

        ApplicationExpressionOfInterestConfigResource applicationExpressionOfInterestConfigResource =
                 newApplicationExpressionOfInterestConfigResource()
                .withApplicationId(application.getId())
                .withEnabledForExpressionOfInterest(true).build();

        application.setApplicationExpressionOfInterestConfigResource(applicationExpressionOfInterestConfigResource);
        List<QuestionResource> questions = newQuestionResource()
                .withShortName("A question")
                .withQuestionSetupType(ASSESSED_QUESTION)
                .withQuestionNumber("4")
                .withEnabledForPreRegistration(true)
                .build(1);
        UserResource user = newUserResource().build();
        OrganisationResource organisation = newOrganisationResource().build();
        List<ProcessRoleResource> processRoles = newProcessRoleResource()
                .withUser(user, newUserResource().build())
                .withRole(ProcessRoleType.LEADAPPLICANT, ProcessRoleType.COLLABORATOR)
                .withOrganisation(organisation.getId(), 99L)
                .build(2);
        List<QuestionStatusResource> questionStatuses = newQuestionStatusResource()
                .withQuestion(questions.get(0).getId())
                .withMarkedAsComplete(false)
                .withAssignee(processRoles.get(1).getId())
                .build(1);

        SectionResource childSection = newSectionResource()
                .withName("Child finance")
                .withPriority(3)
                .withEnabledForPreRegistration(true)
                .build();

        List<SectionResource> sections = newSectionResource()
                .withPriority(1, 2, 3, 4)
                .withName("Section with questions", "Finances", "Project details", "Terms and conditions","Application questions")
                .withType(SectionType.GENERAL, SectionType.FINANCES, SectionType.PROJECT_DETAILS, SectionType.TERMS_AND_CONDITIONS,SectionType.APPLICATION_QUESTIONS)
                .withChildSections(emptyList(), Collections.singletonList(childSection.getId()), emptyList(), emptyList(), emptyList())
                .withEnabledForPreRegistration(true)
                .withQuestions(questions.stream().map(QuestionResource::getId).collect(Collectors.toList()), emptyList(), emptyList(), emptyList(), emptyList())

                .build(5);
        sections.add(childSection);
        childSection.setParentSection(sections.get(1).getId());

        Map<Long, Set<Long>> completedSectionsByOrganisation = emptyMap();

        when(organisationRestService.getByUserAndApplicationId(user.getId(), application.getId())).thenReturn(restSuccess(organisation));
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(sectionRestService.getByCompetition(application.getCompetition())).thenReturn(restSuccess(sections));
        when(questionRestService.findByCompetition(application.getCompetition())).thenReturn(restSuccess(questions));
        when(processRoleRestService.findProcessRole(application.getId())).thenReturn(restSuccess(processRoles));
        when(questionStatusRestService.findByApplicationAndOrganisation(application.getId(), organisation.getId())).thenReturn(restSuccess(questionStatuses));
        when(sectionStatusRestService.getCompletedSectionIds(application.getId(), organisation.getId())).thenReturn(restSuccess(asList(sections.get(1).getId(), childSection.getId())));
        when(questionService.getNotificationsForUser(questionStatuses, user.getId())).thenReturn(questionStatuses);
        when(messageSource.getMessage("ifs.section.finances.description", null, Locale.getDefault())).thenReturn("Finance description");
        when(messageSource.getMessage("ifs.section.projectDetails.description", null, Locale.getDefault())).thenReturn("Project details description");
        when(messageSource.getMessage("ifs.section.termsAndConditions.description", null, Locale.getDefault())).thenReturn("T&Cs description");
        when(applicationUrlHelper.getQuestionUrl(any(), anyLong(), anyLong(), anyLong())).thenReturn(Optional.of("/the-question-url"));
        when(sectionStatusRestService.getCompletedSectionsByOrganisation(application.getId())).thenReturn(restSuccess(completedSectionsByOrganisation));
        when(grantTermsAndConditionsResource.isProcurementThirdParty()).thenReturn(false);

        ApplicationOverviewViewModel viewModel = populator.populateModel(application, user);

        assertEquals(application, viewModel.getApplication());
        assertEquals(competition, viewModel.getCompetition());
        assertEquals(processRoles.get(0), viewModel.getProcessRole());
        assertTrue(viewModel.isLead());

        assertEquals(5, viewModel.getSections().size());

        Iterator<ApplicationOverviewSectionViewModel> sectionIterator = viewModel.getSections()
                .stream()
                .sorted(comparing(ApplicationOverviewSectionViewModel::getId))
                .iterator();

        ApplicationOverviewSectionViewModel sectionWithQuestions = sectionIterator.next();
        assertEquals("Section with questions", sectionWithQuestions.getTitle());
        assertNull(sectionWithQuestions.getSubTitle());
        assertEquals((long) sections.get(0).getId(), sectionWithQuestions.getId());
        assertEquals(1, sectionWithQuestions.getRows().size());
        assertTrue(sections.get(0).isEnabledForPreRegistration());

        ApplicationOverviewRowViewModel questionRow = sectionWithQuestions.getRows().iterator().next();
        assertEquals("4. A question", questionRow.getTitle());
        assertEquals("/the-question-url", questionRow.getUrl());
        assertEquals(false, questionRow.isComplete());
        assertEquals(processRoles.get(1), questionRow.getAssignButtonsViewModel().get().getAssignee());
        assertEquals(processRoles, questionRow.getAssignButtonsViewModel().get().getAssignableApplicants());
        assertTrue(questionRow.isEnabledForPreRegistration());

        ApplicationOverviewSectionViewModel sectionWithChildSections = sectionIterator.next();
        assertEquals("Finances", sectionWithChildSections.getTitle());
        assertEquals("Finance description", sectionWithChildSections.getSubTitle());
        assertEquals((long) sections.get(1).getId(), sectionWithChildSections.getId());

        ApplicationOverviewRowViewModel childSectionRow = sectionWithChildSections.getRows().iterator().next();
        assertEquals("Child finance", childSectionRow.getTitle());
        assertEquals(String.format("/application/%d/form/section/%d", application.getId(), childSection.getId()), childSectionRow.getUrl());
        assertEquals(true, childSectionRow.isComplete());
        assertFalse(childSectionRow.getAssignButtonsViewModel().isPresent());
        assertTrue(childSectionRow.isEnabledForPreRegistration());

        ApplicationOverviewSectionViewModel projectDetailsSection = sectionIterator.next();
        assertEquals("Project details", projectDetailsSection.getTitle());
        assertEquals("Project details description", projectDetailsSection.getSubTitle());
        assertEquals((long) sections.get(2).getId(), projectDetailsSection.getId());
        assertTrue(sections.get(2).isEnabledForPreRegistration());

        ApplicationOverviewSectionViewModel termsAndConditionsSection = sectionIterator.next();
        assertEquals("Terms and conditions", termsAndConditionsSection.getTitle());
        assertEquals("T&Cs description", termsAndConditionsSection.getSubTitle());
        assertEquals((long) sections.get(3).getId(), termsAndConditionsSection.getId());

        ApplicationOverviewSectionViewModel appSection = sectionIterator.next();
        assertEquals("Expression of interest questions", appSection.getTitle());
        assertEquals((long) sections.get(4).getId(), appSection.getId());

        verify(questionService).removeNotifications(questionStatuses);
    }


    @Test
    public void populateModelWithAndWithoutPreRegistration() {
        GrantTermsAndConditionsResource termsAndCondition = newGrantTermsAndConditionsResource().withName("Innovate UK").build();
        CompetitionResource competition = newCompetitionResource()
                .withCollaborationLevel(SINGLE)
                .withTermsAndConditions(termsAndCondition)
                .build();

        ApplicationResource application = newApplicationResource()
                .withCompetition(competition.getId())
                .build();

        ApplicationExpressionOfInterestConfigResource applicationExpressionOfInterestConfigResource =
                newApplicationExpressionOfInterestConfigResource().
                        withApplicationId(application.getId())
                        .withEnabledForExpressionOfInterest(true).build();

         application.setApplicationExpressionOfInterestConfigResource(applicationExpressionOfInterestConfigResource);

        List<QuestionResource> questions = newQuestionResource()
                .withShortName("A question")
                .withQuestionSetupType(ASSESSED_QUESTION)
                .withQuestionNumber("4")
                .withEnabledForPreRegistration(true)
                .build(1);
        UserResource user = newUserResource().build();
        OrganisationResource organisation = newOrganisationResource().build();
        List<ProcessRoleResource> processRoles = newProcessRoleResource()
                .withUser(user, newUserResource().build())
                .withRole(ProcessRoleType.LEADAPPLICANT, ProcessRoleType.COLLABORATOR)
                .withOrganisation(organisation.getId(), 99L)
                .build(2);
        List<QuestionStatusResource> questionStatuses = newQuestionStatusResource()
                .withQuestion(questions.get(0).getId())
                .withMarkedAsComplete(false)
                .withAssignee(processRoles.get(1).getId())
                .build(1);

        SectionResource childSection = newSectionResource()
                .withName("Child finance")
                .withPriority(3)
                .withEnabledForPreRegistration(true)
                .build();

        List<SectionResource> sections = newSectionResource()
                .withPriority(1, 2, 3, 4)
                .withName("Section with questions", "Finances", "Project details", "Terms and conditions")
                .withType(SectionType.GENERAL, SectionType.FINANCES, SectionType.PROJECT_DETAILS, SectionType.TERMS_AND_CONDITIONS)
                .withChildSections(emptyList(), Collections.singletonList(childSection.getId()), emptyList(), emptyList())
                .withEnabledForPreRegistration(false,false,true,true)
                .withQuestions(questions.stream().map(QuestionResource::getId).collect(Collectors.toList()), emptyList(), emptyList(), emptyList())

                .build(4);
        sections.add(childSection);
        childSection.setParentSection(sections.get(1).getId());

        Map<Long, Set<Long>> completedSectionsByOrganisation = emptyMap();

        when(organisationRestService.getByUserAndApplicationId(user.getId(), application.getId())).thenReturn(restSuccess(organisation));
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(sectionRestService.getByCompetition(application.getCompetition())).thenReturn(restSuccess(sections));
        when(questionRestService.findByCompetition(application.getCompetition())).thenReturn(restSuccess(questions));
        when(processRoleRestService.findProcessRole(application.getId())).thenReturn(restSuccess(processRoles));
        when(questionStatusRestService.findByApplicationAndOrganisation(application.getId(), organisation.getId())).thenReturn(restSuccess(questionStatuses));
        when(sectionStatusRestService.getCompletedSectionIds(application.getId(), organisation.getId())).thenReturn(restSuccess(asList(sections.get(1).getId(), childSection.getId())));
        when(questionService.getNotificationsForUser(questionStatuses, user.getId())).thenReturn(questionStatuses);
        when(messageSource.getMessage("ifs.section.finances.description", null, Locale.getDefault())).thenReturn("Finance description");
        when(messageSource.getMessage("ifs.section.projectDetails.description", null, Locale.getDefault())).thenReturn("Project details description");
        when(messageSource.getMessage("ifs.section.termsAndConditions.description", null, Locale.getDefault())).thenReturn("T&Cs description");
        when(applicationUrlHelper.getQuestionUrl(any(), anyLong(), anyLong(), anyLong())).thenReturn(Optional.of("/the-question-url"));
        when(sectionStatusRestService.getCompletedSectionsByOrganisation(application.getId())).thenReturn(restSuccess(completedSectionsByOrganisation));
        when(grantTermsAndConditionsResource.isProcurementThirdParty()).thenReturn(false);

        ApplicationOverviewViewModel viewModel = populator.populateModel(application, user);

        assertEquals(application, viewModel.getApplication());
        assertEquals(competition, viewModel.getCompetition());
        assertEquals(processRoles.get(0), viewModel.getProcessRole());
        assertTrue(viewModel.isLead());

        assertEquals(2, viewModel.getSections().size());


    }

    @Test
    public void thirdPartyApplication() {
        GrantTermsAndConditionsResource termsAndCondition = newGrantTermsAndConditionsResource()
                .withName("Third Party")
                .build();
        CompetitionThirdPartyConfigResource thirdPartyConfigResource = newCompetitionThirdPartyConfigResource()
                .withTermsAndConditionsLabel("Test label")
                .withTermsAndConditionsGuidance("Test guidance")
                .withProjectCostGuidanceUrl("https://www.gov.uk/government/publications/innovate-uk-completing-your-application-project-costs-guidance")
                .build();
        CompetitionResource competition = newCompetitionResource()
                .withCollaborationLevel(SINGLE)
                .withTermsAndConditions(termsAndCondition)
                .withCompetitionThirdPartyConfig(thirdPartyConfigResource)
                .build();

        ApplicationResource application = newApplicationResource()
                .withCompetition(competition.getId())
                .build();
        List<QuestionResource> questions = newQuestionResource()
                .withShortName("A question")
                .withQuestionSetupType(ASSESSED_QUESTION)
                .withQuestionNumber("4")
                .withEnabledForPreRegistration(true)
                .build(1);
        UserResource user = newUserResource().build();
        OrganisationResource organisation = newOrganisationResource().build();
        List<ProcessRoleResource> processRoles = newProcessRoleResource()
                .withUser(user, newUserResource().build())
                .withRole(ProcessRoleType.LEADAPPLICANT, ProcessRoleType.COLLABORATOR)
                .withOrganisation(organisation.getId(), 99L)
                .build(2);
        List<QuestionStatusResource> questionStatuses = newQuestionStatusResource()
                .withQuestion(questions.get(0).getId())
                .withMarkedAsComplete(false)
                .withAssignee(processRoles.get(1).getId())
                .build(1);

        SectionResource childSection = newSectionResource()
                .withName("Child finance")
                .withPriority(3)
                .withEnabledForPreRegistration(true)
                .build();
        List<SectionResource> sections = newSectionResource()
                .withPriority(1, 2, 3, 4)
                .withName("Section with questions", "Finances", "Project details", "Terms and conditions")
                .withType(SectionType.GENERAL, SectionType.FINANCES, SectionType.PROJECT_DETAILS, SectionType.TERMS_AND_CONDITIONS)
                .withChildSections(emptyList(), Collections.singletonList(childSection.getId()), emptyList(), emptyList())
                .withQuestions(questions.stream().map(QuestionResource::getId).collect(Collectors.toList()), emptyList(), emptyList(), emptyList())
                .withEnabledForPreRegistration(true)
                .build(4);
        sections.add(childSection);
        childSection.setParentSection(sections.get(1).getId());

        Map<Long, Set<Long>> completedSectionsByOrganisation = emptyMap();

        when(organisationRestService.getByUserAndApplicationId(user.getId(), application.getId())).thenReturn(restSuccess(organisation));
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(sectionRestService.getByCompetition(application.getCompetition())).thenReturn(restSuccess(sections));
        when(questionRestService.findByCompetition(application.getCompetition())).thenReturn(restSuccess(questions));
        when(processRoleRestService.findProcessRole(application.getId())).thenReturn(restSuccess(processRoles));
        when(questionStatusRestService.findByApplicationAndOrganisation(application.getId(), organisation.getId())).thenReturn(restSuccess(questionStatuses));
        when(sectionStatusRestService.getCompletedSectionIds(application.getId(), organisation.getId())).thenReturn(restSuccess(asList(sections.get(1).getId(), childSection.getId())));
        when(questionService.getNotificationsForUser(questionStatuses, user.getId())).thenReturn(questionStatuses);
        when(messageSource.getMessage("ifs.section.finances.description", null, Locale.getDefault())).thenReturn("Finance description");
        when(messageSource.getMessage("ifs.section.projectDetails.description", null, Locale.getDefault())).thenReturn("Project details description");
        when(messageSource.getMessage("ifs.section.termsAndConditionsProcurementThirdParty.description", null, Locale.getDefault())).thenReturn("You must agree to it before you submit your application");
        when(applicationUrlHelper.getQuestionUrl(any(), anyLong(), anyLong(), anyLong())).thenReturn(Optional.of("/the-question-url"));
        when(sectionStatusRestService.getCompletedSectionsByOrganisation(application.getId())).thenReturn(restSuccess(completedSectionsByOrganisation));
        when(competitionThirdPartyConfigRestService.findOneByCompetitionId(competition.getId())).thenReturn(restSuccess(thirdPartyConfigResource));
        when(grantTermsAndConditionsResource.isProcurementThirdParty()).thenReturn(false);

        ApplicationOverviewViewModel viewModel = populator.populateModel(application, user);

        assertEquals(application, viewModel.getApplication());
        assertEquals(competition, viewModel.getCompetition());
        assertEquals(processRoles.get(0), viewModel.getProcessRole());
        assertTrue(viewModel.isLead());

        assertEquals(4, viewModel.getSections().size());

        Iterator<ApplicationOverviewSectionViewModel> sectionIterator = viewModel.getSections()
                .stream()
                .sorted(comparing(ApplicationOverviewSectionViewModel::getId))
                .iterator();

        ApplicationOverviewSectionViewModel sectionWithQuestions = sectionIterator.next();
        assertEquals("Section with questions", sectionWithQuestions.getTitle());
        assertNull(sectionWithQuestions.getSubTitle());
        assertEquals((long) sections.get(0).getId(), sectionWithQuestions.getId());
        assertEquals(1, sectionWithQuestions.getRows().size());

        ApplicationOverviewRowViewModel questionRow = sectionWithQuestions.getRows().iterator().next();
        assertEquals("4. A question", questionRow.getTitle());
        assertEquals("/the-question-url", questionRow.getUrl());
        assertEquals(false, questionRow.isComplete());
        assertEquals(processRoles.get(1), questionRow.getAssignButtonsViewModel().get().getAssignee());
        assertEquals(processRoles, questionRow.getAssignButtonsViewModel().get().getAssignableApplicants());

        ApplicationOverviewSectionViewModel sectionWithChildSections = sectionIterator.next();
        assertEquals("Finances", sectionWithChildSections.getTitle());
        assertEquals("Finance description", sectionWithChildSections.getSubTitle());
        assertEquals((long) sections.get(1).getId(), sectionWithChildSections.getId());

        ApplicationOverviewRowViewModel childSectionRow = sectionWithChildSections.getRows().iterator().next();
        assertEquals("Child finance", childSectionRow.getTitle());
        assertEquals(String.format("/application/%d/form/section/%d", application.getId(), childSection.getId()), childSectionRow.getUrl());
        assertEquals(true, childSectionRow.isComplete());
        assertFalse(childSectionRow.getAssignButtonsViewModel().isPresent());

        ApplicationOverviewSectionViewModel projectDetailsSection = sectionIterator.next();
        assertEquals("Project details", projectDetailsSection.getTitle());
        assertEquals("Project details description", projectDetailsSection.getSubTitle());
        assertEquals((long) sections.get(2).getId(), projectDetailsSection.getId());

        ApplicationOverviewSectionViewModel termsAndConditionsSection = sectionIterator.next();
        assertEquals("Test label", termsAndConditionsSection.getTitle());
        assertEquals("You must agree to it before you submit your application", termsAndConditionsSection.getSubTitle());
        assertEquals((long) sections.get(3).getId(), termsAndConditionsSection.getId());

        verify(questionService).removeNotifications(questionStatuses);
    }
}
