package org.innovateuk.ifs.applicant.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.applicant.resource.ApplicantFormInputResponseResource;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ApplicantFormInputResponseResourceBuilder extends BaseBuilder<ApplicantFormInputResponseResource, ApplicantFormInputResponseResourceBuilder> {

    public static ApplicantFormInputResponseResourceBuilder newApplicantFormInputResponseResource() {
        return new ApplicantFormInputResponseResourceBuilder(emptyList());
    }

    @Override
    protected ApplicantFormInputResponseResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicantFormInputResponseResource>> actions) {
        return new ApplicantFormInputResponseResourceBuilder(actions);
    }

    @Override
    protected ApplicantFormInputResponseResource createInitial() {
        return new ApplicantFormInputResponseResource();
    }

    private ApplicantFormInputResponseResourceBuilder(List<BiConsumer<Integer, ApplicantFormInputResponseResource>> newMultiActions) {
        super(newMultiActions);
    }

    public ApplicantFormInputResponseResourceBuilder withResponse(FormInputResponseResource... response) {
        return withArraySetFieldByReflection("response", response);
    }

    public ApplicantFormInputResponseResourceBuilder withApplicant(ApplicantResource... applicant) {
        return withArraySetFieldByReflection("applicant", applicant);
    }
}
