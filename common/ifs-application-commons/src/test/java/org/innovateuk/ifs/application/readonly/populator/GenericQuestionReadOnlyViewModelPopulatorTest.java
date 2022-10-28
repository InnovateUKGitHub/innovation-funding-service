package org.innovateuk.ifs.application.readonly.populator;

import com.google.common.collect.Lists;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.viewmodel.GenericQuestionReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationExpressionOfInterestConfigResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.assessment.builder.ApplicationAssessmentsResourceBuilder;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationAssessmentResourceBuilder.newApplicationAssessmentResource;
import static org.innovateuk.ifs.application.builder.ApplicationExpressionOfInterestConfigResourceBuilder.newApplicationExpressionOfInterestConfigResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings.defaultSettings;
import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GenericQuestionReadOnlyViewModelPopulatorTest {

    @InjectMocks
    private GenericQuestionReadOnlyViewModelPopulator populator;

    @Mock
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

    @Mock
    private OrganisationRestService organisationRestService;
    @Mock
    private QuestionStatusRestService questionStatusRestService;

    private ApplicationResource application;

    private CompetitionResource competition;

    private QuestionResource question;

    private UserResource user;

    @Before
    public void setup() {
        application = newApplicationResource()
                .build();
        competition = newCompetitionResource()
                .build();
        question = newQuestionResource()
                .withShortName("Question")
                .withName("Question text?")
                .withQuestionNumber("1")
                .withQuestionSetupType(QuestionSetupType.ASSESSED_QUESTION)
                .build();
        user = newUserResource().withRoleGlobal(Role.IFS_ADMINISTRATOR).build();

    }

    @Test
    public void populate() {
        Map<Long, BigDecimal> scores = new HashMap<>();
        scores.put(question.getId(), new BigDecimal("1"));
        Map<Long, String> feedbackStrings = new HashMap<>();
        feedbackStrings.put(question.getId(), "Feedback");

        FormInputResource textarea = newFormInputResource()
                .withType(FormInputType.TEXTAREA)
                .withScope(FormInputScope.APPLICATION)
                .withQuestion(question.getId())
                .build();
        FormInputResource appendix = newFormInputResource()
                .withType(FormInputType.FILEUPLOAD)
                .withScope(FormInputScope.APPLICATION)
                .withQuestion(question.getId())
                .build();
        FormInputResource templateDocument = newFormInputResource()
                .withType(FormInputType.TEMPLATE_DOCUMENT)
                .withScope(FormInputScope.APPLICATION)
                .withQuestion(question.getId())
                .withDescription("Document Title")
                .build();
        FormInputResource feedback = newFormInputResource()
                .withType(FormInputType.TEXTAREA)
                .withScope(FormInputScope.ASSESSMENT)
                .withQuestion(question.getId())
                .build();
        FormInputResource score = newFormInputResource()
                .withType(FormInputType.ASSESSOR_SCORE)
                .withScope(FormInputScope.ASSESSMENT)
                .withQuestion(question.getId())
                .build();
        FormInputResponseResource textareaResponse = newFormInputResponseResource()
                .withFormInputs(textarea.getId())
                .withValue("Some text")
                .build();
        FormInputResponseResource appendixResponse = newFormInputResponseResource()
                .withFormInputs(appendix.getId())
                .withFileEntries(newFileEntryResource()
                        .withName("Appendix1.pdf", "Appendix2.pdf")
                        .build(2))
                .build();
        FormInputResponseResource templateDocumentResponse = newFormInputResponseResource()
                .withFormInputs(templateDocument.getId())
                .withFileEntries(newFileEntryResource()
                        .withName("template.pdf")
                        .build(1))
                .build();
        AssessorFormInputResponseResource feedbackResponse = newAssessorFormInputResponseResource()
                .withFormInput(feedback.getId())
                .withQuestion(question.getId())
                .withValue("Feedback")
                .build();
        AssessorFormInputResponseResource scoreResponse = newAssessorFormInputResponseResource()
                .withFormInput(score.getId())
                .withQuestion(question.getId())
                .withValue("1")
                .build();

        ApplicationAssessmentResource assessorResponseFuture = newApplicationAssessmentResource()
                .withApplicationId(application.getId())
                .withTestId(3L)
                .withAveragePercentage(new BigDecimal("50.0"))
                .withScores(scores)
                .withFeedback(feedbackStrings)
                .build();

        ApplicationReadOnlyData data = new ApplicationReadOnlyData(application, competition, newUserResource().build(), emptyList(), emptyList(),
                asList(textarea, appendix, templateDocument, feedback, score), asList(textareaResponse, appendixResponse,
                templateDocumentResponse), emptyList(), singletonList(assessorResponseFuture), emptyList(), Optional.empty());

        GenericQuestionReadOnlyViewModel viewModel = populator.populate(question, data,
                ApplicationReadOnlySettings.defaultSettings().setAssessmentId(3L));

        assertEquals("Some text", viewModel.getAnswer());
        assertEquals("Appendix1.pdf", viewModel.getAppendices().get(0).getFilename());
        assertEquals("Appendix2.pdf", viewModel.getAppendices().get(1).getFilename());
        assertEquals("Question text?", viewModel.getQuestion());
        assertEquals("template.pdf", viewModel.getTemplateFile().getFilename());
        assertEquals("Document Title", viewModel.getTemplateDocumentTitle());

        assertEquals("1. Question", viewModel.getName());
        assertEquals(application.getId(), (Long) viewModel.getApplicationId());
        assertEquals(question.getId(), (Long) viewModel.getQuestionId());
        assertFalse(viewModel.isComplete());
        assertFalse(viewModel.isLead());

        assertTrue(viewModel.hasAssessorResponse());
        assertEquals("Feedback", viewModel.getFeedback().get(0));
        assertEquals(new BigDecimal("1"), viewModel.getScores().get(0));
    }

    @Test
    public void populateForSupporterReturnsCorrectDownloadUrlForFiles() {
        user = newUserResource().withRoleGlobal(Role.SUPPORTER).build();

        FormInputResource appendix = newFormInputResource()
                .withType(FormInputType.FILEUPLOAD)
                .withScope(FormInputScope.APPLICATION)
                .withQuestion(question.getId())
                .build();

        FileEntryResource appendixOneFileEntry = newFileEntryResource()
                .withName("Appendix1.pdf")
                .build();

        FileEntryResource appendixTwoFileEntry = newFileEntryResource()
                .withName("Appendix2.pdf")
                .build();

        FormInputResource templateDocument = newFormInputResource()
                .withType(FormInputType.TEMPLATE_DOCUMENT)
                .withScope(FormInputScope.APPLICATION)
                .withQuestion(question.getId())
                .withDescription("Document Title")
                .build();

        FormInputResponseResource appendixResponse = newFormInputResponseResource()
                .withFormInputs(appendix.getId())
                .withFileEntries(Arrays.asList(appendixOneFileEntry, appendixTwoFileEntry))
                .build();

        FileEntryResource templateFileEntry = newFileEntryResource()
                .withName("template.pdf")
                .build();

        FormInputResponseResource templateDocumentResponse = newFormInputResponseResource()
                .withFormInputs(templateDocument.getId())
                .withFileEntries(Collections.singletonList(templateFileEntry))
                .build();

        ApplicationReadOnlyData data = new ApplicationReadOnlyData(application, competition, user, emptyList(), emptyList(),
                asList(appendix, templateDocument), asList(appendixResponse, templateDocumentResponse), emptyList(), emptyList(), emptyList(), Optional.empty());

        GenericQuestionReadOnlyViewModel viewModel = populator.populate(question, data,
                ApplicationReadOnlySettings.defaultSettings().setAssessmentId(3L));

        assertEquals(String.format("/application/%d/form/question/%d/forminput/%d/file/%d/download", application.getId(),
                question.getId(), appendix.getId(), appendixOneFileEntry.getId()), viewModel.getAppendices().get(0).getUrl());
        assertEquals(String.format("/application/%d/form/question/%d/forminput/%d/file/%d/download", application.getId(),
                question.getId(), appendix.getId(), appendixTwoFileEntry.getId()), viewModel.getAppendices().get(1).getUrl());
        assertEquals(String.format("/application/%d/form/question/%d/forminput/%d/file/%d/download", application.getId(),
                question.getId(), templateDocument.getId(), templateFileEntry.getId()), viewModel.getTemplateFile().getUrl());
    }

    @Test
    public void populateForMultipleChoiceOptions() {
        FormInputResource multipleChoice = newFormInputResource()
                .withType(FormInputType.MULTIPLE_CHOICE)
                .withScope(FormInputScope.APPLICATION)
                .withQuestion(question.getId())
                .build();

        FormInputResponseResource multipleChoiceResponse = newFormInputResponseResource()
                .withFormInputs(multipleChoice.getId())
                .withMultipleChoiceOptionText("Some text")
                .build();

        ApplicationReadOnlyData data = new ApplicationReadOnlyData(application, competition, user, emptyList(), emptyList(),
                asList(multipleChoice), asList(multipleChoiceResponse), emptyList(), emptyList(), emptyList(), Optional.empty());

        GenericQuestionReadOnlyViewModel viewModel = populator.populate(question, data,
                ApplicationReadOnlySettings.defaultSettings().setAssessmentId(1L));

        assertEquals("Some text", viewModel.getAnswer());
    }

    @Test
    public void populateForKtpAssessment() {

        Long questionId = 1L;

        ApplicationResource application = newApplicationResource()
                .withResearchCategory(newResearchCategoryResource().withName("Research category").build())
                .build();
        CompetitionResource competition = newCompetitionResource()
                .build();
        QuestionResource question = newQuestionResource()
                .withId(questionId)
                .withShortName("Impact")
                .withQuestionSetupType(QuestionSetupType.KTP_ASSESSMENT)
                .build();
        List<ApplicationAssessmentResource> assessments = ApplicationAssessmentsResourceBuilder.newApplicationAssessmentResource()
                .withApplicationId(application.getId())
                .withAssessmentId(2L, 3L)
                .withScores(asMap(questionId, BigDecimal.ONE), asMap(questionId, BigDecimal.TEN))
                .withFeedback(asMap(questionId, "Feedback-1"), asMap(questionId, "Feedback-10"))
                .build(2);

        ApplicationReadOnlyData data = new ApplicationReadOnlyData(application, competition, newUserResource().build(),
                emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), assessments, emptyList(), Optional.empty());

        ApplicationReadOnlySettings settings = defaultSettings().setIncludeAllAssessorFeedback(true);

        GenericQuestionReadOnlyViewModel viewModel = populator.populate(question, data, settings);

        assertNotNull(viewModel);
        assertTrue(viewModel.isKtpAssessmentQuestion());
        assertEquals(questionId.longValue(), viewModel.getQuestionId());
        assertEquals(asList("Feedback-1", "Feedback-10"), viewModel.getFeedback());
        assertEquals(asList(BigDecimal.ONE, BigDecimal.TEN), viewModel.getScores());
        assertEquals(BigDecimal.valueOf(5.5), viewModel.getAverageScore());
    }

    @Test
    public void populateEOI() {
        Long questionId = 1L;

        ApplicationExpressionOfInterestConfigResource applicationExpressionOfInterestConfig = newApplicationExpressionOfInterestConfigResource()
                .withEnabledForExpressionOfInterest(true)
                .build();

        ApplicationResource application = newApplicationResource()
                .withResearchCategory(newResearchCategoryResource()
                        .withName("Research category")
                        .build())
                .withApplicationExpressionOfInterestConfigResource(applicationExpressionOfInterestConfig)
                .build();
        CompetitionResource competition = newCompetitionResource()
                .build();
        QuestionResource question = newQuestionResource()
                .withId(questionId)
                .withShortName("Question short name 1")
                .withQuestionSetupType(QuestionSetupType.ASSESSED_QUESTION)
                .withEnabledForPreRegistration(true)
                .build();

        ApplicationReadOnlyData data = new ApplicationReadOnlyData(application, competition, newUserResource().build(),
                emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), Optional.empty());

        ApplicationReadOnlySettings settings = defaultSettings()
                .setIncludeAllAssessorFeedback(true)
                .setIncludeQuestionNumber(true);

        GenericQuestionReadOnlyViewModel viewModel = populator.populate(question, data, settings);

        assertNotNull(viewModel);
        assertEquals("Question short name 1", viewModel.getDisplayName());
    }

    @Test
    public void populateEOIFullApplication() {
        Long questionId = 1L;

        ApplicationExpressionOfInterestConfigResource applicationExpressionOfInterestConfig = newApplicationExpressionOfInterestConfigResource()
                .withEnabledForExpressionOfInterest(false)
                .build();

        ApplicationResource application = newApplicationResource()
                .withResearchCategory(newResearchCategoryResource()
                        .withName("Research category")
                        .build())
                .withApplicationExpressionOfInterestConfigResource(applicationExpressionOfInterestConfig)
                .build();
        CompetitionResource competition = newCompetitionResource()
                .build();
        QuestionResource question = newQuestionResource()
                .withId(questionId)
                .withShortName("Question short name 1")
                .withQuestionSetupType(QuestionSetupType.ASSESSED_QUESTION)
                .withEnabledForPreRegistration(false)
                .build();

        ApplicationReadOnlyData data = new ApplicationReadOnlyData(application, competition, newUserResource().build(),
                emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), Optional.empty());

        ApplicationReadOnlySettings settings = defaultSettings()
                .setIncludeAllAssessorFeedback(true)
                .setIncludeQuestionNumber(true);

        GenericQuestionReadOnlyViewModel viewModel = populator.populate(question, data, settings);

        assertNotNull(viewModel);
        assertEquals("1. Question short name 1", viewModel.getDisplayName());
    }

    @Test
    public void populateImpactManagementApplicationWithIncompleteStatus() {
        Long questionId = 1L;

        ApplicationExpressionOfInterestConfigResource applicationExpressionOfInterestConfig = newApplicationExpressionOfInterestConfigResource()
                .withEnabledForExpressionOfInterest(false)
                .build();

        ApplicationResource application = newApplicationResource()
                .withResearchCategory(newResearchCategoryResource()
                        .withName("Research category")
                        .build())
                .withApplicationExpressionOfInterestConfigResource(applicationExpressionOfInterestConfig)
                .build();
        CompetitionResource competition = newCompetitionResource()
                .build();
        QuestionResource question = newQuestionResource()
                .withId(questionId)
                .withMultipleStatuses(true)
                .withShortName("Question short name 1")
                .withQuestionSetupType(QuestionSetupType.IMPACT_MANAGEMENT_SURVEY)
                .withEnabledForPreRegistration(false)
                .build();


        UserResource userResource = newUserResource().build();
        List<ProcessRoleResource> processRoleResources = newProcessRoleResource()
                .withOrganisation(1L)
                .withUserId(userResource.getId())
                .withRole(ProcessRoleType.LEADAPPLICANT, ProcessRoleType.COLLABORATOR)
                .build(2);


        ApplicationReadOnlyData data = new ApplicationReadOnlyData(application, competition, userResource,
                processRoleResources, emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), Optional.empty());

        ApplicationReadOnlySettings settings = defaultSettings()
                .setIncludeAllAssessorFeedback(true)
                .setIncludeQuestionNumber(true);


        OrganisationResource organisation = new OrganisationResource();
        organisation.setName("A");
        organisation.setId(1L);
        when(organisationRestService.getOrganisationById(1L)).thenReturn(restSuccess(organisation));
        QuestionStatusResource status = new QuestionStatusResource();
        status.setQuestion(question.getId());
        status.setApplication(application.getId());
        status.setId(1L);
        status.setMarkedAsComplete(false);

        when(questionStatusRestService.findByQuestionAndApplicationAndOrganisation(questionId, application.getId(), organisation.getId())).thenReturn(restSuccess(Lists.newArrayList(status)));

        GenericQuestionReadOnlyViewModel viewModel = populator.populate(question, data, settings);

        assertNotNull(viewModel);
        assertFalse(viewModel.isComplete());
        assertEquals("GenericQuestionAnswerRowReadOnlyViewModel should be populated with false", "Incomplete", viewModel.getAnswers().get(0).getAnswer());
    }

    @Test
    public void populateImpactManagementApplicationWithCompleteStatus() {
        Long questionId = 1L;

        ApplicationExpressionOfInterestConfigResource applicationExpressionOfInterestConfig = newApplicationExpressionOfInterestConfigResource()
                .withEnabledForExpressionOfInterest(false)
                .build();

        ApplicationResource application = newApplicationResource()
                .withResearchCategory(newResearchCategoryResource()
                        .withName("Research category")
                        .build())
                .withApplicationExpressionOfInterestConfigResource(applicationExpressionOfInterestConfig)
                .build();
        CompetitionResource competition = newCompetitionResource()
                .build();
        QuestionResource question = newQuestionResource()
                .withId(questionId)
                .withMultipleStatuses(true)
                .withShortName("Question short name 1")
                .withQuestionSetupType(QuestionSetupType.IMPACT_MANAGEMENT_SURVEY)
                .withEnabledForPreRegistration(false)
                .build();


        UserResource userResource = newUserResource().build();
        List<ProcessRoleResource> processRoleResources = newProcessRoleResource()
                .withOrganisation(1L)
                .withUserId(userResource.getId())
                .withRole(ProcessRoleType.LEADAPPLICANT, ProcessRoleType.COLLABORATOR)
                .build(2);


        ApplicationReadOnlyData data = new ApplicationReadOnlyData(application, competition, userResource,
                processRoleResources, emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), Optional.empty());

        ApplicationReadOnlySettings settings = defaultSettings()
                .setIncludeAllAssessorFeedback(true)
                .setIncludeQuestionNumber(true);


        OrganisationResource organisation = new OrganisationResource();
        organisation.setName("A");
        organisation.setId(1L);
        when(organisationRestService.getOrganisationById(1L)).thenReturn(restSuccess(organisation));
        QuestionStatusResource status = new QuestionStatusResource();
        status.setQuestion(question.getId());
        status.setApplication(application.getId());
        status.setId(1L);
        status.setMarkedAsComplete(true);


        when(questionStatusRestService.findByQuestionAndApplicationAndOrganisation(questionId, application.getId(), organisation.getId())).thenReturn(restSuccess(Lists.newArrayList(status)));

        GenericQuestionReadOnlyViewModel viewModel = populator.populate(question, data, settings);

        assertNotNull(viewModel);
        assertTrue(viewModel.isComplete());
        assertEquals("GenericQuestionAnswerRowReadOnlyViewModel should be populated with true", "Complete", viewModel.getAnswers().get(0).getAnswer());
    }
    @Test
    public void populateImpactManagementApplicationWithIncompleteAndCompleteStatus() {
        Long questionId = 1L;

        ApplicationExpressionOfInterestConfigResource applicationExpressionOfInterestConfig = newApplicationExpressionOfInterestConfigResource()
                .withEnabledForExpressionOfInterest(false)
                .build();

        ApplicationResource application = newApplicationResource()
                .withResearchCategory(newResearchCategoryResource()
                        .withName("Research category")
                        .build())
                .withApplicationExpressionOfInterestConfigResource(applicationExpressionOfInterestConfig)
                .build();
        CompetitionResource competition = newCompetitionResource()
                .build();
        QuestionResource question = newQuestionResource()
                .withId(questionId)
                .withMultipleStatuses(true)
                .withShortName("Question short name 1")
                .withQuestionSetupType(QuestionSetupType.IMPACT_MANAGEMENT_SURVEY)
                .withEnabledForPreRegistration(false)
                .build();


        UserResource userResource = newUserResource().build();
        UserResource userResource2 = newUserResource().build();
        List<ProcessRoleResource> processRoleResources = newProcessRoleResource()
                .withOrganisation(1L,2L)
                .withUserId(userResource.getId(),userResource2.getId())
                .withRole(ProcessRoleType.LEADAPPLICANT, ProcessRoleType.COLLABORATOR)
                .build(2);


        ApplicationReadOnlyData data = new ApplicationReadOnlyData(application, competition, userResource,
                processRoleResources, emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), Optional.empty());

        ApplicationReadOnlySettings settings = defaultSettings()
                .setIncludeAllAssessorFeedback(true)
                .setIncludeQuestionNumber(true);


        OrganisationResource organisation = new OrganisationResource();
        organisation.setName("A2");
        organisation.setId(1L);

        OrganisationResource organisation2 = new OrganisationResource();
        organisation2.setName("A4");
        organisation2.setId(2L);

        when(organisationRestService.getOrganisationById(1L)).thenReturn(restSuccess(organisation));
        when(organisationRestService.getOrganisationById(2L)).thenReturn(restSuccess(organisation2));

        QuestionStatusResource status = new QuestionStatusResource();
        status.setQuestion(question.getId());
        status.setApplication(application.getId());
        status.setId(1L);
        status.setMarkedAsComplete(true);

        QuestionStatusResource status2 = new QuestionStatusResource();
        status2.setQuestion(question.getId());
        status2.setApplication(application.getId());
        status2.setId(2L);
        status2.setMarkedAsComplete(false);


        when(questionStatusRestService.findByQuestionAndApplicationAndOrganisation(questionId, application.getId(), organisation.getId())).thenReturn(restSuccess(Lists.newArrayList(status)));
        when(questionStatusRestService.findByQuestionAndApplicationAndOrganisation(questionId, application.getId(), organisation2.getId())).thenReturn(restSuccess(Lists.newArrayList(status2)));

        GenericQuestionReadOnlyViewModel viewModel = populator.populate(question, data, settings);

        assertNotNull(viewModel);
        assertFalse(viewModel.isComplete());
        assertEquals("GenericQuestionAnswerRowReadOnlyViewModel should be populated with true", "Complete", viewModel.getAnswers().get(0).getAnswer());
        assertEquals("GenericQuestionAnswerRowReadOnlyViewModel should be populated with false", "Incomplete", viewModel.getAnswers().get(1).getAnswer());
    }
}
