package org.innovateuk.ifs.category.builder;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class InnovationAreaResourceBuilder extends CategoryResourceBuilder<InnovationAreaResource, InnovationAreaResourceBuilder> {
    protected InnovationAreaResourceBuilder(List<BiConsumer<Integer, InnovationAreaResource>> multiActions) {
        super(multiActions);
    }

    public static InnovationAreaResourceBuilder newInnovationAreaResource() {
        return new InnovationAreaResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected InnovationAreaResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InnovationAreaResource>> actions) {
        return new InnovationAreaResourceBuilder(actions);
    }

    @Override
    protected InnovationAreaResource createInitial() {
        return new InnovationAreaResource();
    }
}
