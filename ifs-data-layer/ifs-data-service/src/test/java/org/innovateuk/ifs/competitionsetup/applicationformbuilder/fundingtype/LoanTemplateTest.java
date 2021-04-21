package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingtype;

import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.*;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.equalityDiversityAndInclusion;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder.aQuestion;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LoanTemplateTest {

    private LoanTemplate loanTemplate;

    @Before
    public void setUp() {
        loanTemplate = new LoanTemplate();
    }

    @Test
    public void shouldOverrideEdiQuestionDescription() {
        List<SectionBuilder> sections = newArrayList(
                projectDetails()
                    .withQuestions(newArrayList(
                            aQuestion()
                                    .withShortName("This is not the EDI question")
                                    .withName("This is not the EDI question")
                                    .withDescription("not EDI, should not be overridden"),
                            equalityDiversityAndInclusion()
                )),
                termsAndConditions());

        List<SectionBuilder> result = loanTemplate.sections(sections);

        Question nonEdiQuestion = result.get(0).getQuestions().get(0).build();
        assertEquals("not EDI, should not be overridden", nonEdiQuestion.getDescription());

        Question ediQuestion = result.get(0).getQuestions().get(1).build();
        assertTrue(ediQuestion.getDescription().contains("https://bit.ly/EDIForm"));
    }

    @Test
    public void shouldRenameSectionsAndTermsQuestion() {
        List<SectionBuilder> sections = newArrayList(
                projectDetails()
                        .withName("Old project details section name"),
                termsAndConditions()
                        .withQuestions(newArrayList(
                                aQuestion()
                                        .withQuestionSetupType(QuestionSetupType.TERMS_AND_CONDITIONS)
                                        .withShortName("Old terms question short name")
                                        .withName("Old terms question name")
                                        .withDescription("Old terms question description")
                        )),
                finances()
                    .withName("Old finances section name"));

        List<SectionBuilder> result = loanTemplate.sections(sections);

        SectionBuilder projectDetails = result.get(0);
        assertThat(projectDetails.getName()).isEqualTo("Applicant details");

        QuestionBuilder termsQuestion = result.get(1).getQuestions().get(0);
        assertThat(termsQuestion.getName()).isEqualTo("Loan terms and conditions");
        assertThat(termsQuestion.getShortName()).isEqualTo("Loan terms and conditions");
        assertThat(termsQuestion.getDescription()).isEqualTo("Loan terms and conditions");

        SectionBuilder finances = result.get(2);
        assertEquals("Project finance", finances.getName());
    }
}
