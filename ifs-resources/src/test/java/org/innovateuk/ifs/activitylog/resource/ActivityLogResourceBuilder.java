package org.innovateuk.ifs.activitylog.resource;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.threads.resource.FinanceChecksSectionType;
import org.innovateuk.ifs.user.resource.Role;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

public class ActivityLogResourceBuilder  extends BaseBuilder<ActivityLogResource, ActivityLogResourceBuilder> {

    private ActivityLogResourceBuilder(List<BiConsumer<Integer, ActivityLogResource>> multiActions) {
        super(multiActions);
    }

    public static ActivityLogResourceBuilder newActivityLogResource() {
        return new ActivityLogResourceBuilder(emptyList());
    }

    @Override
    protected ActivityLogResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ActivityLogResource>> actions) {
        return new ActivityLogResourceBuilder(actions);
    }

    @Override
    protected ActivityLogResource createInitial() {
        return new ActivityLogResource();
    }

    public ActivityLogResourceBuilder withActivityType(ActivityType... activityTypes) {
        return withArraySetFieldByReflection("activityType", activityTypes);
    }

    public ActivityLogResourceBuilder withAuthoredBy(Long... createdBys) {
        return withArraySetFieldByReflection("createdBy", createdBys);
    }

    public ActivityLogResourceBuilder withAuthoredByName(String... createdByNames) {
        return withArraySetFieldByReflection("authoredByName", createdByNames);
    }

    @SafeVarargs
    public final ActivityLogResourceBuilder withAuthoredByRoles(Set<Role>... authoredByRolez) {
        return withArraySetFieldByReflection("authoredByRoles", authoredByRolez);
    }

    public ActivityLogResourceBuilder withCreatedOn(ZonedDateTime... createdOns) {
        return withArraySetFieldByReflection("createdOn", createdOns);
    }

    public ActivityLogResourceBuilder withOrganisation(Long... organisations) {
        return withArraySetFieldByReflection("organisation", organisations);
    }
    public ActivityLogResourceBuilder withOrganisationName(String... organisationNames) {
        return withArraySetFieldByReflection("organisationName", organisationNames);
    }
    public ActivityLogResourceBuilder withDocumentConfig(Long... documentConfigs) {
        return withArraySetFieldByReflection("documentConfig", documentConfigs);
    }
    public ActivityLogResourceBuilder withDocumentConfigName(String... documentConfigNames) {
        return withArraySetFieldByReflection("documentConfigName", documentConfigNames);
    }
    public ActivityLogResourceBuilder withQuery(Long... queries) {
        return withArraySetFieldByReflection("query", queries);
    }
    public ActivityLogResourceBuilder withQueryType(FinanceChecksSectionType... queryTypes) {
        return withArraySetFieldByReflection("queryType", queryTypes);
    }
}
