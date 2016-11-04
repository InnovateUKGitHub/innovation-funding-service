package com.worth.ifs.assessment.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.workflow.domain.ActivityState;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import com.worth.ifs.workflow.resource.ProcessEvent;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
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

    public AssessmentBuilder withProcessEvent(ProcessEvent... processEvents) {
        return withArray((processEvent, object) -> setField("event", processEvent.name(), object), processEvents);
    }

    public AssessmentBuilder withLastModifiedDate(Calendar... lastModifiedDates) {
        return withArray((lastModifiedDate, object) -> setField("lastModified", lastModifiedDate, object), lastModifiedDates);
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

    public AssessmentBuilder withParticipant(ProcessRole... processRoles) {
        return withArray((processRole, object) -> setField("participant", processRole, object), processRoles);
    }

    public AssessmentBuilder withApplication(Application... application) {
        return withArray((app, object) -> setField("target", app, object), application);
    }

    public AssessmentBuilder withActivityState(ActivityState... activityState) {
        return withArray((state, object) -> object.setActivityState(state), activityState);
    }
}
