package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.workflow.resource.ProcessEvent;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class AssessmentResourceBuilder extends BaseBuilder<AssessmentResource, AssessmentResourceBuilder> {

    private AssessmentResourceBuilder(List<BiConsumer<Integer, AssessmentResource>> multiActions) {
        super(multiActions);
    }

    public static AssessmentResourceBuilder newAssessmentResource() {
        return new AssessmentResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected AssessmentResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentResource>> actions) {
        return new AssessmentResourceBuilder(actions);
    }

    @Override
    protected AssessmentResource createInitial() {
        return new AssessmentResource();
    }

    public AssessmentResourceBuilder withId(Long... values) {
        return withArraySetFieldByReflection("id", values);
    }

    public AssessmentResourceBuilder withProcessEvent(ProcessEvent... processEvents) {
        return withArray((processEvent, object) -> setField("event", processEvent.name(), object), processEvents);
    }

    public AssessmentResourceBuilder withLastModifiedDate(Calendar... values) {
        return withArraySetFieldByReflection("lastModified", values);
    }

    public AssessmentResourceBuilder withStartDate(LocalDate... values) {
        return withArraySetFieldByReflection("startDate", values);
    }

    public AssessmentResourceBuilder withEndDate(LocalDate... values) {
        return withArraySetFieldByReflection("endDate", values);
    }

    public AssessmentResourceBuilder withProcessOutcome(List<Long>... values) {
        return withArraySetFieldByReflection("processOutcomes", values);
    }

    public AssessmentResourceBuilder withProcessRole(Long... values) {
        return withArraySetFieldByReflection("processRole", values);
    }

    public AssessmentResourceBuilder withApplication(Long... values) {
        return withArraySetFieldByReflection("application", values);
    }

    public AssessmentResourceBuilder withApplicationName(String... values) {
        return withArraySetFieldByReflection("applicationName", values);
    }

    public AssessmentResourceBuilder withCompetition(Long... values) {
        return withArraySetFieldByReflection("competition", values);
    }

    public AssessmentResourceBuilder withActivityState(AssessmentStates... values) {
        return withArraySetFieldByReflection("assessmentState", values);
    }
}
