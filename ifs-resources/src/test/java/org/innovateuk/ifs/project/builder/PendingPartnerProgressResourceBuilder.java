package org.innovateuk.ifs.project.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class PendingPartnerProgressResourceBuilder extends BaseBuilder<PendingPartnerProgressResource, PendingPartnerProgressResourceBuilder> {

    private PendingPartnerProgressResourceBuilder(List<BiConsumer<Integer, PendingPartnerProgressResource>> multiActions) {
        super(multiActions);
    }

    public static PendingPartnerProgressResourceBuilder newPendingPartnerProgressResource() {
        return new PendingPartnerProgressResourceBuilder(emptyList());
    }

    @Override
    protected PendingPartnerProgressResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, PendingPartnerProgressResource>> actions) {
        return new PendingPartnerProgressResourceBuilder(actions);
    }

    @Override
    protected PendingPartnerProgressResource createInitial() {
        return new PendingPartnerProgressResource();
    }

    public PendingPartnerProgressResourceBuilder withYourOrganisationCompletedOn(ZonedDateTime... yourOrganisationCompletedOn){
        return withArray((n, progress) -> progress.setYourOrganisationCompletedOn(n), yourOrganisationCompletedOn);
    }

    public PendingPartnerProgressResourceBuilder withYourFundingCompletedOn(ZonedDateTime... yourFundingCompletedOn){
        return withArray((n, progress) -> progress.setYourFundingCompletedOn(n), yourFundingCompletedOn);
    }

    public PendingPartnerProgressResourceBuilder withTermsAndConditionsCompletedOn(ZonedDateTime... termsAndConditionsCompletedOn){
        return withArray((n, progress) -> progress.setTermsAndConditionsCompletedOn(n), termsAndConditionsCompletedOn);
    }

}
