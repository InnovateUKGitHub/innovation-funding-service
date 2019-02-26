package org.innovateuk.ifs.eugrant.domain;

import org.innovateuk.ifs.BaseBuilder;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class EuGrantBuilder extends BaseBuilder<EuGrant, EuGrantBuilder> {

    private EuGrantBuilder(List<BiConsumer<Integer, EuGrant>> multiActions) {
        super(multiActions);
    }

    public static EuGrantBuilder newEuGrant() {
        return new EuGrantBuilder(emptyList());
    }

    @Override
    protected EuGrantBuilder createNewBuilderWithActions(List<BiConsumer<Integer, EuGrant>> actions) {
        return new EuGrantBuilder(actions);
    }

    @Override
    protected EuGrant createInitial() {
        return new EuGrant();
    }

    public EuGrantBuilder withId(UUID... ids) {
        return withArray((id, grant) -> grant.setId(id), ids);
    }

    public EuGrantBuilder withOrganisation(EuOrganisation... organisations) {
        return withArray((organisation, grant) -> grant.setOrganisation(organisation), organisations);
    }

    public EuGrantBuilder withContact(EuContact... contacts) {
        return withArray((contact, grant) -> grant.setContact(contact), contacts);
    }

    public EuGrantBuilder withFunding(EuFunding... fundings) {
        return withArray((funding, grant) -> grant.setFunding(funding), fundings);
    }

    public EuGrantBuilder withSubmitted(Boolean... submittedStatuses) {
        return withArray((submittedStatus, grant) -> grant.setSubmitted(submittedStatus), submittedStatuses);
    }

}
