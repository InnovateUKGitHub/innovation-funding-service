package com.worth.ifs.invite.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.invite.domain.Invite;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class InviteBuilder extends BaseBuilder<Invite, InviteBuilder> {

    private InviteBuilder(List<BiConsumer<Integer, Invite>> multiActions) {
        super(multiActions);
    }

    public static InviteBuilder newInvite() {
        return new InviteBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected InviteBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Invite>> actions) {
        return new InviteBuilder(actions);
    }

    @Override
    protected Invite createInitial() {
        return new Invite();
    }
}
