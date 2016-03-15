package com.worth.ifs.application.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.user.domain.ProcessRole;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

public class ApplicationBuilder extends BaseBuilder<Application, ApplicationBuilder> {

    private ApplicationBuilder(List<BiConsumer<Integer, Application>> multiActions) {
        super(multiActions);
    }

    public static ApplicationBuilder newApplication() {
        return new ApplicationBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ApplicationBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Application>> actions) {
        return new ApplicationBuilder(actions);
    }

    @Override
    protected Application createInitial() {
        return new Application();
    }

    public ApplicationBuilder withId(Long... ids) {
        return withArray((id, application) -> setField("id", id, application), ids);
    }

    public ApplicationBuilder withCompetition(Competition competition) {
        return with(application -> application.setCompetition(competition));
    }

    public ApplicationBuilder withApplicationStatus(ApplicationStatus... applicationStatus) {
        return withArray((applicationState, application) -> application.setApplicationStatus(applicationState), applicationStatus);
    }

    public ApplicationBuilder withStartDate(LocalDate... dates) {
        return withArray((date, application) -> application.setStartDate(date), dates);
    }

    public ApplicationBuilder withProcessRoles(ProcessRole... processRoles) {
        return with(application -> application.setProcessRoles(asList(processRoles)));
    }

    public ApplicationBuilder withName(String name) {
        return with(application -> application.setName(name));
    }
}
