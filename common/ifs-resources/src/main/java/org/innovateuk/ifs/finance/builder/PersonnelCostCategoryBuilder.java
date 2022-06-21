package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.finance.resource.category.PersonnelCostCategory;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class PersonnelCostCategoryBuilder extends AbstractFinanceRowCostCategoryBuilder<PersonnelCostCategory, PersonnelCostCategoryBuilder> {

    public static PersonnelCostCategoryBuilder newPersonnelCostCategory() {
        return new PersonnelCostCategoryBuilder(emptyList());
    }

    private PersonnelCostCategoryBuilder(List<BiConsumer<Integer, PersonnelCostCategory>> multiActions) {
        super(multiActions);
    }

    @Override
    protected PersonnelCostCategoryBuilder createNewBuilderWithActions(List<BiConsumer<Integer, PersonnelCostCategory>> actions) {
        return new PersonnelCostCategoryBuilder(actions);
    }

    @Override
    protected PersonnelCostCategory createInitial() {
        return newInstance(PersonnelCostCategory.class);
    }
}

    

