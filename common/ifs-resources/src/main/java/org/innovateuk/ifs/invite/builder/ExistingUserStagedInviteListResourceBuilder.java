package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteListResource;
import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ExistingUserStagedInviteListResourceBuilder extends BaseBuilder<ExistingUserStagedInviteListResource, ExistingUserStagedInviteListResourceBuilder> {

    public static ExistingUserStagedInviteListResourceBuilder newExistingUserStagedInviteListResource() {
        return new ExistingUserStagedInviteListResourceBuilder(emptyList());
    }

    private ExistingUserStagedInviteListResourceBuilder(List<BiConsumer<Integer, ExistingUserStagedInviteListResource>> newActions) {
        super(newActions);
    }

    @Override
    protected ExistingUserStagedInviteListResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ExistingUserStagedInviteListResource>> actions) {
        return new ExistingUserStagedInviteListResourceBuilder(actions);
    }

    @Override
    protected ExistingUserStagedInviteListResource createInitial() {
        return new ExistingUserStagedInviteListResource();
    }

    public ExistingUserStagedInviteListResourceBuilder withInvites(List<ExistingUserStagedInviteResource>... invites) {
        return withArraySetFieldByReflection("invites", invites);
    }
}