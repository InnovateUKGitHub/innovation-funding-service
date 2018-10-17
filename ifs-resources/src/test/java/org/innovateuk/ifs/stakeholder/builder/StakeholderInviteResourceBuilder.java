package org.innovateuk.ifs.stakeholder.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.invite.resource.StakeholderInviteResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

public class StakeholderInviteResourceBuilder extends BaseBuilder<StakeholderInviteResource, StakeholderInviteResourceBuilder> {

    private StakeholderInviteResourceBuilder(final List<BiConsumer<Integer, StakeholderInviteResource>> newActions) {
        super(newActions);
    }

    @Override
    protected StakeholderInviteResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, StakeholderInviteResource>> actions) {
        return new StakeholderInviteResourceBuilder(actions);
    }

    protected StakeholderInviteResource createInitial() {
        return new StakeholderInviteResource();
    }

    public static StakeholderInviteResourceBuilder newStakeholderInviteResource() {
        return new StakeholderInviteResourceBuilder(emptyList());
    }

    public StakeholderInviteResourceBuilder withId(Long... ids) {
        return withArray((id, stakeholderInviteResource) -> setField("id", id, stakeholderInviteResource), ids);
    }

    public StakeholderInviteResourceBuilder withHash(String... hashes) {
        return withArray((hash, stakeholderInviteResource) -> setField("hash", hash, stakeholderInviteResource), hashes);
    }

    public StakeholderInviteResourceBuilder withCompetition(Long... competitionIds) {
        return withArray((competitionId, stakeholderInviteResource) -> setField("competitionId", competitionId, stakeholderInviteResource), competitionIds);
    }

    public StakeholderInviteResourceBuilder withEmail(String... emails) {
        return withArray((email, stakeholderInviteResource) -> setField("email", email, stakeholderInviteResource), emails);
    }
}
