package com.worth.ifs.category.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.category.domain.Category;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class CategoryBuilder extends BaseBuilder<Category, CategoryBuilder> {

    public static CategoryBuilder newCategory() {
        return new CategoryBuilder(emptyList()).with(uniqueIds());
    }

    private CategoryBuilder(List<BiConsumer<Integer, Category>> multiActions) {
        super(multiActions);
    }

    @Override
    protected CategoryBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Category>> actions) {
        return new CategoryBuilder(actions);
    }

    @Override
    protected Category createInitial() {
        return new Category();
    }
}
