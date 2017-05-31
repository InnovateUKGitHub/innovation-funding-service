package org.innovateuk.ifs.applicant.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.applicant.resource.ApplicantFormInputResource;
import org.innovateuk.ifs.applicant.resource.ApplicantFormInputResponseResource;
import org.innovateuk.ifs.form.resource.FormInputResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ApplicantFormInputResourceBuilder extends BaseBuilder<ApplicantFormInputResource, ApplicantFormInputResourceBuilder> {

    public static ApplicantFormInputResourceBuilder newApplicantFormInputResource() {
        return new ApplicantFormInputResourceBuilder(emptyList());
    }

    @Override
    protected ApplicantFormInputResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicantFormInputResource>> actions) {
        return new ApplicantFormInputResourceBuilder(actions);
    }

    @Override
    protected ApplicantFormInputResource createInitial() {
        return new ApplicantFormInputResource();
    }

    private ApplicantFormInputResourceBuilder(List<BiConsumer<Integer, ApplicantFormInputResource>> newMultiActions) {
        super(newMultiActions);
    }

    public ApplicantFormInputResourceBuilder withFormInput(FormInputResource... formInput) {
        return withArraySetFieldByReflection("formInput", formInput);
    }

    public ApplicantFormInputResourceBuilder withApplicantResponses(List<ApplicantFormInputResponseResource>... applicantResponses) {
        return withArraySetFieldByReflection("applicantResponses", applicantResponses);
    }
}
