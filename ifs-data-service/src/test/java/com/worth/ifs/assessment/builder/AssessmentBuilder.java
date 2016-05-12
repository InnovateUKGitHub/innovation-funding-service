package com.worth.ifs.assessment.builder;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.workflow.resource.ProcessEvent;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import com.worth.ifs.workflow.resource.ProcessStates;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class AssessmentBuilder extends BaseBuilder<Assessment, AssessmentBuilder> {

    private AssessmentBuilder(List<BiConsumer<Integer, Assessment>> multiActions) {
        super(multiActions);
    }

    public static AssessmentBuilder newAssessment() {
        return new AssessmentBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected AssessmentBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Assessment>> actions) {
        return new AssessmentBuilder(actions);
    }

    @Override
    protected Assessment createInitial() {
        return new Assessment();
    }

    public AssessmentBuilder withId(Long... ids) {
        return withArray((id, assessment) -> setField("id", id, assessment), ids);
    }

    public AssessmentBuilder withProcessState(String... processStates) {
        return withArray((processState, assessment) -> assessment.setProcessStatus(processState), processStates);
    }

    public AssessmentBuilder withProcessRole(ProcessRole processRole) {
        return with(assessment -> assessment.setProcessRole(processRole));
    }

    public AssessmentBuilder withStartDate(LocalDate... startDates) {
        return withArray((startDate, object) -> setField("startDate", startDate, object), startDates);
    }

    public AssessmentBuilder withEndDate(LocalDate... endDates) {
        return withArray((endDate, object) -> setField("endDate", endDate, object), endDates);
    }

    public AssessmentBuilder withProcessOutcome(List<ProcessOutcome>... processOutcomes) {
        return withArray((processOutcome, object) -> setField("processOutcomes", processOutcome, object), processOutcomes);
    }

    public AssessmentBuilder withProcessStatus(ProcessStates... processStatuss) {
        return withArray((processStatus, object) -> setField("status", processStatus.getState(), object), processStatuss);
    }

    public AssessmentBuilder withProcessEvent(ProcessEvent... processEvents) {
        return withArray((processEvent, object) -> setField("event", processEvent.name(), object), processEvents);
    }

    public AssessmentBuilder withProcessRole(ProcessRole... processRoles) {
        return withArray((processRole, object) -> setField("processRole", processRole, object), processRoles);
    }

}
