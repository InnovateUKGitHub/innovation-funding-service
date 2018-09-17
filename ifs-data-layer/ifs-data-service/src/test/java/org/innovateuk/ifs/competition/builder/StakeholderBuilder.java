package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Stakeholder;
import org.innovateuk.ifs.user.domain.User;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.createDefault;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

/**
 * Builder for {@link Stakeholder}s.
 */
public class StakeholderBuilder extends BaseBuilder<Stakeholder, StakeholderBuilder> {

    public static StakeholderBuilder newStakeholder() {
        return new StakeholderBuilder(emptyList()).with(uniqueIds());
    }

    private StakeholderBuilder(List<BiConsumer<Integer, Stakeholder>> multiActions) {
        super(multiActions);
    }

    @Override
    protected StakeholderBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Stakeholder>> actions) {
        return new StakeholderBuilder(actions);
    }

    @Override
    protected Stakeholder createInitial() {
        return createDefault(Stakeholder.class);
    }

    public StakeholderBuilder withId(Long... ids) {
        return withArray((id, i) -> setField("id", id, i), ids);
    }

    public StakeholderBuilder withCompetition(Competition... competitions) {
        return withArray((competition, p) -> setField("competition", competition, p), competitions);
    }

    public StakeholderBuilder withUser(User... users) {
        return withArray((user, u) -> setField("user", user, u), users);
    }
}

