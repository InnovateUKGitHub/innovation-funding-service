package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.InnovationLead;
import org.innovateuk.ifs.user.domain.User;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.*;

/**
 * Builder for {@link InnovationLead}s.
 */
public class InnovationLeadBuilder extends BaseBuilder<InnovationLead, InnovationLeadBuilder> {

    public static InnovationLeadBuilder newInnovationLead() {
        return new InnovationLeadBuilder(emptyList()).with(uniqueIds());
    }

    private InnovationLeadBuilder(List<BiConsumer<Integer, InnovationLead>> multiActions) {
        super(multiActions);
    }

    @Override
    protected InnovationLeadBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InnovationLead>> actions) {
        return new InnovationLeadBuilder(actions);
    }

    @Override
    protected InnovationLead createInitial() {
        return createDefault(InnovationLead.class);
    }

    public InnovationLeadBuilder withId(Long... ids) {
        return withArray((id, i) -> setField("id", id, i), ids);
    }

    public InnovationLeadBuilder withCompetition(Competition... competitions) {
        return withArray((competition, p) -> setField("competition", competition, p), competitions);
    }

    public InnovationLeadBuilder withUser(User... users) {
        return withArray((user, u) -> setField("user", user, u), users);
    }
}
