package com.worth.ifs.application.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.resource.FormInputGuidanceRowResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class AssessmentScoreRowResourceBuilder extends BaseBuilder<FormInputGuidanceRowResource, AssessmentScoreRowResourceBuilder> {

    private AssessmentScoreRowResourceBuilder(List<BiConsumer<Integer, FormInputGuidanceRowResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected AssessmentScoreRowResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FormInputGuidanceRowResource>> actions) {
        return new AssessmentScoreRowResourceBuilder(actions);
    }

    public static AssessmentScoreRowResourceBuilder newAssessmentScoreRow() {
        return new AssessmentScoreRowResourceBuilder(emptyList())
                .with(uniqueIds());
    }

    public AssessmentScoreRowResourceBuilder withStart(Integer start) {
        return with(assessmentScoreRow -> setField("start", start, assessmentScoreRow));
    }

    public AssessmentScoreRowResourceBuilder withEnd(Integer end) {
        return with(assessmentScoreRow -> setField("end", end, assessmentScoreRow));
    }

    public AssessmentScoreRowResourceBuilder withJustification(String justification) {
        return with(assessmentScoreRow -> setField("justification", justification, assessmentScoreRow));
    }

    @Override
    protected FormInputGuidanceRowResource createInitial() {
        return new FormInputGuidanceRowResource();
    }
}
