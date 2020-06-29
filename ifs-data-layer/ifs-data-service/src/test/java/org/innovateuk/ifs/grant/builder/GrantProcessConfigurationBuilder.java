package org.innovateuk.ifs.grant.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.builder.CompetitionBuilder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.grant.domain.GrantProcessConfiguration;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class GrantProcessConfigurationBuilder extends BaseBuilder<GrantProcessConfiguration, GrantProcessConfigurationBuilder> {

    private GrantProcessConfigurationBuilder(List<BiConsumer<Integer, GrantProcessConfiguration>> newMultiActions) {
        super(newMultiActions);
    }

    public static GrantProcessConfigurationBuilder newGrantProcessConfiguration() {
        return new GrantProcessConfigurationBuilder(emptyList());
    }

    public GrantProcessConfigurationBuilder withCompetition(Competition competition) {
        return with(grantProcessConfiguration -> grantProcessConfiguration.setCompetition(competition));
    }

    public GrantProcessConfigurationBuilder withSendByDefault(boolean sendByDefault) {
        return with(grantProcessConfiguration -> grantProcessConfiguration.setSendByDefault(sendByDefault));
    }

    @Override
    protected GrantProcessConfigurationBuilder createNewBuilderWithActions(List<BiConsumer<Integer, GrantProcessConfiguration>> actions) {
        return new GrantProcessConfigurationBuilder(actions);
    }

    @Override
    protected GrantProcessConfiguration createInitial() {
        return new GrantProcessConfiguration();
    }
}
