package org.innovateuk.ifs.category.builder;

import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.domain.InnovationSector;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class InnovationAreaBuilder extends CategoryBuilder<InnovationArea, InnovationAreaBuilder> {

    protected InnovationAreaBuilder(List<BiConsumer<Integer, InnovationArea>> multiActions) {
        super(multiActions);
    }

    public static InnovationAreaBuilder newInnovationArea() {
        return new InnovationAreaBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected InnovationAreaBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InnovationArea>> actions) {
        return new InnovationAreaBuilder(actions);
    }

    @Override
    protected InnovationArea createInitial() {
        return new InnovationArea();
    }

    public InnovationAreaBuilder withSector(InnovationSector... innovationSectors) {
        return withArraySetFieldByReflection("sector", innovationSectors);
    }
}
