package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.viewmodel.GenericQuestionReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
                .withQuestion(question.getId())
                .build();
        FormInputResource appendix = newFormInputResource()
                .withType(FormInputType.FILEUPLOAD)
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

        ApplicationReadOnlyData data = new ApplicationReadOnlyData(application, competition, newUserResource().build(), empty(), emptyList(), asList(textarea, appendix), asList(textareaResponse, appendixResponse), emptyList());

        GenericQuestionReadOnlyViewModel viewModel = populator.populate(question, data);

        assertEquals("Some text", viewModel.getAnswer());
        assertEquals("Appendix.pdf", viewModel.getAppendixFilename());
        assertEquals("Question text?", viewModel.getQuestion());

        assertEquals("1. Question", viewModel.getName());
        assertEquals(application.getId(), (Long) viewModel.getApplicationId());
        assertEquals(question.getId(), (Long) viewModel.getQuestionId());
        assertFalse(viewModel.isComplete());
        assertFalse(viewModel.isLead());
    }
}
