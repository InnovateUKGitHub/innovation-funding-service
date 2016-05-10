package com.worth.ifs.assessment.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.workflow.resource.ProcessEvent;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import com.worth.ifs.workflow.resource.ProcessStates;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Arrays.asList;
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

    public AssessmentResourceBuilder withProcessState(String... processStates) {
        return withArray((processState, assessment) -> assessment.setStatus(processState), processStates);
    }

    public AssessmentResourceBuilder withProcessRole(ProcessRoleResource processRole) {
        return with(assessment -> assessment.setProcessRole(processRole.getId()));
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

    public AssessmentResourceBuilder withProcessStatus(ProcessStates... processStates) {
        return withArray((processStatus, object) -> setField("status", processStatus.getState(), object), processStates);
    }

    public AssessmentResourceBuilder withProcessEvent(ProcessEvent... processEvents) {
        return withArray((processEvent, object) -> setField("event", processEvent.name(), object), processEvents);
    }

    public AssessmentResourceBuilder withProcessRole(ProcessRoleResource... processRoles) {
        return withArray((processRole, object) -> setField("processRole", processRole, object), processRoles);
    }

}
