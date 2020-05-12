package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.ExternalFinance;
import org.innovateuk.ifs.user.domain.User;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.*;

public class CompetitionFinanceBuilder extends BaseBuilder<ExternalFinance, CompetitionFinanceBuilder> {

    public static CompetitionFinanceBuilder newCompetitionFinance() {
        return new CompetitionFinanceBuilder(emptyList()).with(uniqueIds());
    }

    private CompetitionFinanceBuilder(List<BiConsumer<Integer, ExternalFinance>> multiActions) {
        super(multiActions);
    }

    @Override
    protected CompetitionFinanceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ExternalFinance>> actions) {
        return new CompetitionFinanceBuilder(actions);
    }

    @Override
    protected ExternalFinance createInitial() {
        return createDefault(ExternalFinance.class);
    }

    public CompetitionFinanceBuilder withId(Long... ids)  {
        return withArray((id, i) -> setField("id", id, i), ids);
    }

    public CompetitionFinanceBuilder withCompetition(Competition... competitions) {
        return withArray((competition, p) -> setField("competition", competition, p), competitions);
    }

    public CompetitionFinanceBuilder withUser(User... users) {
        return withArray((user, u) -> setField("user", user, u), users);
    }
}