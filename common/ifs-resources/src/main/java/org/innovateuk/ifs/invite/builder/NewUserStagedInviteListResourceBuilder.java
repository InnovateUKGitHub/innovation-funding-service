package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.invite.resource.NewUserStagedInviteListResource;
import org.innovateuk.ifs.invite.resource.NewUserStagedInviteResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class NewUserStagedInviteListResourceBuilder extends BaseBuilder<NewUserStagedInviteListResource, NewUserStagedInviteListResourceBuilder> {

    public static NewUserStagedInviteListResourceBuilder newNewUserStagedInviteListResource() {
        return new NewUserStagedInviteListResourceBuilder(emptyList());
    }

    private NewUserStagedInviteListResourceBuilder(List<BiConsumer<Integer, NewUserStagedInviteListResource>> newActions) {
        super(newActions);
    }

    @Override
    protected NewUserStagedInviteListResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, NewUserStagedInviteListResource>> actions) {
        return new NewUserStagedInviteListResourceBuilder(actions);
    }

    @Override
    protected NewUserStagedInviteListResource createInitial() {
        return new NewUserStagedInviteListResource();
    }

    public NewUserStagedInviteListResourceBuilder withInvites(List<NewUserStagedInviteResource>... invites) {
        return withArraySetFieldByReflection("invites", invites);
    }
}
