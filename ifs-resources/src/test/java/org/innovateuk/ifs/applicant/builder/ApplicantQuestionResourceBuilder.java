package org.innovateuk.ifs.applicant.builder;

import org.innovateuk.ifs.applicant.resource.ApplicantFormInputResource;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionStatusResource;
import org.innovateuk.ifs.application.resource.QuestionResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ApplicantQuestionResourceBuilder extends AbstractApplicantResourceBuilder<ApplicantQuestionResource, ApplicantQuestionResourceBuilder> {

    public static ApplicantQuestionResourceBuilder newApplicantQuestionResource() {
        return new ApplicantQuestionResourceBuilder(emptyList());
    }

    @Override
    protected ApplicantQuestionResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicantQuestionResource>> actions) {
        return new ApplicantQuestionResourceBuilder(actions);
    }

    @Override
    protected ApplicantQuestionResource createInitial() {
        return new ApplicantQuestionResource();
    }

    private ApplicantQuestionResourceBuilder(List<BiConsumer<Integer, ApplicantQuestionResource>> newMultiActions) {
        super(newMultiActions);
    }

    public ApplicantQuestionResourceBuilder withQuestion(QuestionResource... question) {
        return withArraySetFieldByReflection("question", question);
    }

    public ApplicantQuestionResourceBuilder withApplicantFormInputs(List<ApplicantFormInputResource>... applicantFormInputs) {
        return withArraySetFieldByReflection("applicantFormInputs", applicantFormInputs);
    }

    public ApplicantQuestionResourceBuilder withApplicantQuestionStatuses(List<ApplicantQuestionStatusResource>... applicantQuestionStatuses) {
        return withArraySetFieldByReflection("applicantQuestionStatuses", applicantQuestionStatuses);
    }
}
