package com.worth.ifs.application.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.ApplicationStatus;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class ApplicationStatusBuilder extends BaseBuilder<ApplicationStatus, ApplicationStatusBuilder> {

    private ApplicationStatusBuilder(List<BiConsumer<Integer, ApplicationStatus>> multiActions) {
        super(multiActions);
    }

    public static ApplicationStatusBuilder newApplicationStatus() {
        return new ApplicationStatusBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ApplicationStatusBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationStatus>> actions) {
        return new ApplicationStatusBuilder(actions);
    }

    @Override
    protected ApplicationStatus createInitial() {
        return new ApplicationStatus();
    }

    public ApplicationStatusBuilder withName(ApplicationStatusConstants... names) {
        return withArray((name, applicationStatus) -> {applicationStatus.setName(name.getName()); applicationStatus.setId(name.getId());}, names);
    }

    public ApplicationStatusBuilder withName(String... names) {
        return withArray((name, applicationStatus) -> applicationStatus.setName(name), names);
    }
}
