package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.invite.resource.StagedApplicationResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class StagedApplicationResourceBuilder extends BaseBuilder<StagedApplicationResource, StagedApplicationResourceBuilder> {

    private StagedApplicationResourceBuilder(List<BiConsumer<Integer, StagedApplicationResource>> newActions) {
        super(newActions);
    }

    public static StagedApplicationResourceBuilder newStagedApplicationResource() {
        return new StagedApplicationResourceBuilder(emptyList());
    }

    @Override
    protected StagedApplicationResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, StagedApplicationResource>> actions) {
        return new StagedApplicationResourceBuilder(actions);
    }

    @Override
    protected StagedApplicationResource createInitial() {
        return new StagedApplicationResource();
    }

    public StagedApplicationResourceBuilder withApplicationId(Long... applicationIds) {
        return withArraySetFieldByReflection("applicationId", applicationIds);
    }

    public StagedApplicationResourceBuilder withCompetitionId(Long... competitionIds) {
        return withArraySetFieldByReflection("competitionId", competitionIds);
    }
}