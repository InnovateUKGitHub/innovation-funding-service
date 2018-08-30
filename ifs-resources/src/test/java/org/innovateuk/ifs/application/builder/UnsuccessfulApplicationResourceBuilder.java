package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.resource.*;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import org.innovateuk.ifs.application.resource.ApplicationState;

public class UnsuccessfulApplicationResourceBuilder extends BaseBuilder<UnsuccessfulApplicationResource, UnsuccessfulApplicationResourceBuilder> {

    private UnsuccessfulApplicationResourceBuilder(List<BiConsumer<Integer, UnsuccessfulApplicationResource>> multiActions) {
        super(multiActions);
    }

    public static UnsuccessfulApplicationResourceBuilder newUnsuccessfulApplicationResource() {
        return new UnsuccessfulApplicationResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected UnsuccessfulApplicationResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, UnsuccessfulApplicationResource>> actions) {
        return new UnsuccessfulApplicationResourceBuilder(actions);
    }

    @Override
    protected UnsuccessfulApplicationResource createInitial() {
        return new UnsuccessfulApplicationResource();
    }

    public UnsuccessfulApplicationResourceBuilder withId(Long... ids) {
        return withArray((id, application) -> setField("id", id, application), ids);
    }

    public UnsuccessfulApplicationResourceBuilder withCompetition(Long... competitionIds) {
        return withArray((competition, application) -> setField("competition", competition, application), competitionIds);
    }

    public UnsuccessfulApplicationResourceBuilder withApplicationState(ApplicationState... applicationStates) {
        return withArray((applicationState, application) -> application.setApplicationState(applicationState), applicationStates);
    }

    public UnsuccessfulApplicationResourceBuilder withName(String... names) {
        return withArray((name, application) -> setField("name", name, application), names);
    }

    public UnsuccessfulApplicationResourceBuilder withLeadOrganisationName(String... leadOrganisationNames) {
        return withArray((leadOrganisationName, application) -> application.setLeadOrganisationName(leadOrganisationName), leadOrganisationNames);
    }

}
