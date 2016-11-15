package com.worth.ifs.invite.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.ApplicationInviteResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class InviteOrganisationResourceBuilder extends BaseBuilder<InviteOrganisationResource, InviteOrganisationResourceBuilder> {

    private InviteOrganisationResourceBuilder(List<BiConsumer<Integer, InviteOrganisationResource>> multiActions) {
        super(multiActions);
    }

    public static InviteOrganisationResourceBuilder newInviteOrganisationResource() {
        return new InviteOrganisationResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected InviteOrganisationResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InviteOrganisationResource>> actions) {
        return new InviteOrganisationResourceBuilder(actions);
    }

    public InviteOrganisationResourceBuilder withId(Long... ids) {
        return withArray((id, inviteResource) -> setField("id", id, inviteResource), ids);
    }

    public InviteOrganisationResourceBuilder withOrganisationName(final String... organisationNames) {
        return withArray((organisationName, inviteResource) -> setField("organisationName", organisationName, inviteResource), organisationNames);
    }

    public InviteOrganisationResourceBuilder withInviteResources(List<ApplicationInviteResource> inviteResource) {
        return with(inviteOrganisationResource -> inviteOrganisationResource.setInviteResources(inviteResource));
    }

    public InviteOrganisationResourceBuilder withOrganisation(final Long... organisationIds) {
        return withArray((organisationId, inviteResource) -> setField("organisation", organisationId, inviteResource), organisationIds);
    }

    @Override
    protected InviteOrganisationResource createInitial() {
        return new InviteOrganisationResource();
    }
}
