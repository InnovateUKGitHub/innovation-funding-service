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
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class GenericQuestionReadOnlyViewModelPopulatorTest {

    @InjectMocks
    private GenericQuestionReadOnlyViewModelPopulator populator;

    @Test
    public void populate() {
        ApplicationResource application = newApplicationResource()
                .build();
        CompetitionResource competition = newCompetitionResource()
                .build();
        QuestionResource question = newQuestionResource()
                .withShortName("Question")
                .withName("Question text?")
                .withQuestionNumber("1")
                .withQuestionSetupType(QuestionSetupType.ASSESSED_QUESTION)
                .build();
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
        UserResource user = newUserResource().withRoleGlobal(Role.IFS_ADMINISTRATOR).build();

        ApplicationReadOnlyData data = new ApplicationReadOnlyData(application, competition, newUserResource().build(), empty(), emptyList(), asList(textarea, appendix, templateDocument, feedback, score), asList(textareaResponse, appendixResponse, templateDocumentResponse), emptyList(), asList(feedbackResponse, scoreResponse));

        GenericQuestionReadOnlyViewModel viewModel = populator.populate(competition, question, data, ApplicationReadOnlySettings.defaultSettings().setAssessmentId(1L));

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
        assertEquals("Feedback", viewModel.getFeedback());
        assertEquals("1", viewModel.getScore());
    }
}
