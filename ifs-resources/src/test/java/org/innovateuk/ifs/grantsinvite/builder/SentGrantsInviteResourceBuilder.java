package org.innovateuk.ifs.grantsinvite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.grantsinvite.resource.GrantsInviteResource.GrantsInviteRole;
import org.innovateuk.ifs.grantsinvite.resource.SentGrantsInviteResource;
import org.innovateuk.ifs.invite.constant.InviteStatus;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class SentGrantsInviteResourceBuilder extends BaseBuilder<SentGrantsInviteResource, SentGrantsInviteResourceBuilder> {

    public static SentGrantsInviteResourceBuilder newSentGrantsInviteResource() {
        return new SentGrantsInviteResourceBuilder(emptyList());
    }

    public SentGrantsInviteResourceBuilder(List<BiConsumer<Integer, SentGrantsInviteResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected SentGrantsInviteResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, SentGrantsInviteResource>> actions) {
        return new SentGrantsInviteResourceBuilder(actions);
    }

    @Override
    protected SentGrantsInviteResource createInitial() {
        return new SentGrantsInviteResource();
    }

    public SentGrantsInviteResourceBuilder withUserName(String... userName) {
        return withArraySetFieldByReflection("userName", userName);
    }

    public SentGrantsInviteResourceBuilder withEmail(String... email) {
        return withArraySetFieldByReflection("email", email);
    }

    public SentGrantsInviteResourceBuilder withGrantsInviteRole(GrantsInviteRole... grantsInviteRole) {
        return withArraySetFieldByReflection("grantsInviteRole", grantsInviteRole);
    }

    public SentGrantsInviteResourceBuilder withStatus(InviteStatus... status) {
        return withArraySetFieldByReflection("status", status);
    }

}