package org.innovateuk.ifs.setup.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.setup.resource.SetupStatusResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class SetupStatusResourceBuilder extends BaseBuilder<SetupStatusResource, SetupStatusResourceBuilder> {
    private SetupStatusResourceBuilder(List<BiConsumer<Integer, SetupStatusResource>> multiActions) {
        super(multiActions);
    }

    public static SetupStatusResourceBuilder newSetupStatusResource() {
        return new SetupStatusResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected SetupStatusResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, SetupStatusResource>> actions) {
        return new SetupStatusResourceBuilder(actions);
    }

    @Override
    protected SetupStatusResource createInitial() {
        return new SetupStatusResource();
    }

    public SetupStatusResourceBuilder withId(Long... ids) {
        return withArray((id, setupStatus) -> setField("id", id, setupStatus), ids);
    }

    public SetupStatusResourceBuilder withCompleted(Boolean... completeds) {
        return withArray((completed, setupStatus) -> setField("completed", completed, setupStatus), completeds);
    }

    public SetupStatusResourceBuilder withClassName(String... classNames) {
        return withArray((className, setupStatus) -> setField("className", className, setupStatus), classNames);
    }

    public SetupStatusResourceBuilder withClassPk(Long... classPks) {
        return withArray((classPk, setupStatus) -> setField("classPk", classPk, setupStatus), classPks);
    }

    public SetupStatusResourceBuilder withParentId(Long... parentIds) {
        return withArray((parentId, setupStatus) -> setField("parentId", parentId, setupStatus), parentIds);
    }

    public SetupStatusResourceBuilder withTargetId(Long... targetIds) {
        return withArray((targetId, setupStatus) -> setField("targetId", targetId, setupStatus), targetIds);
    }

    public SetupStatusResourceBuilder withTargetClassName(String... targetClassNames) {
        return withArray((targetClassName, setupStatus) -> setField("targetClassName", targetClassName, setupStatus), targetClassNames);
    }

}
