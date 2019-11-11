package org.innovateuk.ifs.activitylog.domain;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.user.domain.User;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ActivityLogBuilder extends BaseBuilder<ActivityLog, ActivityLogBuilder> {

    public static ActivityLogBuilder newActivityLog() {
        return new ActivityLogBuilder(emptyList()).with(uniqueIds());
    }

    private ActivityLogBuilder(List<BiConsumer<Integer, ActivityLog>> multiActions) {
        super(multiActions);
    }

    @Override
    protected ActivityLogBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ActivityLog>> actions) {
        return new ActivityLogBuilder(actions);
    }

    public ActivityLogBuilder withOrganisation(Organisation... organisations) {
        return withArraySetFieldByReflection("organisation", organisations);
    }

    public ActivityLogBuilder withApplication(Application... applications) {
        return withArraySetFieldByReflection("application", applications);
    }

    public ActivityLogBuilder withCreatedBy(User... createdBys) {
        return withArraySetFieldByReflection("createdBy", createdBys);
    }

    @Override
    protected ActivityLog createInitial() {
        return new ActivityLog();
    }

}
