package com.worth.ifs.invite.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.invite.resource.ApplicationInviteResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class InviteResourceBuilder extends BaseBuilder<ApplicationInviteResource, InviteResourceBuilder> {

    private InviteResourceBuilder(List<BiConsumer<Integer, ApplicationInviteResource>> multiActions) {
        super(multiActions);
    }

    public static InviteResourceBuilder newInviteResource() {
        return new InviteResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected InviteResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationInviteResource>> actions) {
        return new InviteResourceBuilder(actions);
    }

    public InviteResourceBuilder withId(Long... ids) {
        return withArray((id, inviteResource) -> setField("id", id, inviteResource), ids);
    }

    public InviteResourceBuilder withEmail(final String... emailAddresses) {
        return withArray((email, inviteResource) -> setField("email", email, inviteResource), emailAddresses);
    }

    public InviteResourceBuilder withName(final String... names) {
        return withArray((name, inviteResource) -> setField("name", name, inviteResource), names);
    }

    public InviteResourceBuilder withApplication(final Long... applicationIds) {
        return withArray((applicationId, inviteResource) -> setField("application", applicationId, inviteResource), applicationIds);
    }

    public InviteResourceBuilder withOrganisation(final Long... organisationIds) {
        return withArray((organisationId, inviteResource) -> setField("organisation", organisationId, inviteResource), organisationIds);
    }

    @Override
    protected ApplicationInviteResource createInitial() {
        return new ApplicationInviteResource();
    }
}
