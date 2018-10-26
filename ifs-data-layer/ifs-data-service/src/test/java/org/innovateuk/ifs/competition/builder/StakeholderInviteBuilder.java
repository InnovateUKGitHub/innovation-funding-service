package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.domain.StakeholderInvite;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.createDefault;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

/**
 * Builder for {@link StakeholderInvite}s.
 */
public class StakeholderInviteBuilder extends BaseBuilder<StakeholderInvite, StakeholderInviteBuilder> {

    public static StakeholderInviteBuilder newStakeholderInvite() {
        return new StakeholderInviteBuilder(emptyList()).with(uniqueIds());
    }

    private StakeholderInviteBuilder(List<BiConsumer<Integer, StakeholderInvite>> multiActions) {
        super(multiActions);
    }

    @Override
    protected StakeholderInviteBuilder createNewBuilderWithActions(List<BiConsumer<Integer, StakeholderInvite>> actions) {
        return new StakeholderInviteBuilder(actions);
    }

    @Override
    protected StakeholderInvite createInitial() {
        return createDefault(StakeholderInvite.class);
    }

    public StakeholderInviteBuilder withId(Long... ids) {
        return withArray((id, i) -> setField("id", id, i), ids);
    }

    public StakeholderInviteBuilder withName(String... names) {
        return withArray((name, i) -> setField("name", name, i), names);
    }

    public StakeholderInviteBuilder withEmail(String... emails) {
        return withArray((email, i) -> setField("email", email, i), emails);
    }
}


