package com.worth.ifs.assessment.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.assessment.resource.AssessmentFundingDecisionResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static java.util.Collections.emptyList;

public class AssessmentFundingDecisionResourceBuilder extends BaseBuilder<AssessmentFundingDecisionResource, AssessmentFundingDecisionResourceBuilder> {

    private AssessmentFundingDecisionResourceBuilder(List<BiConsumer<Integer, AssessmentFundingDecisionResource>> multiActions) {
        super(multiActions);
    }

    public static AssessmentFundingDecisionResourceBuilder newAssessmentFundingDecisionResource() {
        return new AssessmentFundingDecisionResourceBuilder(emptyList());
    }

    @Override
    protected AssessmentFundingDecisionResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentFundingDecisionResource>> actions) {
        return new AssessmentFundingDecisionResourceBuilder(actions);
    }

    @Override
    protected AssessmentFundingDecisionResource createInitial() {
        return new com.worth.ifs.assessment.resource.AssessmentFundingDecisionResourceBuilder().createAssessmentFundingDecisionResource();
    }

    public AssessmentFundingDecisionResourceBuilder withFundingConfirmation(Boolean... fundingConfirmations) {
        return withArray((fundingConfirmation, assessmentFundingDecisionResource) -> setField("fundingConfirmation", fundingConfirmation, assessmentFundingDecisionResource), fundingConfirmations);
    }

    public AssessmentFundingDecisionResourceBuilder withComment(String... comments) {
        return withArray((comment, assessmentFundingDecisionResource) -> setField("comment", comment, assessmentFundingDecisionResource), comments);
    }

    public AssessmentFundingDecisionResourceBuilder withFeedback(String... feedbacks) {
        return withArray((feedback, assessmentFundingDecisionResource) -> setField("feedback", feedback, assessmentFundingDecisionResource), feedbacks);
    }
}