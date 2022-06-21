package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.cost.PersonnelCost;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class PersonnelCostBuilder extends BaseBuilder<PersonnelCost, PersonnelCostBuilder> {

    public static PersonnelCostBuilder newPersonnelCost() {
        return new PersonnelCostBuilder(emptyList()).with(uniqueIds());
    }

    public PersonnelCostBuilder withId(Long... value) {
        return withArraySetFieldByReflection("id", value);
    }

    public PersonnelCostBuilder withName(String... value) {
        return withArraySetFieldByReflection("name", value);
    }

    public PersonnelCostBuilder withDescription(String... value) {
        return withArraySetFieldByReflection("description", value);
    }

    public PersonnelCostBuilder withRole(String... value) {
        return withArraySetFieldByReflection("role", value);
    }

    public PersonnelCostBuilder withGrossEmployeeCost(BigDecimal... value) {
        return withArraySetFieldByReflection("grossEmployeeCost", value);
    }

    public PersonnelCostBuilder withLabourDays(Integer... value) {
        return withArraySetFieldByReflection("labourDays", value);
    }

    public PersonnelCostBuilder withRate(BigDecimal... value) {
        return withArraySetFieldByReflection("rate", value);
    }

    public PersonnelCostBuilder withTotal(BigDecimal... value) {
        return withArraySetFieldByReflection("total", value);
    }

    public PersonnelCostBuilder withThirdPartyOfgem(boolean... value) {
        return withArraySetFieldByReflection("thirdPartyOfgem", value);
    }

    private PersonnelCostBuilder(List<BiConsumer<Integer, PersonnelCost>> multiActions) {
        super(multiActions);
    }

    @Override
    protected PersonnelCostBuilder createNewBuilderWithActions(List<BiConsumer<Integer, PersonnelCost>> actions) {
        return new PersonnelCostBuilder(actions);
    }

    @Override
    protected PersonnelCost createInitial() {
        return newInstance(PersonnelCost.class);
    }
}
