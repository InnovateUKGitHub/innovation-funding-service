package com.worth.ifs.application.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.domain.*;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.*;
import static java.util.Collections.emptyList;

public class AssessmentScoreRowBuilder extends BaseBuilder<FormInputGuidanceRow, AssessmentScoreRowBuilder> {

    private AssessmentScoreRowBuilder(List<BiConsumer<Integer, FormInputGuidanceRow>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected AssessmentScoreRowBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FormInputGuidanceRow>> actions) {
        return new AssessmentScoreRowBuilder(actions);
    }

    public static AssessmentScoreRowBuilder newAssessmentScoreRow() {
        return new AssessmentScoreRowBuilder(emptyList())
                .with(uniqueIds());
    }

    public AssessmentScoreRowBuilder withQuestionAssessment(QuestionAssessment questionAssessment) {
        return with(assessmentScoreRow -> setField("questionAssessment", questionAssessment, assessmentScoreRow));
    }

    public AssessmentScoreRowBuilder witStart(Integer start) {
        return with(assessmentScoreRow -> setField("start", start, assessmentScoreRow));
    }

    public AssessmentScoreRowBuilder withEnd(Integer end) {
        return with(assessmentScoreRow -> setField("end", end, assessmentScoreRow));
    }

    public AssessmentScoreRowBuilder withJustification(String justification) {
        return with(assessmentScoreRow -> setField("justification", justification, assessmentScoreRow));
    }

    @Override
    protected FormInputGuidanceRow createInitial() {
        return new FormInputGuidanceRow();
    }
}
