package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.application.resource.AssessorCountSummaryResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class AssessorCountSummaryResourceBuilder extends AssessmentCountSummaryResourceBuilder<AssessorCountSummaryResource, AssessorCountSummaryResourceBuilder> {

    private AssessorCountSummaryResourceBuilder(List<BiConsumer<Integer, AssessorCountSummaryResource>> multiActions) {
        super(multiActions);
    }

    public static AssessorCountSummaryResourceBuilder newAssessorCountSummaryResource() {
        return new AssessorCountSummaryResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected AssessorCountSummaryResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessorCountSummaryResource>> actions) {
        return new AssessorCountSummaryResourceBuilder(actions);
    }

    @Override
    protected AssessorCountSummaryResource createInitial() {
        return new AssessorCountSummaryResource();
    }

    public AssessorCountSummaryResourceBuilder withSkillAreas(String... skillAreasList) {
        return withArraySetFieldByReflection("skillAreas", skillAreasList);
    }

    public AssessorCountSummaryResourceBuilder withAssigned(Long... assigned) {
        return withArraySetFieldByReflection("assigned", assigned);
    }

    public AssessorCountSummaryResourceBuilder withTotalAssigned(Long... totalAssigned) {
        return withArraySetFieldByReflection("totalAssigned", totalAssigned);
    }
}