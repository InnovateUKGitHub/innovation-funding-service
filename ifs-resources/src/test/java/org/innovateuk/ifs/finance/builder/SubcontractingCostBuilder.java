package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.cost.SubContractingCost;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class SubcontractingCostBuilder extends BaseBuilder<SubContractingCost, SubcontractingCostBuilder> {

    public SubcontractingCostBuilder withId(Long... id) {
        return withArraySetFieldByReflection("id", id);
    }

    public SubcontractingCostBuilder withName(String... value) {
        return withArraySetFieldByReflection("name", value);
    }

    public SubcontractingCostBuilder withCountry(String... value) {
        return withArraySetFieldByReflection("country", value);
    }

    public SubcontractingCostBuilder withRole(String... value) {
        return withArraySetFieldByReflection("role", value);
    }

    public SubcontractingCostBuilder withCost(BigDecimal... value) {
        return withArraySetFieldByReflection("cost", value);
    }

    public static SubcontractingCostBuilder newSubContractingCost() {
        return new SubcontractingCostBuilder(emptyList()).with(uniqueIds());
    }

    private SubcontractingCostBuilder(List<BiConsumer<Integer, SubContractingCost>> multiActions) {
        super(multiActions);
    }

    @Override
    protected SubcontractingCostBuilder createNewBuilderWithActions(List<BiConsumer<Integer, SubContractingCost>> actions) {
        return new SubcontractingCostBuilder(actions);
    }

    @Override
    protected SubContractingCost createInitial() {
        return new SubContractingCost();
    }
}
