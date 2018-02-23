package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.invite.resource.StagedApplicationResource;
import org.innovateuk.ifs.invite.resource.StagedApplicationListResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class StagedApplicationListResourceBuilder extends BaseBuilder<StagedApplicationListResource, StagedApplicationListResourceBuilder> {

    public static StagedApplicationListResourceBuilder newStagedApplicationListResource() {
        return new StagedApplicationListResourceBuilder(emptyList());
    }

    private StagedApplicationListResourceBuilder(List<BiConsumer<Integer, StagedApplicationListResource>> newActions) {
        super(newActions);
    }

    @Override
    protected StagedApplicationListResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, StagedApplicationListResource>> actions) {
        return new StagedApplicationListResourceBuilder(actions);
    }

    @Override
    protected StagedApplicationListResource createInitial() {
        return new StagedApplicationListResource();
    }

    public StagedApplicationListResourceBuilder withInvites(List<StagedApplicationResource>... invites) {
        return withArraySetFieldByReflection("invites", invites);
    }
}