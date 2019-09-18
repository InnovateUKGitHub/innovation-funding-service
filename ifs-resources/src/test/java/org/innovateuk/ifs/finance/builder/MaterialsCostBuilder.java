package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.cost.Materials;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class MaterialsCostBuilder extends BaseBuilder<Materials, MaterialsCostBuilder> {

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
        return new MaterialsCostBuilder(emptyList()).with(uniqueIds());
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
        return newInstance(Materials.class);
    }
}
