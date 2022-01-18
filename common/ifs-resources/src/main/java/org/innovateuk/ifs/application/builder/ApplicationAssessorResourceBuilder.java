package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.user.resource.BusinessType;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ApplicationAssessorResourceBuilder extends BaseBuilder<ApplicationAssessorResource, ApplicationAssessorResourceBuilder> {

    private ApplicationAssessorResourceBuilder(List<BiConsumer<Integer, ApplicationAssessorResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected ApplicationAssessorResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationAssessorResource>> actions) {
        return new ApplicationAssessorResourceBuilder(actions);
    }

    @Override
    protected ApplicationAssessorResource createInitial() {
        return new ApplicationAssessorResource();
    }

    public static ApplicationAssessorResourceBuilder newApplicationAssessorResource() {
        return new ApplicationAssessorResourceBuilder(emptyList());
    }

    public ApplicationAssessorResourceBuilder withUserId(Long... value) {
        return withArraySetFieldByReflection("userId", value);
    }

    public ApplicationAssessorResourceBuilder withFirstName(String... value) {
        return withArraySetFieldByReflection("firstName", value);
    }

    public ApplicationAssessorResourceBuilder withLastName(String... value) {
        return withArraySetFieldByReflection("lastName", value);
    }

    public ApplicationAssessorResourceBuilder withBusinessType(BusinessType... value) {
        return withArraySetFieldByReflection("businessType", value);
    }

    public ApplicationAssessorResourceBuilder withInnovationAreas(Set<InnovationAreaResource>... value) {
        return withArraySetFieldByReflection("innovationAreas", value);
    }

    public ApplicationAssessorResourceBuilder withSkillAreas(String... value) {
        return withArraySetFieldByReflection("skillAreas", value);
    }

    public ApplicationAssessorResourceBuilder withRejectReason(AssessmentRejectOutcomeValue... value) {
        return withArraySetFieldByReflection("rejectReason", value);
    }

    public ApplicationAssessorResourceBuilder withRejectComment(String... value) {
        return withArraySetFieldByReflection("rejectComment", value);
    }

    public ApplicationAssessorResourceBuilder withAvailable(Boolean... value) {
        return withArraySetFieldByReflection("available", value);
    }

    public ApplicationAssessorResourceBuilder withMostRecentAssessmentId(Long... value) {
        return withArraySetFieldByReflection("mostRecentAssessmentId", value);
    }

    public ApplicationAssessorResourceBuilder withMostRecentAssessmentState(AssessmentState... value) {
        return withArraySetFieldByReflection("mostRecentAssessmentState", value);
    }

    public ApplicationAssessorResourceBuilder withTotalApplicationsCount(Long... value) {
        return withArraySetFieldByReflection("totalApplicationsCount", value);
    }

    public ApplicationAssessorResourceBuilder withAssignedCount(Long... value) {
        return withArraySetFieldByReflection("assignedCount", value);
    }

    public ApplicationAssessorResourceBuilder withSubmittedCount(Long... value) {
        return withArraySetFieldByReflection("submittedCount", value);
    }
}