package com.worth.ifs.application.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.domain.FormInputGuidanceRow;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.QuestionAssessment;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class QuestionAssessmentBuilder extends BaseBuilder<QuestionAssessment, QuestionAssessmentBuilder> {

    private QuestionAssessmentBuilder(List<BiConsumer<Integer, QuestionAssessment>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected QuestionAssessmentBuilder createNewBuilderWithActions(List<BiConsumer<Integer, QuestionAssessment>> actions) {
        return new QuestionAssessmentBuilder(actions);
    }

    public static QuestionAssessmentBuilder newQuestionAssessment() {
        return new QuestionAssessmentBuilder(emptyList())
                .with(uniqueIds());
    }

    public QuestionAssessmentBuilder withQuestion(Question question) {
        return with(questionAssessment -> setField("question", question, questionAssessment));
    }

    public QuestionAssessmentBuilder withScored(Boolean scored) {
        return with(questionAssessment -> setField("scored", scored, questionAssessment));
    }

    public QuestionAssessmentBuilder withScoreTotal(Integer scoreTotal) {
        return with(questionAssessment -> setField("scoreTotal", scoreTotal, questionAssessment));
    }

    public QuestionAssessmentBuilder withWrittenFeedback(Boolean writtenFeedback) {
        return with(questionAssessment -> setField("writtenFeedback", writtenFeedback, questionAssessment));
    }

    public QuestionAssessmentBuilder withGuidance(String guidance) {
        return with(questionAssessment -> setField("guidance", guidance, questionAssessment));
    }

    public QuestionAssessmentBuilder withWordCount(Integer wordCount) {
        return with(questionAssessment -> setField("wordCount", wordCount, questionAssessment));
    }

    public QuestionAssessmentBuilder withScoreRows(List<FormInputGuidanceRow> scoreRows) {
        return with(questionAssessment -> setField("scoreRows", scoreRows, questionAssessment));
    }

    @Override
    protected QuestionAssessment createInitial() {
        return new QuestionAssessment();
    }
}
