package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ExistingUserStagedInviteResourceBuilder extends StagedInviteResourceBuilder<ExistingUserStagedInviteResource, ExistingUserStagedInviteResourceBuilder> {

    private ExistingUserStagedInviteResourceBuilder(List<BiConsumer<Integer, ExistingUserStagedInviteResource>> newActions) {
        super(newActions);
    }

    public static ExistingUserStagedInviteResourceBuilder newExistingUserStagedInviteResource() {
        return new ExistingUserStagedInviteResourceBuilder(emptyList());
    }

    @Override
    protected ExistingUserStagedInviteResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ExistingUserStagedInviteResource>> actions) {
        return new ExistingUserStagedInviteResourceBuilder(actions);
    }

    @Override
    protected ExistingUserStagedInviteResource createInitial() {
        return new ExistingUserStagedInviteResource();
    }
}
