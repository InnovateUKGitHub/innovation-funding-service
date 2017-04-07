package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.Builder;
import org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeResource;
import org.innovateuk.ifs.assessment.resource.AssessmentFundingDecisionOutcomeResource;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.workflow.resource.ProcessEvent;

import java.time.LocalDate;
import java.time.ZonedDateTime;
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

    public AssessmentResourceBuilder withId(Long... value) {
        return withArraySetFieldByReflection("id", value);
    }

    public AssessmentResourceBuilder withProcessEvent(ProcessEvent... processEvents) {
        return withArray((processEvent, object) -> setField("event", processEvent.name(), object), processEvents);
    }

    public AssessmentResourceBuilder withLastModifiedDate(ZonedDateTime... value) {
        return withArraySetFieldByReflection("lastModified", value);
    }

    public AssessmentResourceBuilder withStartDate(LocalDate... value) {
        return withArraySetFieldByReflection("startDate", value);
    }

    public AssessmentResourceBuilder withEndDate(LocalDate... value) {
        return withArraySetFieldByReflection("endDate", value);
    }

    public AssessmentResourceBuilder withFundingDecision(AssessmentFundingDecisionOutcomeResource... value) {
        return withArraySetFieldByReflection("fundingDecision", value);
    }

    public AssessmentResourceBuilder withFundingDecision(Builder<AssessmentFundingDecisionOutcomeResource, ?> value) {
        return withFundingDecision(value.build());
    }

    public AssessmentResourceBuilder withRejection(AssessmentRejectOutcomeResource... value) {
        return withArraySetFieldByReflection("rejection", value);
    }

    public AssessmentResourceBuilder withRejection(Builder<AssessmentRejectOutcomeResource, ?> value) {
        return withRejection(value.build());
    }

    public AssessmentResourceBuilder withProcessRole(Long... value) {
        return withArraySetFieldByReflection("processRole", value);
    }

    public AssessmentResourceBuilder withApplication(Long... value) {
        return withArraySetFieldByReflection("application", value);
    }

    public AssessmentResourceBuilder withApplicationName(String... values) {
        return withArraySetFieldByReflection("applicationName", values);
    }

    public AssessmentResourceBuilder withCompetition(Long... value) {
        return withArraySetFieldByReflection("competition", value);
    }

    public AssessmentResourceBuilder withActivityState(AssessmentStates... value) {
        return withArraySetFieldByReflection("assessmentState", value);
    }
}
