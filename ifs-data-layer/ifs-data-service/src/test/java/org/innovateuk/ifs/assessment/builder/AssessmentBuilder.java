package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessmentDecisionOutcome;
import org.innovateuk.ifs.assessment.domain.AssessmentRejectOutcome;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.user.domain.ProcessRole;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class AssessmentBuilder extends BaseBuilder<Assessment, AssessmentBuilder> {

    private AssessmentBuilder(List<BiConsumer<Integer, Assessment>> multiActions) {
        super(multiActions);
    }

    public static AssessmentBuilder newAssessment() {
        return new AssessmentBuilder(emptyList()).with(uniqueIds());
    }

    public static AssessmentBuilder newAssessmentWithoutIds() {
        return new AssessmentBuilder(emptyList());
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

    public AssessmentBuilder withLastModifiedDate(ZonedDateTime... lastModifiedDates) {
        return withArray((lastModifiedDate, object) -> setField("lastModified", lastModifiedDate, object), lastModifiedDates);
    }

    public AssessmentBuilder withStartDate(LocalDate... startDates) {
        return withArray((startDate, object) -> setField("startDate", startDate, object), startDates);
    }

    public AssessmentBuilder withEndDate(LocalDate... endDates) {
        return withArray((endDate, object) -> setField("endDate", endDate, object), endDates);
    }

    public AssessmentBuilder withParticipant(ProcessRole... processRoles) {
        return withArray((processRole, object) -> setField("participant", processRole, object), processRoles);
    }

    public AssessmentBuilder withApplication(Application... application) {
        return withArray((app, object) -> setField("target", app, object), application);
    }

    public AssessmentBuilder withProcessState(AssessmentState... activityState) {
        return withArray((state, object) -> object.setProcessState(state), activityState);
    }

    public AssessmentBuilder withDecision(AssessmentDecisionOutcome... decision) {
        return withArraySetFieldByReflection("decision", decision);
    }

    public AssessmentBuilder withRejection(AssessmentRejectOutcome... rejection) {
        return withArraySetFieldByReflection("rejection", rejection);
    }
}
