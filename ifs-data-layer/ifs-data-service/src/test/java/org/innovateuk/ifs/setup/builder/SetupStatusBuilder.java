package org.innovateuk.ifs.setup.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.setup.domain.SetupStatus;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class SetupStatusBuilder extends BaseBuilder<SetupStatus, SetupStatusBuilder> {

    public static SetupStatusBuilder newSetupStatus() {
        return new SetupStatusBuilder(emptyList()).with(uniqueIds());
    }

    private SetupStatusBuilder(List<BiConsumer<Integer, SetupStatus>> multiActions) {
        super(multiActions);
    }

    @Override
    protected SetupStatusBuilder createNewBuilderWithActions(List<BiConsumer<Integer, SetupStatus>> actions) {
        return new SetupStatusBuilder(actions);
    }

    public SetupStatusBuilder withId(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public SetupStatusBuilder withCompleted(Boolean... completeds) {
        return withArray((completed, setupStatus) -> setField("completed", completed, setupStatus), completeds);
    }

    public SetupStatusBuilder withClassName(String... classNames) {
        return withArray((className, setupStatus) -> setField("className", className, setupStatus), classNames);
    }

    public SetupStatusBuilder withClassPk(Long... classPks) {
        return withArray((classPk, setupStatus) -> setField("classPk", classPk, setupStatus), classPks);
    }

    public SetupStatusBuilder withParentId(Long... parentIds) {
        return withArray((parentId, setupStatus) -> setField("parentId", parentId, setupStatus), parentIds);
    }

    public SetupStatusBuilder withTargetId(Long... targetIds) {
        return withArray((targetId, setupStatus) -> setField("targetId", targetId, setupStatus), targetIds);
    }

    public SetupStatusBuilder withTargetClassName(String... targetClassNames) {
        return withArray((targetClassName, setupStatus) -> setField("targetClassName", targetClassName, setupStatus), targetClassNames);
    }

    @Override
    protected SetupStatus createInitial() {
        return new SetupStatus();
    }
}
