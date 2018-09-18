package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.resource.*;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import org.innovateuk.ifs.application.resource.ApplicationState;

public class PreviousApplicationResourceBuilder extends BaseBuilder<PreviousApplicationResource, PreviousApplicationResourceBuilder> {

    private PreviousApplicationResourceBuilder(List<BiConsumer<Integer, PreviousApplicationResource>> multiActions) {
        super(multiActions);
    }

    public static PreviousApplicationResourceBuilder newPreviousApplicationResource() {
        return new PreviousApplicationResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected PreviousApplicationResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, PreviousApplicationResource>> actions) {
        return new PreviousApplicationResourceBuilder(actions);
    }

    @Override
    protected PreviousApplicationResource createInitial() {
        return new PreviousApplicationResource();
    }

    public PreviousApplicationResourceBuilder withId(Long... ids) {
        return withArray((id, application) -> setField("id", id, application), ids);
    }

    public PreviousApplicationResourceBuilder withCompetition(Long... competitionIds) {
        return withArray((competition, application) -> setField("competition", competition, application), competitionIds);
    }

    public PreviousApplicationResourceBuilder withApplicationState(ApplicationState... applicationStates) {
        return withArray((applicationState, application) -> application.setApplicationState(applicationState), applicationStates);
    }

    public PreviousApplicationResourceBuilder withName(String... names) {
        return withArray((name, application) -> setField("name", name, application), names);
    }

    public PreviousApplicationResourceBuilder withLeadOrganisationName(String... leadOrganisationNames) {
        return withArray((leadOrganisationName, application) -> application.setLeadOrganisationName(leadOrganisationName), leadOrganisationNames);
    }

}
