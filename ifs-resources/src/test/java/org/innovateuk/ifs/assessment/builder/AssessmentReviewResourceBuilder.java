package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.Builder;
import org.innovateuk.ifs.assessment.resource.AssessmentFundingDecisionOutcomeResource;
import org.innovateuk.ifs.review.resource.ReviewRejectOutcomeResource;
import org.innovateuk.ifs.review.resource.ReviewResource;
import org.innovateuk.ifs.review.resource.ReviewState;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class AssessmentReviewResourceBuilder extends BaseBuilder<ReviewResource, AssessmentReviewResourceBuilder> {

    private AssessmentReviewResourceBuilder(List<BiConsumer<Integer, ReviewResource>> multiActions) {
        super(multiActions);
    }

    public static AssessmentReviewResourceBuilder newAssessmentReviewResource() {
        return new AssessmentReviewResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected AssessmentReviewResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ReviewResource>> actions) {
        return new AssessmentReviewResourceBuilder(actions);
    }

    @Override
    protected ReviewResource createInitial() {
        return new ReviewResource();
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

    public AssessmentReviewResourceBuilder withRejection(ReviewRejectOutcomeResource... value) {
        return withArraySetFieldByReflection("rejection", value);
    }

    public AssessmentReviewResourceBuilder withRejection(Builder<ReviewRejectOutcomeResource, ?> value) {
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

    public AssessmentReviewResourceBuilder withActivityState(ReviewState... value) {
        return withArraySetFieldByReflection("assessmentReviewState", value);
    }
}
