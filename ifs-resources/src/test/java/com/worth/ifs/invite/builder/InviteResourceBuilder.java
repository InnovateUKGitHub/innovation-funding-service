package com.worth.ifs.invite.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.invite.resource.ApplicationInviteResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
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

    public InviteResourceBuilder withHash(final String... hashes) {
        return withArray((hash, inviteResource) -> setField("hash", hash, inviteResource), hashes);
    }

    public InviteResourceBuilder withApplication(final Long... applicationIds) {
        return withArray((applicationId, inviteResource) -> setField("application", applicationId, inviteResource), applicationIds);
    }

    public InviteResourceBuilder withLeadOrganisation(final String... leadOrganisations) {
        return withArray((leadOrganisation, inviteResource) -> setField("leadOrganisation", leadOrganisation, inviteResource), leadOrganisations);
    }

    public InviteResourceBuilder withInviteOrganisationName(final String... inviteOrganisationNames) {
        return withArray((inviteOrganisationName, inviteResource) -> setField("inviteOrganisationName", inviteOrganisationName, inviteResource), inviteOrganisationNames);
    }

    public InviteResourceBuilder withOrganisation(final Long... organisationIds) {
        return withArray((organisationId, inviteResource) -> setField("organisation", organisationId, inviteResource), organisationIds);
    }

    @Override
    protected ApplicationInviteResource createInitial() {
        return new ApplicationInviteResource();
    }
}
