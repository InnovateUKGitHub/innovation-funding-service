package org.innovateuk.ifs.eugrant.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.eugrant.EuActionTypeResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class EuActionTypeResourceBuilder extends BaseBuilder<EuActionTypeResource, EuActionTypeResourceBuilder> {

    private EuActionTypeResourceBuilder(List<BiConsumer<Integer, EuActionTypeResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static EuActionTypeResourceBuilder newEuActionTypeResource() {
        return new EuActionTypeResourceBuilder(emptyList());
    }

    @Override
    protected EuActionTypeResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, EuActionTypeResource>> actions) {
        return new EuActionTypeResourceBuilder(actions);
    }

    @Override
    protected EuActionTypeResource createInitial() {
        return new EuActionTypeResource();
    }

    public EuActionTypeResourceBuilder withId(Long... ids) {
        return withArray((id, actionType) -> actionType.setId(id), ids);
    }

    public EuActionTypeResourceBuilder withName(String... names) {
        return withArray((name, actionType) -> actionType.setName(name), names);
    }

    public EuActionTypeResourceBuilder withDescription(String... descriptions) {
        return withArray((description, actionType) -> actionType.setDescription(description), descriptions);
    }

    public EuActionTypeResourceBuilder withPriority(Integer... priorities) {
        return withArray((priority, actionType) -> actionType.setPriority(priority), priorities);
    }

}