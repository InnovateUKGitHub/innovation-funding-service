package org.innovateuk.ifs.applicant.builder;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.resource.SectionResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ApplicantSectionResourceBuilder extends AbstractApplicantResourceBuilder<ApplicantSectionResource, ApplicantSectionResourceBuilder> {

    public static ApplicantSectionResourceBuilder newApplicantSectionResource() {
        return new ApplicantSectionResourceBuilder(emptyList());
    }

    @Override
    protected ApplicantSectionResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicantSectionResource>> actions) {
        return new ApplicantSectionResourceBuilder(actions);
    }

    @Override
    protected ApplicantSectionResource createInitial() {
        return new ApplicantSectionResource();
    }

    private ApplicantSectionResourceBuilder(List<BiConsumer<Integer, ApplicantSectionResource>> newMultiActions) {
        super(newMultiActions);
    }

    public ApplicantSectionResourceBuilder withSection(SectionResource... section) {
        return withArraySetFieldByReflection("section", section);
    }

    public ApplicantSectionResourceBuilder withApplicantQuestions(List<ApplicantQuestionResource>... applicantQuestions) {
        return withArraySetFieldByReflection("applicantQuestions", applicantQuestions);
    }

    public ApplicantSectionResourceBuilder withApplicantParentSection(ApplicantSectionResource... applicantParentSection) {
        return withArraySetFieldByReflection("applicantParentSection", applicantParentSection);
    }

    public ApplicantSectionResourceBuilder withApplicantChildrenSections(List<ApplicantSectionResource>... applicantChildrenSections) {
        return withArraySetFieldByReflection("applicantChildrenSections", applicantChildrenSections);
    }

}
