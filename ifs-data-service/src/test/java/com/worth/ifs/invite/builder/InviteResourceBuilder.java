package com.worth.ifs.invite.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.invite.domain.Invite;
import com.worth.ifs.invite.resource.InviteResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class InviteResourceBuilder extends BaseBuilder<InviteResource, InviteResourceBuilder> {

    private InviteResourceBuilder(List<BiConsumer<Integer, InviteResource>> multiActions) {
        super(multiActions);
    }

    public static InviteResourceBuilder newInviteResource() {
        return new InviteResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected InviteResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InviteResource>> actions) {
        return new InviteResourceBuilder(actions);
    }

    @Override
    protected InviteResource createInitial() {
        return new InviteResource();
    }
}
