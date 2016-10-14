package com.worth.ifs.finance.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.finance.resource.cost.LabourCost;
import com.worth.ifs.finance.resource.cost.Materials;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.idBasedNames;
import static com.worth.ifs.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class MaterialsCostBuilder extends AbstractCostBuilder<Materials, MaterialsCostBuilder> {

    public static MaterialsCostBuilder newMaterials() {
        return new MaterialsCostBuilder(emptyList()).with(uniqueIds()).with(idBasedNames("Material "));
    }

    private MaterialsCostBuilder(List<BiConsumer<Integer, Materials>> multiActions) {
        super(multiActions);
    }

    @Override
    protected MaterialsCostBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Materials>> actions) {
        return new MaterialsCostBuilder(actions);
    }

    @Override
    protected Materials createInitial() {
        return new Materials();
    }
}
