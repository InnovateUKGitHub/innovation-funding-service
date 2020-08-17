package org.innovateuk.ifs.management.competition.setup.application.validator;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.management.competition.setup.application.form.QuestionForm;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import static junit.framework.TestCase.assertEquals;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionSetupQuestionResourceBuilder.newCompetitionSetupQuestionResource;
import static org.junit.Assert.assertTrue;

public class CompetitionSetupApplicationQuestionValidatorTest {

    private CompetitionSetupApplicationQuestionValidator validator;
    private BindingResult bindingResult;

    @Before
    public void setup() {
        validator = new CompetitionSetupApplicationQuestionValidator();
    }

    @Test
    public void invalidApplicationQuestion() {

        long questionId = 1l;

        CompetitionResource competitionResource = newCompetitionResource()
                .withFundingType(FundingType.KTP)
                .build();

        CompetitionSetupQuestionResource competitionSetupQuestionResource = newCompetitionSetupQuestionResource().build();
        QuestionForm form = new QuestionForm();
        form.setQuestion(competitionSetupQuestionResource);

        DataBinder dataBinder = new DataBinder(form);
        bindingResult = dataBinder.getBindingResult();

        validator.validate(form, bindingResult, questionId, competitionResource);

        assertTrue(bindingResult.hasErrors());
        assertEquals(2, bindingResult.getErrorCount());
        assertEquals("This field cannot be left blank.", bindingResult.getFieldError("numberOfUploads").getDefaultMessage());
        assertEquals("This field cannot be left blank.", bindingResult.getFieldError("question.templateDocument").getDefaultMessage());
    }
}
