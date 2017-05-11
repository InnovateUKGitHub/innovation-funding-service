package org.innovateuk.ifs.applicant.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionStatusResource;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ApplicantQuestionStatusResourceBuilder extends BaseBuilder<ApplicantQuestionStatusResource, ApplicantQuestionStatusResourceBuilder> {

    public static ApplicantQuestionStatusResourceBuilder newApplicantQuestionStatusResource() {
        return new ApplicantQuestionStatusResourceBuilder(emptyList());
    }

    @Override
    protected ApplicantQuestionStatusResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicantQuestionStatusResource>> actions) {
        return new ApplicantQuestionStatusResourceBuilder(actions);
    }

    @Override
    protected ApplicantQuestionStatusResource createInitial() {
        return new ApplicantQuestionStatusResource();
    }

    private ApplicantQuestionStatusResourceBuilder(List<BiConsumer<Integer, ApplicantQuestionStatusResource>> newMultiActions) {
        super(newMultiActions);
    }

    public ApplicantQuestionStatusResourceBuilder withStatus(QuestionStatusResource... status) {
        return withArraySetFieldByReflection("status", status);
    }

    public ApplicantQuestionStatusResourceBuilder withMarkedAsCompleteBy(ApplicantResource... markedAsCompleteBy) {
        return withArraySetFieldByReflection("markedAsCompleteBy", markedAsCompleteBy);
    }


    public ApplicantQuestionStatusResourceBuilder withAssignee(ApplicantResource... assignee) {
        return withArraySetFieldByReflection("assignee", assignee);
    }


    public ApplicantQuestionStatusResourceBuilder withAssignedBy(ApplicantResource... assignedBy) {
        return withArraySetFieldByReflection("assignedBy", assignedBy);
    }

}
