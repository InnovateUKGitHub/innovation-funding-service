package org.innovateuk.ifs.category.builder;

import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.domain.InnovationSector;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class InnovationSectorBuilder extends CategoryBuilder<InnovationSector, InnovationSectorBuilder> {

    protected InnovationSectorBuilder(List<BiConsumer<Integer, InnovationSector>> multiActions) {
        super(multiActions);
    }

    public static InnovationSectorBuilder newInnovationSector() {
        return new InnovationSectorBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected InnovationSectorBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InnovationSector>> actions) {
        return new InnovationSectorBuilder(actions);
    }

    @Override
    protected InnovationSector createInitial() {
        return new InnovationSector();
    }

    public InnovationSectorBuilder withChildren(List<InnovationArea>... innovationAreaLists) {
        return withArray((innovationAreaList, innovationSector) ->  innovationSector.setChildren(innovationAreaList), innovationAreaLists);
    }
}
