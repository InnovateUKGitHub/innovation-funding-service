package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ApplicationCountSummaryResourceBuilder extends BaseBuilder<ApplicationCountSummaryResource, ApplicationCountSummaryResourceBuilder> {

    private ApplicationCountSummaryResourceBuilder(List<BiConsumer<Integer, ApplicationCountSummaryResource>> multiActions) {
        super(multiActions);
    }

    public static ApplicationCountSummaryResourceBuilder newApplicationCountSummaryResource() {
        return new ApplicationCountSummaryResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ApplicationCountSummaryResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationCountSummaryResource>> actions) {
        return new ApplicationCountSummaryResourceBuilder(actions);
    }

    @Override
    protected ApplicationCountSummaryResource createInitial() {
        return new ApplicationCountSummaryResource();
    }

    public ApplicationCountSummaryResourceBuilder withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public ApplicationCountSummaryResourceBuilder withName(String... names) {
        return withArraySetFieldByReflection("name", names);
    }

    public ApplicationCountSummaryResourceBuilder withLeadOrganisation(String... leadOrganisations) {
        return withArraySetFieldByReflection("leadOrganisation", leadOrganisations);
    }

    public ApplicationCountSummaryResourceBuilder withAssessors(Long... assessorss) {
        return withArraySetFieldByReflection("assessors", assessorss);
    }

    public ApplicationCountSummaryResourceBuilder withAccepted(Long... accepteds) {
        return withArraySetFieldByReflection("accepted", accepteds);
    }

    public ApplicationCountSummaryResourceBuilder withSubmitted(Long... submitteds) {
        return withArraySetFieldByReflection("submitted", submitteds);
    }

}
