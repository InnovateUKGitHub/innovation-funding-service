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
        return withArray((activityType, log) -> setField("activityType", activityType, log), activityTypes);
    }

    public ActivityLogResourceBuilder withAuthoredBy(Long... createdBys) {
        return withArray((createdBy, log) -> setField("authoredBy", createdBy, log), createdBys);
    }

    public ActivityLogResourceBuilder withAuthoredByName(String... createdByNames) {
        return withArray((createdByName, log) -> setField("authoredByName", createdByName, log), createdByNames);
    }

    @SafeVarargs
    public final ActivityLogResourceBuilder withAuthoredByRoles(Set<Role>... authoredByRolez) {
        return withArray((authoredByRoles, log) -> setField("authoredByRoles", authoredByRoles, log), authoredByRolez);
    }

    public ActivityLogResourceBuilder withCreatedOn(ZonedDateTime... createdOns) {
        return withArray((createdOn, log) -> setField("createdOn", createdOn, log), createdOns);
    }

    public ActivityLogResourceBuilder withOrganisation(Long... organisations) {
        return withArray((organisation, log) -> setField("organisation", organisation, log), organisations);
    }
    public ActivityLogResourceBuilder withOrganisationName(String... organisationNames) {
        return withArray((organisationName, log) -> setField("organisationName", organisationName, log), organisationNames);
    }
    public ActivityLogResourceBuilder withDocumentConfig(Long... documentConfigs) {
        return withArray((documentConfig, log) -> setField("documentConfig", documentConfig, log), documentConfigs);
    }
    public ActivityLogResourceBuilder withDocumentConfigName(String... documentConfigNames) {
        return withArray((documentConfigName, log) -> setField("documentConfigName", documentConfigName, log), documentConfigNames);
    }
    public ActivityLogResourceBuilder withQuery(Long... queries) {
        return withArray((query, log) -> setField("query", query, log), queries);
    }
    public ActivityLogResourceBuilder withQueryType(FinanceChecksSectionType... queryTypes) {
        return withArray((queryType, log) -> setField("queryType", queryType, log), queryTypes);
    }
}
