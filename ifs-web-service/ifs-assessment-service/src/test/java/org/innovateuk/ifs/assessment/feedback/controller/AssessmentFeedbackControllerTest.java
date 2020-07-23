package org.innovateuk.ifs.assessment.feedback.controller;

import org.innovateuk.ifs.AbstractInviteMockMVCTest;
import org.innovateuk.ifs.application.populator.OrganisationDetailsModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.assessment.common.service.AssessmentService;
import org.innovateuk.ifs.assessment.feedback.populator.AssessmentFeedbackApplicationDetailsModelPopulator;
import org.innovateuk.ifs.assessment.feedback.populator.AssessmentFeedbackModelPopulator;
import org.innovateuk.ifs.assessment.feedback.populator.AssessmentFeedbackNavigationModelPopulator;
import org.innovateuk.ifs.assessment.feedback.viewmodel.AssessmentFeedbackApplicationDetailsViewModel;
import org.innovateuk.ifs.assessment.feedback.viewmodel.AssessmentFeedbackNavigationViewModel;
import org.innovateuk.ifs.assessment.feedback.viewmodel.AssessmentFeedbackViewModel;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponsesResource;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.form.Form;
import org.innovateuk.ifs.form.resource.*;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.invite.InviteService;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.is;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.form.resource.FormInputScope.ASSESSMENT;
import static org.innovateuk.ifs.form.resource.FormInputType.*;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleToMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessmentFeedbackControllerTest extends AbstractInviteMockMVCTest<AssessmentFeedbackController> {

    @Mock
    private AssessmentService assessmentService;

    @Spy
    @InjectMocks
    private AssessmentFeedbackModelPopulator assessmentFeedbackModelPopulator;

    @Spy
    @InjectMocks
    private AssessmentFeedbackNavigationModelPopulator assessmentFeedbackNavigationModelPopulator;

    @Spy
    @InjectMocks
    private AssessmentFeedbackApplicationDetailsModelPopulator assessmentFeedbackApplicationDetailsModelPopulator;

    @Spy
    @InjectMocks
    private OrganisationDetailsModelPopulator organisationDetailsModelPopulator;

    @Mock
    private QuestionService questionService;

    @Mock
    private FormInputRestService formInputRestService;

    @Mock
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private FormInputResponseRestService formInputResponseRestService;

    @Mock
    private CategoryRestService categoryRestServiceMock;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private SectionService sectionService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private FormInputResponseService formInputResponseService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private InviteService inviteService;

    @Override
    protected AssessmentFeedbackController supplyControllerUnderTest() {
        return new AssessmentFeedbackController();
    }

    private ZonedDateTime twoHoursAgo;

    @Before
    public void setUp() {
        twoHoursAgo = ZonedDateTime.now().minusHours(2);
    }

    @Test
    public void getQuestion() throws Exception {
        long applicationId = 1L;

        CompetitionResource competitionResource = setupCompetitionResource();

        AssessmentResource assessmentResource = setupAssessment(competitionResource.getId(), applicationId);

        SectionResource sectionResource = setupSection(SectionType.GENERAL);

        QuestionResource questionResource = setupQuestion(assessmentResource.getId());

        QuestionResource previousQuestionResource = newQuestionResource()
                .withShortName("Previous question")
                .withSection(sectionResource.getId())
                .build();

        QuestionResource nextQuestionResource = newQuestionResource()
                .withShortName("Next question")
                .withSection(sectionResource.getId())
                .build();

        setupQuestionNavigation(questionResource.getId(), of(previousQuestionResource), of(nextQuestionResource));

        List<FormInputResource> applicationFormInputs = setupApplicationFormInputs(questionResource.getId(), TEXTAREA);
        setupApplicantResponses(applicationId, applicationFormInputs);

        List<FormInputResource> assessmentFormInputs = setupAssessmentFormInputs(questionResource.getId(), TEXTAREA, ASSESSOR_SCORE);
        List<AssessorFormInputResponseResource> assessorResponses = setupAssessorResponses(assessmentResource.getId(),
                questionResource.getId(), assessmentFormInputs);

        Form expectedForm = new Form();
        expectedForm.setFormInput(simpleToMap(assessorResponses, assessorFormInputResponseResource ->
                String.valueOf(assessorFormInputResponseResource.getFormInput()), AssessorFormInputResponseResource::getValue));

        AssessmentFeedbackNavigationViewModel expectedNavigation = new AssessmentFeedbackNavigationViewModel(assessmentResource.getId(),
                of(previousQuestionResource), of(nextQuestionResource));

        AssessmentFeedbackViewModel expectedViewModel = new AssessmentFeedbackViewModel(
                assessmentResource,
                competitionResource,
                questionResource,
                "Applicant response",
                assessmentFormInputs,
                true,
                false,
                emptyList(),
                null,
                null,
                null);

        mockMvc.perform(get("/{assessmentId}/question/{questionId}", assessmentResource.getId(), questionResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().attribute("navigation", expectedNavigation))
                .andExpect(view().name("assessment/application-question"));

        InOrder inOrder = inOrder(questionService, formInputRestService, assessorFormInputResponseRestService,
                assessmentService, competitionRestService, formInputResponseRestService, categoryRestServiceMock);
        inOrder.verify(questionService).getByIdAndAssessmentId(questionResource.getId(), assessmentResource.getId());
        inOrder.verify(formInputRestService).getByQuestionIdAndScope(questionResource.getId(), APPLICATION);
        applicationFormInputs.forEach(formInput -> inOrder.verify(formInputResponseRestService).getByFormInputIdAndApplication(formInput.getId(), applicationId));
        inOrder.verify(formInputRestService).getByQuestionIdAndScope(questionResource.getId(), ASSESSMENT);
        inOrder.verify(questionService).getPreviousQuestion(questionResource.getId());
        inOrder.verify(questionService).getNextQuestion(questionResource.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getQuestion_withAppendixAndTemplateDocument() throws Exception {
        Long applicationId = 1L;

        CompetitionResource competitionResource = setupCompetitionResource();

        AssessmentResource assessmentResource = setupAssessment(competitionResource.getId(), applicationId);

        SectionResource sectionResource = setupSection(SectionType.GENERAL);

        QuestionResource questionResource = setupQuestion(assessmentResource.getId());

        QuestionResource previousQuestionResource = newQuestionResource()
                .withShortName("Previous question")
                .withSection(sectionResource.getId())
                .build();

        QuestionResource nextQuestionResource = newQuestionResource()
                .withShortName("Next question")
                .withSection(sectionResource.getId())
                .build();

        setupQuestionNavigation(questionResource.getId(), of(previousQuestionResource), of(nextQuestionResource));

        List<FormInputResource> applicationFormInputs = setupApplicationFormInputs(questionResource.getId(), TEXTAREA, FILEUPLOAD, TEMPLATE_DOCUMENT);
        setupApplicantResponses(applicationId, applicationFormInputs);

        List<FormInputResource> assessmentFormInputs = setupAssessmentFormInputs(questionResource.getId(), TEXTAREA, ASSESSOR_SCORE);
        List<AssessorFormInputResponseResource> assessorResponses = setupAssessorResponses(assessmentResource.getId(),
                questionResource.getId(), assessmentFormInputs);

        Form expectedForm = new Form();
        expectedForm.setFormInput(simpleToMap(assessorResponses, assessorFormInputResponseResource ->
                String.valueOf(assessorFormInputResponseResource.getFormInput()), AssessorFormInputResponseResource::getValue));

        AssessmentFeedbackNavigationViewModel expectedNavigation = new AssessmentFeedbackNavigationViewModel(assessmentResource.getId(),
                of(previousQuestionResource), of(nextQuestionResource));

        List<FileDetailsViewModel> expectedFileDetailsViewModel = newArrayList(new FileDetailsViewModel(applicationFormInputs.get(1).getId(),
                1L,
                "Appendix1.pdf",
                1024L),
                new FileDetailsViewModel(applicationFormInputs.get(1).getId(),
                        2L,
                        "Appendix2.pdf",
                        1024L));
        FileDetailsViewModel expectedTemplateFileDetailsViewModel = new FileDetailsViewModel(applicationFormInputs.get(2).getId(),
                3L,
                "template.pdf",
                1024L);
        String templateTitle = "templateTitle";
        applicationFormInputs.stream()
                .filter(input -> input.getType().equals(TEMPLATE_DOCUMENT))
                .findAny()
                .ifPresent(input -> input.setDescription(templateTitle));

        AssessmentFeedbackViewModel expectedViewModel = new AssessmentFeedbackViewModel(assessmentResource, competitionResource,
                questionResource,
                "Applicant response",
                assessmentFormInputs,
                true,
                false,
                expectedFileDetailsViewModel,
                expectedTemplateFileDetailsViewModel,
                templateTitle,
                null);

        mockMvc.perform(get("/{assessmentId}/question/{questionId}", assessmentResource.getId(), questionResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().attribute("navigation", expectedNavigation))
                .andExpect(view().name("assessment/application-question"));

        InOrder inOrder = inOrder(questionService, formInputRestService, assessorFormInputResponseRestService,
                assessmentService, competitionRestService, formInputResponseRestService, categoryRestServiceMock);
        inOrder.verify(questionService).getByIdAndAssessmentId(questionResource.getId(), assessmentResource.getId());
        inOrder.verify(formInputRestService).getByQuestionIdAndScope(questionResource.getId(), APPLICATION);
        applicationFormInputs.forEach(formInput -> inOrder.verify(formInputResponseRestService).getByFormInputIdAndApplication(formInput.getId(), applicationId));
        inOrder.verify(formInputRestService).getByQuestionIdAndScope(questionResource.getId(), ASSESSMENT);
        inOrder.verify(questionService).getPreviousQuestion(questionResource.getId());
        inOrder.verify(questionService).getNextQuestion(questionResource.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void nextQuestionIsNotNavigable() throws Exception {
        Long applicationId = 1L;

        CompetitionResource competitionResource = setupCompetitionResource();

        AssessmentResource assessmentResource = setupAssessment(competitionResource.getId(), applicationId);

        SectionResource sectionResource = setupSection(SectionType.GENERAL);
        SectionResource financeSectionResource = setupSection(SectionType.FINANCE);

        QuestionResource questionResource = setupQuestion(assessmentResource.getId());

        QuestionResource previousQuestionResource = newQuestionResource()
                .withShortName("Previous question")
                .withSection(sectionResource.getId())
                .build();

        QuestionResource nextQuestionResource = newQuestionResource()
                .withShortName("Next question")
                .withSection(financeSectionResource.getId())
                .build();

        // The next question should be skipped since it's within the Finance section
        setupQuestionNavigation(questionResource.getId(), of(previousQuestionResource), of(nextQuestionResource));
        // There is also no navigation from the question following that
        when(questionService.getNextQuestion(nextQuestionResource.getId())).thenReturn(empty());

        List<FormInputResource> applicationFormInputs = setupApplicationFormInputs(questionResource.getId(), TEXTAREA);
        setupApplicantResponses(applicationId, applicationFormInputs);

        List<FormInputResource> assessmentFormInputs = setupAssessmentFormInputs(questionResource.getId(), TEXTAREA, ASSESSOR_SCORE);
        List<AssessorFormInputResponseResource> assessorResponses = setupAssessorResponses(assessmentResource.getId(),
                questionResource.getId(), assessmentFormInputs);

        Form expectedForm = new Form();
        expectedForm.setFormInput(simpleToMap(assessorResponses, assessorFormInputResponseResource ->
                String.valueOf(assessorFormInputResponseResource.getFormInput()), AssessorFormInputResponseResource::getValue));
        AssessmentFeedbackNavigationViewModel expectedNavigation = new AssessmentFeedbackNavigationViewModel(assessmentResource.getId(), of(previousQuestionResource), empty());

        mockMvc.perform(get("/{assessmentId}/question/{questionId}", assessmentResource.getId(), questionResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().attribute("navigation", expectedNavigation))
                .andExpect(view().name("assessment/application-question"))
                .andReturn();
    }

    @Test
    public void getQuestion_applicationDetailsQuestion() throws Exception {
        ApplicationResource applicationResource = newApplicationResource()
                .withName("Application name")
                .withStartDate(twoHoursAgo.toLocalDate())
                .withDurationInMonths(20L)
                .build();

        CompetitionResource competitionResource = setupCompetitionResource();

        AssessmentResource assessmentResource = setupAssessment(competitionResource.getId(), applicationResource.getId());

        SectionResource sectionResource = setupSection(SectionType.GENERAL);

        QuestionResource questionResource = newQuestionResource()
                .withShortName("Application details")
                .withQuestionSetupType(QuestionSetupType.APPLICATION_DETAILS)
                .build();

        QuestionResource nextQuestionResource = newQuestionResource()
                .withShortName("Next question")
                .withSection(sectionResource.getId())
                .build();

        when(questionService.getByIdAndAssessmentId(questionResource.getId(), assessmentResource.getId()))
                .thenReturn(questionResource);

        when(organisationRestService.getOrganisationsByApplicationId(applicationResource.getId())).thenReturn(restSuccess(emptyList()));
        when(userRestService.findProcessRole(applicationResource.getId())).thenReturn(restSuccess(
                newProcessRoleResource().withRoleName(Role.LEADAPPLICANT.getName()).build(3)));
        when(applicationService.getById(applicationResource.getId())).thenReturn(applicationResource);

        setupQuestionNavigation(questionResource.getId(), empty(), of(nextQuestionResource));

        AssessmentFeedbackNavigationViewModel expectedNavigation = new AssessmentFeedbackNavigationViewModel(assessmentResource.getId(),
                empty(), of(nextQuestionResource));

        setupInvites();

        Form expectedForm = new Form();

        AssessmentFeedbackApplicationDetailsViewModel expectedViewModel = new AssessmentFeedbackApplicationDetailsViewModel(
                applicationResource.getId(),
                "Application name",
                twoHoursAgo.toLocalDate(),
                20L,
                3,
                50,
                "Application details"
        );

        mockMvc.perform(get("/{assessmentId}/question/{questionId}", assessmentResource.getId(), questionResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().attribute("navigation", expectedNavigation))
                .andExpect(view().name("assessment/application-details"));

        InOrder inOrder = inOrder(questionService, formInputRestService, assessmentService, applicationService, sectionService);
        inOrder.verify(questionService).getByIdAndAssessmentId(questionResource.getId(), assessmentResource.getId());
        inOrder.verify(assessmentService).getById(assessmentResource.getId());
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(questionService).getPreviousQuestion(questionResource.getId());
        inOrder.verify(questionService).getNextQuestion(questionResource.getId());
        inOrder.verify(sectionService).getById(nextQuestionResource.getSection());
        inOrder.verifyNoMoreInteractions();

        verifyZeroInteractions(formInputResponseService, assessorFormInputResponseRestService);
    }

    @Test
    public void getQuestion_scopeQuestion() throws Exception {
        Long applicationId = 1L;

        CompetitionResource competitionResource = setupCompetitionResource();

        AssessmentResource assessmentResource = setupAssessment(competitionResource.getId(), applicationId);

        SectionResource sectionResource = setupSection(SectionType.GENERAL);

        QuestionResource questionResource = setupQuestion(assessmentResource.getId());

        QuestionResource previousQuestionResource = newQuestionResource()
                .withShortName("Previous question")
                .withSection(sectionResource.getId())
                .build();

        QuestionResource nextQuestionResource = newQuestionResource()
                .withShortName("Next question")
                .withSection(sectionResource.getId())
                .build();

        setupQuestionNavigation(questionResource.getId(), of(previousQuestionResource), of(nextQuestionResource));

        List<FormInputResource> applicationFormInputs = setupApplicationFormInputs(questionResource.getId(), TEXTAREA);
        setupApplicantResponses(applicationId, applicationFormInputs);

        List<FormInputResource> assessmentFormInputs = setupAssessmentFormInputs(questionResource.getId(),
                ASSESSOR_RESEARCH_CATEGORY, ASSESSOR_APPLICATION_IN_SCOPE, TEXTAREA);
        List<AssessorFormInputResponseResource> assessorResponses = setupAssessorResponses(assessmentResource.getId(),
                questionResource.getId(), assessmentFormInputs);

        Form expectedForm = new Form();
        expectedForm.setFormInput(simpleToMap(assessorResponses, assessorFormInputResponseResource ->
                String.valueOf(assessorFormInputResponseResource.getFormInput()), AssessorFormInputResponseResource::getValue));
        AssessmentFeedbackNavigationViewModel expectedNavigation = new AssessmentFeedbackNavigationViewModel(assessmentResource.getId(),
                of(previousQuestionResource), of(nextQuestionResource));
        List<ResearchCategoryResource> researchCategoryResources = setupResearchCategories();

        AssessmentFeedbackViewModel expectedViewModel = new AssessmentFeedbackViewModel(assessmentResource, competitionResource, questionResource,
                "Applicant response",
                assessmentFormInputs,
                false,
                true,
                emptyList(),
                null,
                null,
                researchCategoryResources);

        mockMvc.perform(get("/{assessmentId}/question/{questionId}", assessmentResource.getId(),
                questionResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().attribute("navigation", expectedNavigation))
                .andExpect(view().name("assessment/application-question"));

        InOrder inOrder = inOrder(questionService, formInputRestService, assessorFormInputResponseRestService, assessmentService,
                competitionRestService, formInputResponseRestService, categoryRestServiceMock);
        inOrder.verify(questionService).getByIdAndAssessmentId(questionResource.getId(), assessmentResource.getId());
        inOrder.verify(formInputRestService).getByQuestionIdAndScope(questionResource.getId(), APPLICATION);
        applicationFormInputs.forEach(formInput -> inOrder.verify(formInputResponseRestService).getByFormInputIdAndApplication(formInput.getId(), applicationId));
        inOrder.verify(formInputRestService).getByQuestionIdAndScope(questionResource.getId(), ASSESSMENT);
        inOrder.verify(categoryRestServiceMock).getResearchCategories();
        inOrder.verify(questionService).getPreviousQuestion(questionResource.getId());
        inOrder.verify(questionService).getNextQuestion(questionResource.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getQuestion_scopeQuestionWithoutResearchCategory() throws Exception {
        Long applicationId = 1L;

        CompetitionResource competitionResource = setupCompetitionResource();

        AssessmentResource assessmentResource = setupAssessment(competitionResource.getId(), applicationId);

        SectionResource sectionResource = setupSection(SectionType.GENERAL);

        QuestionResource questionResource = setupQuestion(assessmentResource.getId());

        QuestionResource previousQuestionResource = newQuestionResource()
                .withShortName("Previous question")
                .withSection(sectionResource.getId())
                .build();

        QuestionResource nextQuestionResource = newQuestionResource()
                .withShortName("Next question")
                .withSection(sectionResource.getId())
                .build();

        setupQuestionNavigation(questionResource.getId(), of(previousQuestionResource), of(nextQuestionResource));

        List<FormInputResource> applicationFormInputs = setupApplicationFormInputs(questionResource.getId(), TEXTAREA);
        setupApplicantResponses(applicationId, applicationFormInputs);

        List<FormInputResource> assessmentFormInputs = setupAssessmentFormInputs(questionResource.getId(),
                ASSESSOR_APPLICATION_IN_SCOPE, TEXTAREA);
        List<AssessorFormInputResponseResource> assessorResponses = setupAssessorResponses(assessmentResource.getId(),
                questionResource.getId(), assessmentFormInputs);

        Form expectedForm = new Form();
        expectedForm.setFormInput(simpleToMap(assessorResponses, assessorFormInputResponseResource ->
                String.valueOf(assessorFormInputResponseResource.getFormInput()), AssessorFormInputResponseResource::getValue));
        AssessmentFeedbackNavigationViewModel expectedNavigation = new AssessmentFeedbackNavigationViewModel(assessmentResource.getId(),
                of(previousQuestionResource), of(nextQuestionResource));
        // Expect no research categories to be populated
        List<ResearchCategoryResource> researchCategoryResources = null;

        AssessmentFeedbackViewModel expectedViewModel = new AssessmentFeedbackViewModel(assessmentResource, competitionResource, questionResource,
                "Applicant response",
                assessmentFormInputs,
                false,
                true,
                emptyList(),
                null,
                null,
                researchCategoryResources);

        mockMvc.perform(get("/{assessmentId}/question/{questionId}", assessmentResource.getId(),
                questionResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().attribute("navigation", expectedNavigation))
                .andExpect(view().name("assessment/application-question"));

        InOrder inOrder = inOrder(questionService, formInputRestService, assessorFormInputResponseRestService,
                assessmentService, competitionRestService, formInputResponseRestService, categoryRestServiceMock);
        inOrder.verify(questionService).getByIdAndAssessmentId(questionResource.getId(), assessmentResource.getId());
        inOrder.verify(formInputRestService).getByQuestionIdAndScope(questionResource.getId(), APPLICATION);
        applicationFormInputs.forEach(formInput -> inOrder.verify(formInputResponseRestService).getByFormInputIdAndApplication(formInput.getId(), applicationId));
        inOrder.verify(formInputRestService).getByQuestionIdAndScope(questionResource.getId(), ASSESSMENT);
        inOrder.verify(questionService).getPreviousQuestion(questionResource.getId());
        inOrder.verify(questionService).getNextQuestion(questionResource.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void updateFormInputResponse() throws Exception {
        Long assessmentId = 1L;
        String value = "Feedback";
        Long formInputId = 1L;
        when(assessorFormInputResponseRestService.updateFormInputResponse(assessmentId, formInputId, value)).thenReturn(
                restSuccess());

        mockMvc.perform(post("/{assessmentId}/formInput/{formInputId}", assessmentId, formInputId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("value", value))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success", is("true")))
                .andReturn();

        verify(assessorFormInputResponseRestService, only()).updateFormInputResponse(assessmentId, formInputId, value);
    }

    @Test
    public void updateFormInputResponse_exceedsCharacterSizeLimit() throws Exception {
        Long assessmentId = 1L;
        String value = "This is the feedback";
        Long formInputId = 2L;

        when(assessorFormInputResponseRestService.updateFormInputResponse(assessmentId, formInputId, value))
                .thenReturn(restFailure(fieldError("fieldName", "Feedback", "validation.field.too.many.characters", "", "5000", "0")));

        when(messageSource.getMessage("validation.field.too.many.characters", new Object[]{"", "5000", "0"}, Locale.UK))
                .thenReturn("This field cannot contain more than 5000 characters");

        mockMvc.perform(post("/{assessmentId}/formInput/{formInputId}", assessmentId, formInputId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("value", value))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success", is("false")));

        verify(assessorFormInputResponseRestService, only()).updateFormInputResponse(assessmentId, formInputId, value);
    }

    @Test
    public void updateFormInputResponse_exceedWordLimit() throws Exception {
        Long assessmentId = 1L;
        String value = "This is the feedback";
        Long formInputId = 2L;

        when(assessorFormInputResponseRestService.updateFormInputResponse(assessmentId, formInputId, value))
                .thenReturn(restFailure(fieldError("fieldName", "Feedback", "validation.field.max.word.count", "", 100)));

        when(messageSource.getMessage("validation.field.max.word.count", new Object[]{"", "100"}, Locale.UK))
                .thenReturn("Maximum word count exceeded. Please reduce your word count to 100.");

        mockMvc.perform(post("/{assessmentId}/formInput/{formInputId}", assessmentId, formInputId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("value", value))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success", is("false")));

        verify(assessorFormInputResponseRestService, only()).updateFormInputResponse(assessmentId, formInputId, value);
    }

    @Test
    public void updateFormInputResponse_scopeUnselected() throws Exception {
        Long assessmentId = 1L;
        String value = "none";
        Long formInputId = 2L;

        when(assessorFormInputResponseRestService.updateFormInputResponse(assessmentId, formInputId, value))
                .thenReturn(restFailure(fieldError("fieldName", "value", "validation.assessor.scope.invalidScope")));

        when(messageSource.getMessage("validation.assessor.scope.invalidScope", new Object[]{}, Locale.UK))
                .thenReturn("Please select the scope.");


        mockMvc.perform(post("/{assessmentId}/formInput/{formInputId}", assessmentId, formInputId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("value", value))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success", is("false")));

        verify(assessorFormInputResponseRestService, only()).updateFormInputResponse(assessmentId, formInputId, value);
    }

    @Test
    public void save() throws Exception {
        long assessmentId = 1L;
        long questionId = 2L;
        List<FormInputResource> formInputs = setupAssessmentFormInputs(questionId, ASSESSOR_SCORE, TEXTAREA);

        Long formInputIdScore = formInputs.get(0).getId();
        Long formInputIdFeedback = formInputs.get(1).getId();
        String formInputScoreField = format("formInput[%s]", formInputIdScore);
        String formInputFeedbackField = format("formInput[%s]", formInputIdFeedback);

        AssessorFormInputResponsesResource responses = new AssessorFormInputResponsesResource(
                newAssessorFormInputResponseResource()
                        .with(id(null))
                        .withAssessment(assessmentId)
                        .withFormInput(formInputIdScore, formInputIdFeedback)
                        .withValue("10", "Feedback")
                        .build(2));

        when(assessorFormInputResponseRestService.updateFormInputResponses(responses))
                .thenReturn(restSuccess());

        mockMvc.perform(post("/{assessmentId}/question/{questionId}", assessmentId, questionId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param(formInputScoreField, "10")
                .param(formInputFeedbackField, "Feedback"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/%s", assessmentId)))
                .andReturn();

        verify(assessorFormInputResponseRestService, only()).updateFormInputResponses(responses);
    }

    @Test
    public void save_exceedsCharacterSizeLimit() throws Exception {
        Long applicationId = 1L;

        CompetitionResource competitionResource = setupCompetitionResource();

        AssessmentResource assessmentResource = setupAssessment(competitionResource.getId(), applicationId);

        QuestionResource questionResource = setupQuestion(assessmentResource.getId());

        setupQuestionNavigation(questionResource.getId(), empty(), empty());

        List<FormInputResource> formInputs = setupAssessmentFormInputs(questionResource.getId(), ASSESSOR_SCORE, TEXTAREA);

        Long formInputIdScore = formInputs.get(0).getId();
        Long formInputIdFeedback = formInputs.get(1).getId();
        String formInputScoreField = format("formInput[%s]", formInputIdScore);
        String formInputFeedbackField = format("formInput[%s]", formInputIdFeedback);

        AssessorFormInputResponsesResource responses = new AssessorFormInputResponsesResource(
                newAssessorFormInputResponseResource()
                        .with(id(null))
                        .withAssessment(assessmentResource.getId())
                        .withFormInput(formInputIdScore, formInputIdFeedback)
                        .withValue("10", "Feedback")
                        .build(2));

        when(assessorFormInputResponseRestService.updateFormInputResponses(responses)).thenReturn(restFailure(
                fieldError("responses[1].value", "Feedback", "validation.field.too.many.characters", "", "5000", "0")));

        // For re-display of question view following the invalid data entry
        List<FormInputResource> applicationFormInputs = setupApplicationFormInputs(questionResource.getId(), TEXTAREA);
        setupApplicantResponses(applicationId, applicationFormInputs);

        MvcResult result = mockMvc.perform(post("/{assessmentId}/question/{questionId}",
                assessmentResource.getId(), questionResource.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param(formInputScoreField, "10")
                .param(formInputFeedbackField, "Feedback"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form"))
                .andExpect(view().name("assessment/application-question"))
                .andReturn();

        verify(assessorFormInputResponseRestService, only()).updateFormInputResponses(responses);

        Form form = (Form) result.getModelAndView().getModel().get("form");

        assertEquals("10", form.getFormInput(formInputIdScore.toString()));
        assertEquals("Feedback", form.getFormInput(formInputIdFeedback.toString()));

        BindingResult bindingResult = form.getBindingResult();
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors(formInputFeedbackField));
        assertEquals("validation.field.too.many.characters", bindingResult.getFieldError(formInputFeedbackField).getCode());
        assertEquals("5000", bindingResult.getFieldError(formInputFeedbackField).getArguments()[1]);
        assertEquals("0", bindingResult.getFieldError(formInputFeedbackField).getArguments()[2]);
    }

    @Test
    public void save_scopeNotSelected() throws Exception {
        Long applicationId = 1L;

        CompetitionResource competitionResource = setupCompetitionResource();

        AssessmentResource assessmentResource = setupAssessment(competitionResource.getId(), applicationId);

        QuestionResource questionResource = setupQuestion(assessmentResource.getId());

        setupQuestionNavigation(questionResource.getId(), empty(), empty());

        List<FormInputResource> formInputs = setupAssessmentFormInputs(questionResource.getId(), ASSESSOR_APPLICATION_IN_SCOPE);

        Long formInputIdScope = formInputs.get(0).getId();
        String formInputScopeField = format("formInput[%s]", formInputIdScope);

        AssessorFormInputResponsesResource responses = new AssessorFormInputResponsesResource(
                newAssessorFormInputResponseResource()
                        .with(id(null))
                        .withAssessment(assessmentResource.getId())
                        .withFormInput(formInputIdScope)
                        .withValue("none")
                        .build(1));

        when(assessorFormInputResponseRestService.updateFormInputResponses(responses)).thenReturn(restFailure(
                fieldError("responses[0].value", "Scope", "validation.assessor.scope.invalidScope")));

        // For re-display of question view following the invalid data entry
        List<FormInputResource> applicationFormInputs = setupApplicationFormInputs(questionResource.getId(), TEXTAREA);
        setupApplicantResponses(applicationId, applicationFormInputs);

        MvcResult result = mockMvc.perform(post("/{assessmentId}/question/{questionId}",
                assessmentResource.getId(), questionResource.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param(formInputScopeField, "none"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form"))
                .andExpect(view().name("assessment/application-question"))
                .andReturn();

        verify(assessorFormInputResponseRestService, only()).updateFormInputResponses(responses);

        Form form = (Form) result.getModelAndView().getModel().get("form");

        assertEquals("none", form.getFormInput(formInputIdScope.toString()));
        BindingResult bindingResult = form.getBindingResult();
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors(formInputScopeField));
        assertEquals("validation.assessor.scope.invalidScope", bindingResult.getFieldError(formInputScopeField).getCode());
    }


    @Test
    public void save_exceedWordLimit() throws Exception {
        Long applicationId = 1L;

        CompetitionResource competitionResource = setupCompetitionResource();

        AssessmentResource assessmentResource = setupAssessment(competitionResource.getId(), applicationId);

        QuestionResource questionResource = setupQuestion(assessmentResource.getId());

        setupQuestionNavigation(questionResource.getId(), empty(), empty());

        List<FormInputResource> formInputs = setupAssessmentFormInputs(questionResource.getId(), ASSESSOR_SCORE, TEXTAREA);

        Long formInputIdScore = formInputs.get(0).getId();
        Long formInputIdFeedback = formInputs.get(1).getId();
        String formInputScoreField = format("formInput[%s]", formInputIdScore);
        String formInputFeedbackField = format("formInput[%s]", formInputIdFeedback);

        AssessorFormInputResponsesResource responses = new AssessorFormInputResponsesResource(
                newAssessorFormInputResponseResource()
                        .with(id(null))
                        .withAssessment(assessmentResource.getId())
                        .withFormInput(formInputIdScore, formInputIdFeedback)
                        .withValue("10", "Feedback")
                        .build(2));

        when(assessorFormInputResponseRestService.updateFormInputResponses(responses))
                .thenReturn(restFailure(fieldError("value", "Feedback", "validation.field.max.word.count", formInputIdFeedback, 100)));

        // For re-display of question view following the invalid data entry
        List<FormInputResource> applicationFormInputs = setupApplicationFormInputs(questionResource.getId(), TEXTAREA);
        setupApplicantResponses(applicationId, applicationFormInputs);

        MvcResult result = mockMvc.perform(post("/{assessmentId}/question/{questionId}",
                assessmentResource.getId(), questionResource.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param(formInputScoreField, "10")
                .param(formInputFeedbackField, "Feedback"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form"))
                .andExpect(view().name("assessment/application-question"))
                .andReturn();

        verify(assessorFormInputResponseRestService, only()).updateFormInputResponses(responses);

        Form form = (Form) result.getModelAndView().getModel().get("form");

        assertEquals("10", form.getFormInput(formInputIdScore.toString()));
        assertEquals("Feedback", form.getFormInput(formInputIdFeedback.toString()));

        BindingResult bindingResult = form.getBindingResult();
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors(formInputFeedbackField));
        assertEquals("validation.field.max.word.count", bindingResult.getFieldError(formInputFeedbackField).getCode());
        assertEquals("100", bindingResult.getFieldError(formInputFeedbackField).getArguments()[1]);
        assertEquals(formInputIdFeedback.toString(), bindingResult.getFieldError(formInputFeedbackField).getArguments()[0]);
    }

    private CompetitionResource setupCompetitionResource() {
        CompetitionResource competitionResource = newCompetitionResource()
                .withAssessorAcceptsDate(twoHoursAgo.minusDays(2))
                .withAssessorDeadlineDate(twoHoursAgo.plusDays(4))
                .build();

        when(competitionRestService.getCompetitionById(competitionResource.getId())).thenReturn(restSuccess(competitionResource));

        return competitionResource;
    }

    private SectionResource setupSection(SectionType sectionType) {
        SectionResource sectionResource = newSectionResource()
                .withType(sectionType)
                .build();

        when(sectionService.getById(sectionResource.getId())).thenReturn(sectionResource);

        return sectionResource;
    }

    private QuestionResource setupQuestion(long assessmentId) {
        QuestionResource questionResource = newQuestionResource()
                .withQuestionNumber("1")
                .withShortName("Market opportunity")
                .withName("1. What is the business opportunity that this project addresses?")
                .withAssessorMaximumScore(50)
                .withQuestionSetupType(QuestionSetupType.ASSESSED_QUESTION)
                .build();

        when(questionService.getByIdAndAssessmentId(questionResource.getId(), assessmentId))
                .thenReturn(questionResource);

        return questionResource;
    }

    private void setupQuestionNavigation(long questionId, Optional<QuestionResource> previous, Optional<QuestionResource> next) {
        when(questionService.getPreviousQuestion(questionId)).thenReturn(previous);
        when(questionService.getNextQuestion(questionId)).thenReturn(next);
    }

    private List<FormInputResource> setupApplicationFormInputs(long questionId, FormInputType... formInputTypes) {
        List<FormInputResource> formInputs = stream(formInputTypes).map(formInputType ->
                newFormInputResource()
                        .withType(formInputType)
                        .withQuestion(questionId)
                        .build()
        ).collect(toList());
        when(formInputRestService.getByQuestionIdAndScope(questionId, APPLICATION)).thenReturn(restSuccess(formInputs));
        return formInputs;
    }

    private List<FormInputResource> setupAssessmentFormInputs(long questionId, FormInputType... formInputTypes) {
        List<FormInputResource> formInputs = stream(formInputTypes).map(formInputType ->
                newFormInputResource()
                        .withType(formInputType)
                        .withQuestion(questionId)
                        .build()
        ).collect(toList());
        when(formInputRestService.getByQuestionIdAndScope(questionId, ASSESSMENT)).thenReturn(restSuccess(formInputs));
        return formInputs;
    }

    private List<FormInputResponseResource> setupApplicantResponses(Long applicationId, List<FormInputResource> formInputs) {
        List<FormInputResponseResource> applicantResponses = formInputs.stream().map(formInput -> {
                    if (formInput.getType() == FILEUPLOAD) {
                        return newFormInputResponseResource()
                                .withFormInputs(formInput.getId())
                                .withFileEntries(newFileEntryResource()
                                        .withId(1L, 2L)
                                        .withName("Appendix1.pdf", "Appendix2.pdf")
                                        .withFilesizeBytes(1024L)
                                        .build(2))
                                .withValue("Applicant response")
                                .build();
                    } else if (formInput.getType() == TEMPLATE_DOCUMENT) {
                        return newFormInputResponseResource()
                                .withFormInputs(formInput.getId())
                                .withValue("Applicant response")
                                .withFileEntries(newFileEntryResource()
                                        .withId(3L)
                                        .withName("template.pdf")
                                        .withFilesizeBytes(1024L)
                                        .build(1))
                                .build();
                    } else {
                        return newFormInputResponseResource()
                                .withFormInputs(formInput.getId())
                                .withValue("Applicant response")
                                .build();
                    }
                }
        ).collect(Collectors.toList());
        applicantResponses.forEach(formInputResponse -> when(formInputResponseRestService.getByFormInputIdAndApplication(
                formInputResponse.getFormInput(), applicationId)).thenReturn(restSuccess(singletonList(formInputResponse))));
        return applicantResponses;
    }

    private List<AssessorFormInputResponseResource> setupAssessorResponses(long assessmentId, long questionId, List<FormInputResource> formInputs) {
        List<AssessorFormInputResponseResource> assessorResponses = formInputs.stream().map(formInput ->
                newAssessorFormInputResponseResource()
                        .withFormInput(formInput.getId())
                        .withValue("Assessor response")
                        .withQuestion(questionId)
                        .build()
        ).collect(toList());
        when(assessorFormInputResponseRestService.getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId,
                questionId)).thenReturn(restSuccess(assessorResponses));
        return assessorResponses;
    }

    private AssessmentResource setupAssessment(long competitionId, long applicationId) {
        AssessmentResource assessmentResource = newAssessmentResource()
                .withApplication(applicationId)
                .withApplicationName("Application name")
                .withCompetition(competitionId)
                .build();
        when(assessmentService.getById(assessmentResource.getId())).thenReturn(assessmentResource);
        return assessmentResource;
    }

    private List<ResearchCategoryResource> setupResearchCategories() {
        List<ResearchCategoryResource> categories = newResearchCategoryResource()
                .withName("Research category")
                .build(1);

        when(categoryRestServiceMock.getResearchCategories()).thenReturn(restSuccess(categories));
        return categories;
    }
}
