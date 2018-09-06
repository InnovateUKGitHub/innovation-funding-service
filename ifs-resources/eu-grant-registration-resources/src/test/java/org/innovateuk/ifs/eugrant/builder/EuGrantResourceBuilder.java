package org.innovateuk.ifs.eugrant.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.eugrant.EuGrantResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class EuGrantResourceBuilder extends BaseBuilder<EuGrantResource, EuGrantResourceBuilder> {

    private EuGrantResourceBuilder(List<BiConsumer<Integer, EuGrantResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static EuGrantResourceBuilder newEuGrantResource() {
        return new EuGrantResourceBuilder(emptyList());
    }

    @Override
    protected EuGrantResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, EuGrantResource>> actions) {
        return new EuGrantResourceBuilder(actions);
    }

    @Override
    protected EuGrantResource createInitial() {
        return new EuGrantResource();
    }

}
