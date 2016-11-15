package com.worth.ifs.category.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.category.resource.CategoryResource;
import com.worth.ifs.category.resource.CategoryType;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class CategoryResourceBuilder extends BaseBuilder<CategoryResource, CategoryResourceBuilder> {
    private CategoryResourceBuilder(List<BiConsumer<Integer, CategoryResource>> multiActions) {
        super(multiActions);
    }

    public static CategoryResourceBuilder newCategoryResource() {
        return new CategoryResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected CategoryResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CategoryResource>> actions) {
        return new CategoryResourceBuilder(actions);
    }

    @Override
    protected CategoryResource createInitial() {
        return new CategoryResource();
    }

    public CategoryResourceBuilder withId(Long... ids) {
        return withArray((id, category) -> setField("id", id, category), ids);
    }

    public CategoryResourceBuilder withName(String... names) {
        return withArray((name, category) -> setField("name", name, category), names);
    }

    public CategoryResourceBuilder withType(CategoryType... types) {
        return withArray((type, category) -> setField("type", type, category), types);
    }

    public CategoryResourceBuilder withParent(Long... parents) {
        return withArray((parent, category) -> setField("parent", parent, category), parents);
    }

    public CategoryResourceBuilder withChildren(List<Long>... childrens) {
        return withArray((children, category) -> setField("children", children, category), childrens);
    }
}
