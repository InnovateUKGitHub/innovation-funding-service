package com.worth.ifs.application.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.ApplicationStatusResource;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.user.domain.ProcessRole;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

public class ApplicationResourceBuilder extends BaseBuilder<ApplicationResource, ApplicationResourceBuilder> {

    private ApplicationResourceBuilder(List<BiConsumer<Integer, ApplicationResource>> multiActions) {
        super(multiActions);
    }

    public static ApplicationResourceBuilder newApplicationResource() {
        return new ApplicationResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ApplicationResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationResource>> actions) {
        return new ApplicationResourceBuilder(actions);
    }

    @Override
    protected ApplicationResource createInitial() {
        return new ApplicationResource();
    }

    public ApplicationResourceBuilder withId(Long... ids) {
        return withArray((id, application) -> setField("id", id, application), ids);
    }

    public ApplicationResourceBuilder withCompetition(Competition competition) {
        return with(application -> application.setCompetition(competition.getId()));
    }

    public ApplicationResourceBuilder withApplicationStatus(ApplicationStatusResource... applicationStatus) {
        return withArray((applicationState, application) -> application.setApplicationStatus(applicationState.getId()), applicationStatus);
    }

    public ApplicationResourceBuilder withStartDate(LocalDate... dates) {
        return withArray((date, application) -> application.setStartDate(date), dates);
    }

    public ApplicationResourceBuilder withProcessRoles(ProcessRole... processRoles) {
        return with(application -> application.setProcessRoles(simpleMap(asList(processRoles),ProcessRole::getId)));
    }

    public ApplicationResourceBuilder withName(String name) {
        return with(application -> application.setName(name));
    }
}
