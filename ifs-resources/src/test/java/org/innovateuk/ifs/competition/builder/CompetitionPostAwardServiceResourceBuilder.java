package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionPostAwardServiceResource;
import org.innovateuk.ifs.competition.resource.PostAwardService;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class CompetitionPostAwardServiceResourceBuilder extends BaseBuilder<CompetitionPostAwardServiceResource, CompetitionPostAwardServiceResourceBuilder> {

    private CompetitionPostAwardServiceResourceBuilder(List<BiConsumer<Integer, CompetitionPostAwardServiceResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static CompetitionPostAwardServiceResourceBuilder newCompetitionPostAwardServiceResource() {
        return new CompetitionPostAwardServiceResourceBuilder(emptyList());
    }

    public CompetitionPostAwardServiceResourceBuilder withCompetitionId(Long... competitionIds) {
        return withArraySetFieldByReflection("competitionId", competitionIds);
    }

    public CompetitionPostAwardServiceResourceBuilder withPostAwardService(PostAwardService... postAwardServices) {
        return withArraySetFieldByReflection("postAwardService", postAwardServices);
    }

    @Override
    protected CompetitionPostAwardServiceResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionPostAwardServiceResource>> actions) {
        return new CompetitionPostAwardServiceResourceBuilder(actions);
    }

    @Override
    protected CompetitionPostAwardServiceResource createInitial() {
        return new CompetitionPostAwardServiceResource();
    }
}
