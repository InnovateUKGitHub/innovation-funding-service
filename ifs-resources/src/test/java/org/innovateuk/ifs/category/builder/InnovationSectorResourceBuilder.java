package org.innovateuk.ifs.category.builder;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class InnovationSectorResourceBuilder extends CategoryResourceBuilder<InnovationSectorResource, InnovationSectorResourceBuilder> {
    protected InnovationSectorResourceBuilder(List<BiConsumer<Integer, InnovationSectorResource>> multiActions) {
        super(multiActions);
    }

    public static InnovationSectorResourceBuilder newInnovationSectorResource() {
        return new InnovationSectorResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected InnovationSectorResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InnovationSectorResource>> actions) {
        return new InnovationSectorResourceBuilder(actions);
    }

    @Override
    protected InnovationSectorResource createInitial() {
        return new InnovationSectorResource();
    }

    public InnovationSectorResourceBuilder withChildren(List<InnovationAreaResource>... innovationAreaResourceLists) {
        return withArraySetFieldByReflection("children", innovationAreaResourceLists);
    }
}
