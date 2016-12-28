package org.innovateuk.ifs.category.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.category.resource.CategoryType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

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

    public CategoryResourceBuilder withChildren(Collection<CategoryResource>... childrens) {
        return withArray((children, category) -> setField("children", new ArrayList<>(children), category), childrens);
    }
}
