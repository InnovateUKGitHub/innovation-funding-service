package com.worth.ifs.finance.builder;

import com.worth.ifs.finance.resource.cost.Materials;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.idBasedNames;
import static com.worth.ifs.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class MaterialsCostBuilder extends AbstractCostBuilder<Materials, MaterialsCostBuilder> {

    public MaterialsCostBuilder withId(Long... id) {
        return withArraySetFieldByReflection("id", id);
    }

    public MaterialsCostBuilder withItem(String... value) {
        return withArraySetFieldByReflection("item", value);
    }

    public MaterialsCostBuilder withCost(BigDecimal... value) {
        return withArraySetFieldByReflection("cost", value);
    }

    public MaterialsCostBuilder withQuantity(Integer... value) {
        return withArraySetFieldByReflection("quantity", value);
    }

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
