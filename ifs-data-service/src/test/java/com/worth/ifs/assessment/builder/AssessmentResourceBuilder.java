package com.worth.ifs.assessment.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.workflow.resource.ProcessEvent;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

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

    public AssessmentResourceBuilder withId(Long... ids) {
        return withArray((id, assessment) -> setField("id", id, assessment), ids);
    }

    public AssessmentResourceBuilder withProcessEvent(ProcessEvent... processEvents) {
        return withArray((processEvent, object) -> setField("event", processEvent.name(), object), processEvents);
    }

    public AssessmentResourceBuilder withLastModifiedDate(Calendar... lastModifiedDates) {
        return withArray((lastModifiedDate, object) -> setField("lastModified", lastModifiedDate, object), lastModifiedDates);
    }

    public AssessmentResourceBuilder withStartDate(LocalDate... startDates) {
        return withArray((startDate, object) -> setField("startDate", startDate, object), startDates);
    }

    public AssessmentResourceBuilder withEndDate(LocalDate... endDates) {
        return withArray((endDate, object) -> setField("endDate", endDate, object), endDates);
    }

    public AssessmentResourceBuilder withProcessOutcome(List<Long>... processOutcomes) {
        return withArray((processOutcome, object) -> setField("processOutcomes", processOutcome, object), processOutcomes);
    }

    public AssessmentResourceBuilder withProcessRole(Long... processRoles) {
        return withArray((processRole, object) -> setField("processRole", processRole, object), processRoles);
    }

    public AssessmentResourceBuilder withSubmitted(Boolean... submittedValues) {
        return withArray((submitted, object) -> setField("submitted", submitted, object), submittedValues);
    }

    public AssessmentResourceBuilder withStarted(Boolean... startedValues) {
        return withArray((started, object) -> setField("started", started, object), startedValues);
    }

    public AssessmentResourceBuilder withApplication(Long... applications) {
        return withArray((application, object) -> setField("application", application, object), applications);
    }

    public AssessmentResourceBuilder withCompetition(Long... competitions) {
        return withArray((competition, object) -> setField("competition", competition, object), competitions);
    }

    public AssessmentResourceBuilder withActivityState(AssessmentStates... states) {
        return withArray((state, object) -> object.setAssessmentState(state), states);
    }
}
