package com.worth.ifs.application.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.resource.FormInputGuidanceRowResource;
import com.worth.ifs.application.resource.CompetitionSetupQuestionResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class QuestionAssessmentResourceBuilder extends BaseBuilder<CompetitionSetupQuestionResource, QuestionAssessmentResourceBuilder> {

    private QuestionAssessmentResourceBuilder(List<BiConsumer<Integer, CompetitionSetupQuestionResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected QuestionAssessmentResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionSetupQuestionResource>> actions) {
        return new QuestionAssessmentResourceBuilder(actions);
    }

    public static QuestionAssessmentResourceBuilder newQuestionAssessment() {
        return new QuestionAssessmentResourceBuilder(emptyList())
                .with(uniqueIds());
    }

    public QuestionAssessmentResourceBuilder withId(Long... ids) {
        return withArray((id, address) -> setField("id", id, address), ids);
    }


    public QuestionAssessmentResourceBuilder withScored(Boolean scored) {
        return with(questionAssessment -> setField("scored", scored, questionAssessment));
    }

    public QuestionAssessmentResourceBuilder withScoreTotal(Integer scoreTotal) {
        return with(questionAssessment -> setField("scoreTotal", scoreTotal, questionAssessment));
    }

    public QuestionAssessmentResourceBuilder withWrittenFeedback(Boolean writtenFeedback) {
        return with(questionAssessment -> setField("writtenFeedback", writtenFeedback, questionAssessment));
    }

    public QuestionAssessmentResourceBuilder withGuidance(String guidance) {
        return with(questionAssessment -> setField("guidance", guidance, questionAssessment));
    }

    public QuestionAssessmentResourceBuilder withWordCount(Integer wordCount) {
        return with(questionAssessment -> setField("wordCount", wordCount, questionAssessment));
    }

    public QuestionAssessmentResourceBuilder withScoreRows(List<FormInputGuidanceRowResource> scoreRows) {
        return with(questionAssessment -> setField("scoreRows", scoreRows, questionAssessment));
    }

    @Override
    protected CompetitionSetupQuestionResource createInitial() {
        return new CompetitionSetupQuestionResource();
    }
}
