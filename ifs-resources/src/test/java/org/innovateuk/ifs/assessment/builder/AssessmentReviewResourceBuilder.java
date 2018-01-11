package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.Builder;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewRejectOutcomeResource;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewResource;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewState;
import org.innovateuk.ifs.assessment.resource.AssessmentFundingDecisionOutcomeResource;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class AssessmentReviewResourceBuilder extends BaseBuilder<AssessmentReviewResource, AssessmentReviewResourceBuilder> {

    private AssessmentReviewResourceBuilder(List<BiConsumer<Integer, AssessmentReviewResource>> multiActions) {
        super(multiActions);
    }

    public static AssessmentReviewResourceBuilder newAssessmentReviewResource() {
        return new AssessmentReviewResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected AssessmentReviewResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentReviewResource>> actions) {
        return new AssessmentReviewResourceBuilder(actions);
    }

    @Override
    protected AssessmentReviewResource createInitial() {
        return new AssessmentReviewResource();
    }

    public AssessmentReviewResourceBuilder withId(Long... value) {
        return withArraySetFieldByReflection("id", value);
    }

    public AssessmentReviewResourceBuilder withStartDate(LocalDate... value) {
        return withArraySetFieldByReflection("startDate", value);
    }

    public AssessmentReviewResourceBuilder withEndDate(LocalDate... value) {
        return withArraySetFieldByReflection("endDate", value);
    }

    public AssessmentReviewResourceBuilder withFundingDecision(AssessmentFundingDecisionOutcomeResource... value) {
        return withArraySetFieldByReflection("fundingDecision", value);
    }

    public AssessmentReviewResourceBuilder withRejection(AssessmentReviewRejectOutcomeResource... value) {
        return withArraySetFieldByReflection("rejection", value);
    }

    public AssessmentReviewResourceBuilder withRejection(Builder<AssessmentReviewRejectOutcomeResource, ?> value) {
        return withRejection(value.build());
    }

    public AssessmentReviewResourceBuilder withProcessRole(Long... value) {
        return withArraySetFieldByReflection("processRole", value);
    }

    public AssessmentReviewResourceBuilder withApplication(Long... value) {
        return withArraySetFieldByReflection("application", value);
    }

    public AssessmentReviewResourceBuilder withApplicationName(String... values) {
        return withArraySetFieldByReflection("applicationName", values);
    }

    public AssessmentReviewResourceBuilder withCompetition(Long... value) {
        return withArraySetFieldByReflection("competition", value);
    }

    public AssessmentReviewResourceBuilder withActivityState(AssessmentReviewState... value) {
        return withArraySetFieldByReflection("assessmentReviewState", value);
    }
}
