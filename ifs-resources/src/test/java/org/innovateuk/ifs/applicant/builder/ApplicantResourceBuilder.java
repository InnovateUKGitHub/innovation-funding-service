package org.innovateuk.ifs.applicant.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ApplicantResourceBuilder extends BaseBuilder<ApplicantResource, ApplicantResourceBuilder> {

    public static ApplicantResourceBuilder newApplicantResource() {
        return new ApplicantResourceBuilder(emptyList());
    }

    @Override
    protected ApplicantResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicantResource>> actions) {
        return new ApplicantResourceBuilder(actions);
    }

    @Override
    protected ApplicantResource createInitial() {
        return new ApplicantResource();
    }

    private ApplicantResourceBuilder(List<BiConsumer<Integer, ApplicantResource>> newMultiActions) {
        super(newMultiActions);
    }

    public ApplicantResourceBuilder withProcessRole(ProcessRoleResource... processRole) {
        return withArraySetFieldByReflection("processRole", processRole);
    }

    public ApplicantResourceBuilder withOrganisation(OrganisationResource... organisation) {
        return withArraySetFieldByReflection("organisation", organisation);
    }

}
