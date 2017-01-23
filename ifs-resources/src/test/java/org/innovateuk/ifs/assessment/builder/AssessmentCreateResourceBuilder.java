package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.resource.AssessmentCreateResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AssessmentCreateResourceBuilder extends BaseBuilder<AssessmentCreateResource, AssessmentCreateResourceBuilder> {

    public AssessmentCreateResourceBuilder(List<BiConsumer<Integer, AssessmentCreateResource>> actions) {
        super(actions);
    }

    public static AssessmentCreateResourceBuilder newAssessmentCreateResource() {
        return new AssessmentCreateResourceBuilder(emptyList());
    }

    public AssessmentCreateResourceBuilder withApplicationId(Long ...applicationId) {
        return withArraySetFieldByReflection("applicationId", applicationId);
    }

    public AssessmentCreateResourceBuilder withAssessorId(Long ...assessorId) {
        return withArraySetFieldByReflection("assessorId", assessorId);
    }

    @Override
    protected AssessmentCreateResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentCreateResource>> actions) {
        return new AssessmentCreateResourceBuilder(actions);
    }

    @Override
    protected AssessmentCreateResource createInitial() {
        return new AssessmentCreateResource();
    }
}
