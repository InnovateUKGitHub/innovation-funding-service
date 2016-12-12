package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.invite.resource.StagedInviteResource;

import java.util.List;
import java.util.function.BiConsumer;

public abstract class StagedInviteResourceBuilder<T extends StagedInviteResource, B extends StagedInviteResourceBuilder> extends BaseBuilder<T, B> {
    protected StagedInviteResourceBuilder(List<BiConsumer<Integer, T>> newActions) {
        super(newActions);
    }

    public B withEmail(String... emails) {
        return withArraySetFieldByReflection("email", emails);
    }

    public B withCompetitionId(Long... competitionIds) {
        return withArraySetFieldByReflection("competitionId", competitionIds);
    }
}
