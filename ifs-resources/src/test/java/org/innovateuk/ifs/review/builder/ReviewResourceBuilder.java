package org.innovateuk.ifs.review.builder;

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

public class ReviewResourceBuilder extends BaseBuilder<ReviewResource, ReviewResourceBuilder> {

    private ReviewResourceBuilder(List<BiConsumer<Integer, ReviewResource>> multiActions) {
        super(multiActions);
    }

    public static ReviewResourceBuilder newReviewResource() {
        return new ReviewResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ReviewResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ReviewResource>> actions) {
        return new ReviewResourceBuilder(actions);
    }

    @Override
    protected ReviewResource createInitial() {
        return new ReviewResource();
    }

    public ReviewResourceBuilder withId(Long... value) {
        return withArraySetFieldByReflection("id", value);
    }

    public ReviewResourceBuilder withStartDate(LocalDate... value) {
        return withArraySetFieldByReflection("startDate", value);
    }

    public ReviewResourceBuilder withEndDate(LocalDate... value) {
        return withArraySetFieldByReflection("endDate", value);
    }

    public ReviewResourceBuilder withFundingDecision(AssessmentFundingDecisionOutcomeResource... value) {
        return withArraySetFieldByReflection("fundingDecision", value);
    }

    public ReviewResourceBuilder withRejection(ReviewRejectOutcomeResource... value) {
        return withArraySetFieldByReflection("rejection", value);
    }

    public ReviewResourceBuilder withRejection(Builder<ReviewRejectOutcomeResource, ?> value) {
        return withRejection(value.build());
    }

    public ReviewResourceBuilder withProcessRole(Long... value) {
        return withArraySetFieldByReflection("processRole", value);
    }

    public ReviewResourceBuilder withApplication(Long... value) {
        return withArraySetFieldByReflection("application", value);
    }

    public ReviewResourceBuilder withApplicationName(String... values) {
        return withArraySetFieldByReflection("applicationName", values);
    }

    public ReviewResourceBuilder withCompetition(Long... value) {
        return withArraySetFieldByReflection("competition", value);
    }

    public ReviewResourceBuilder withActivityState(ReviewState... value) {
        return withArraySetFieldByReflection("reviewState", value);
    }
}
