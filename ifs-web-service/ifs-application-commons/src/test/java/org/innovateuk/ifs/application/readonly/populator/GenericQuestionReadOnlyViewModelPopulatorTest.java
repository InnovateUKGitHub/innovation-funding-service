package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.viewmodel.GenericQuestionReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class GenericQuestionReadOnlyViewModelPopulatorTest {

    @InjectMocks
    private GenericQuestionReadOnlyViewModelPopulator populator;

    private ApplicationResource application;

    private CompetitionResource competition;

    private QuestionResource question;

    private  UserResource user;

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
                .withFileName("Appendix.pdf")
                .build();
        FormInputResponseResource templateDocumentResponse = newFormInputResponseResource()
                .withFormInputs(templateDocument.getId())
                .withFileName("template.pdf")
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

        ApplicationReadOnlyData data = new ApplicationReadOnlyData(application, competition, newUserResource().build(), empty(), emptyList(),
                asList(textarea, appendix, templateDocument, feedback, score), asList(textareaResponse, appendixResponse,
                templateDocumentResponse), emptyList(), asList(feedbackResponse, scoreResponse));

        GenericQuestionReadOnlyViewModel viewModel = populator.populate(competition, question, data,
                ApplicationReadOnlySettings.defaultSettings().setAssessmentId(1L));

        assertEquals("Some text", viewModel.getAnswer());
        assertEquals("Appendix.pdf", viewModel.getAppendixFilename());
        assertEquals("Question text?", viewModel.getQuestion());
        assertEquals(appendix.getId(), viewModel.getAppendixId());
        assertEquals("template.pdf", viewModel.getTemplateDocumentFilename());
        assertEquals(templateDocument.getId(), viewModel.getTemplateDocumentId());
        assertEquals("Document Title", viewModel.getTemplateDocumentTitle());

        assertEquals("1. Question", viewModel.getName());
        assertEquals(application.getId(), (Long) viewModel.getApplicationId());
        assertEquals(question.getId(), (Long) viewModel.getQuestionId());
        assertFalse(viewModel.isComplete());
        assertFalse(viewModel.isLead());

        assertTrue(viewModel.hasAssessorResponse());
        assertEquals("Feedback", viewModel.getFeedback());
        assertEquals("1", viewModel.getScore());
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

        ApplicationReadOnlyData data = new ApplicationReadOnlyData(application, competition, newUserResource().build(), empty(), emptyList(),
                asList(multipleChoice), asList(multipleChoiceResponse), emptyList(), emptyList());

        GenericQuestionReadOnlyViewModel viewModel = populator.populate(competition, question, data,
                ApplicationReadOnlySettings.defaultSettings().setAssessmentId(1L));

        assertEquals("Some text", viewModel.getAnswer());
    }
}
