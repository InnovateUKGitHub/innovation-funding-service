package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.assessment.resource.AssessorAssessmentResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AssessorAssessmentResourceBuilder extends BaseBuilder<AssessorAssessmentResource, AssessorAssessmentResourceBuilder> {

    public static AssessorAssessmentResourceBuilder newAssessorAssessmentResource() {
        return new AssessorAssessmentResourceBuilder(emptyList());
    }

    protected AssessorAssessmentResourceBuilder(List<BiConsumer<Integer, AssessorAssessmentResource>> newActions) {
        super(newActions);
    }

    @Override
    protected AssessorAssessmentResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessorAssessmentResource>> actions) {
        return new AssessorAssessmentResourceBuilder(actions);
    }

    @Override
    protected AssessorAssessmentResource createInitial() {
        return new AssessorAssessmentResource();
    }

    public AssessorAssessmentResourceBuilder withApplicationId(Long... applicationIds) {
        return withArraySetFieldByReflection("applicationId", applicationIds);
    }

    public AssessorAssessmentResourceBuilder withApplicationName(String... applicationName) {
        return withArraySetFieldByReflection("applicationName", applicationName);
    }

    public AssessorAssessmentResourceBuilder withLeadOrganisation(String... leadOrganisations) {
        return withArraySetFieldByReflection("leadOrganisation", leadOrganisations);
    }

    public AssessorAssessmentResourceBuilder withTotalAssessors(Integer... totalAssessors) {
        return withArraySetFieldByReflection("totalAssessors", totalAssessors);
    }

    public AssessorAssessmentResourceBuilder withState(AssessmentStates... states) {
        return withArraySetFieldByReflection("state", states);
    }
}
