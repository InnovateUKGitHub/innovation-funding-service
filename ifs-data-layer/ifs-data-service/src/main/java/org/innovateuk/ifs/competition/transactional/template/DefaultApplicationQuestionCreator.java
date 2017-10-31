package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.application.domain.GuidanceRow;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.FormValidator;
import org.innovateuk.ifs.form.repository.FormValidatorRepository;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.validator.NotEmptyValidator;
import org.innovateuk.ifs.validator.WordCountValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DefaultApplicationQuestionCreator {

    private static final Integer DEFAULT_APPLICANT_WORD_COUNT = 400;
    private static final Integer DEFAULT_FEEDBACK_WORD_COUNT = 100;
    private static final Integer DEFAULT_MAXIMUM_SCORE = 10;

    @Autowired
    private FormValidatorRepository formValidatorRepository;

    @NotSecured("Has to be secured by calling services.")
    public Question buildQuestion(Competition competition) {
        FormValidator notEmptyValidator = formValidatorRepository.findByClazzName(NotEmptyValidator.class.getName());
        FormValidator wordCountValidator = formValidatorRepository.findByClazzName(WordCountValidator.class.getName());

        FormInput maxWordCountInput = buildApplicantTextInput(competition, notEmptyValidator, wordCountValidator);
        FormInput questionScoreInput = buildQuestionScoreInput(competition, notEmptyValidator);
        FormInput feedbackInput = buildFeedbackInput(competition, notEmptyValidator, wordCountValidator);
        FormInput appendixInput = buildAppendixInput(competition);

        Question question = new Question();
        question.setCompetition(competition);
        question.setAssessorMaximumScore(DEFAULT_MAXIMUM_SCORE);
        question.setFormInputs(Arrays.asList(maxWordCountInput, questionScoreInput, feedbackInput, appendixInput));

        return question;
    }

    private FormInput buildApplicantTextInput(Competition competition, FormValidator notEmptyValidator, FormValidator wordCountValidator) {
        FormInput input = new FormInput();
        input.setWordCount(DEFAULT_APPLICANT_WORD_COUNT);
        input.setType(FormInputType.TEXTAREA);
        input.setCompetition(competition);
        input.setIncludedInApplicationSummary(true);
        input.setPriority(0);
        input.setScope(FormInputScope.APPLICATION);
        input.setActive(true);

        input.setInputValidators(Stream.of(notEmptyValidator, wordCountValidator).collect(Collectors.toSet()));

        return input;
    }

    private FormInput buildQuestionScoreInput(Competition competition, FormValidator notEmptyValidator) {
        FormInput input = new FormInput();
        input.setType(FormInputType.ASSESSOR_SCORE);
        input.setCompetition(competition);
        input.setIncludedInApplicationSummary(false);
        input.setPriority(0);
        input.setDescription("Feedback");
        input.setScope(FormInputScope.ASSESSMENT);
        input.setActive(true);

        input.setInputValidators(Stream.of(notEmptyValidator).collect(Collectors.toSet()));

        return input;
    }

    private FormInput buildAppendixInput(Competition competition) {
        FormInput input = new FormInput();
        input.setType(FormInputType.FILEUPLOAD);
        input.setCompetition(competition);
        input.setIncludedInApplicationSummary(true);
        input.setPriority(1);
        input.setScope(FormInputScope.APPLICATION);
        input.setActive(false);
        input.setGuidanceAnswer("<p>You may include an appendix of additional information to support the technical approach the project will undertake.</p><p>You may include, for example, a Gantt chart or project management structure.</p><p>The appendix should:</p><ul class=\\\"list-bullet\\\"><li>be in a portable document format (.pdf)</li><li>be readable with 100% magnification</li><li>contain your application number and project title at the top</li><li>not be any longer than 6 sides of A4. Longer appendices will only have the first 6 pages assessed</li><li>be less than 1mb in size</li></ul>");
        input.setGuidanceTitle("What should I include in the appendix?");
        input.setDescription("Appendix");

        return input;
    }

    private FormInput buildFeedbackInput(Competition competition, FormValidator notEmptyValidator, FormValidator wordCountValidator) {
        FormInput input = new FormInput();
        input.setWordCount(DEFAULT_FEEDBACK_WORD_COUNT);
        input.setType(FormInputType.TEXTAREA);
        input.setCompetition(competition);
        input.setIncludedInApplicationSummary(false);
        input.setPriority(0);
        input.setDescription("Feedback");
        input.setScope(FormInputScope.ASSESSMENT);
        input.setActive(true);

        GuidanceRow justificationRow1 = new GuidanceRow();
        justificationRow1.setPriority(4);
        justificationRow1.setSubject("1,2");
        GuidanceRow justificationRow2 = new GuidanceRow();
        justificationRow2.setPriority(3);
        justificationRow2.setSubject("3,4");
        GuidanceRow justificationRow3 = new GuidanceRow();
        justificationRow3.setPriority(2);
        justificationRow3.setSubject("5,6");
        GuidanceRow justificationRow4 = new GuidanceRow();
        justificationRow4.setPriority(1);
        justificationRow4.setSubject("7,8");
        GuidanceRow justificationRow5 = new GuidanceRow();
        justificationRow5.setPriority(0);
        justificationRow5.setSubject("9,10");

        input.setInputValidators(Stream.of(notEmptyValidator, wordCountValidator).collect(Collectors.toSet()));
        input.setGuidanceRows(Arrays.asList(justificationRow1, justificationRow2, justificationRow3, justificationRow4, justificationRow5));

        return input;
    }
}
