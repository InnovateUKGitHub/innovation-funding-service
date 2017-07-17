package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.commons.builder.PageResourceBuilder;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AssessorCountSummaryPageResourceBuilder extends PageResourceBuilder<AssessorCountSummaryPageResource,AssessorCountSummaryPageResourceBuilder> {

    private AssessorCountSummaryPageResourceBuilder(List<BiConsumer<Integer, AssessorCountSummaryPageResource>> multiActions) {
        super(multiActions);
    }

    public static AssessorCountSummaryPageResourceBuilder newAssessorCountSummaryPageResource() {
        return new AssessorCountSummaryPageResourceBuilder(emptyList());
    }

    @Override
    protected AssessorCountSummaryPageResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessorCountSummaryPageResource>> actions) {
        return new AssessorCountSummaryPageResourceBuilder(actions);
    }

    @Override
    protected AssessorCountSummaryPageResource createInitial() {
        return new AssessorCountSummaryPageResource();
    }
}