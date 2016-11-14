package com.worth.ifs.assessment.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.BuilderAmendFunctions;
import com.worth.ifs.assessment.resource.AssessmentSubmissionsResource;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public class AssessmentSubmissionsResourceBuilder extends BaseBuilder<AssessmentSubmissionsResource, AssessmentSubmissionsResourceBuilder> {

    private AssessmentSubmissionsResourceBuilder(List<BiConsumer<Integer, AssessmentSubmissionsResource>> multiActions) {
        super(multiActions);
    }

    public static AssessmentSubmissionsResourceBuilder newAssessmentSubmissionsResource() {
        return new AssessmentSubmissionsResourceBuilder(Collections.emptyList());
    }

    @Override
    protected AssessmentSubmissionsResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentSubmissionsResource>> actions) {
        return new AssessmentSubmissionsResourceBuilder(actions);
    }

    @Override
    protected AssessmentSubmissionsResource createInitial() {
        return new AssessmentSubmissionsResource();
    }

    public AssessmentSubmissionsResourceBuilder withAssessmentIds(List<Long> ...assessmentIds) {
        return withArray((ids, resource) -> BuilderAmendFunctions.setField("assessmentIds", ids, resource), assessmentIds);
    }
}
