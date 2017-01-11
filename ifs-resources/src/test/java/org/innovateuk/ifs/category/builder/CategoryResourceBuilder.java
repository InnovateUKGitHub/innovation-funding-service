package org.innovateuk.ifs.category.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.category.resource.CategoryType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

public abstract class CategoryResourceBuilder<T extends CategoryResource, B extends CategoryResourceBuilder<T,B>> extends BaseBuilder<T, B> {
    protected CategoryResourceBuilder(List<BiConsumer<Integer, T>> multiActions) {
        super(multiActions);
    }

    public B withId(Long... ids) {
        return withArray((id, category) -> setField("id", id, category), ids);
    }

    public B withName(String... names) {
        return withArray((name, category) -> setField("name", name, category), names);
    }

    public B withType(CategoryType... types) {
        return withArray((type, category) -> setField("type", type, category), types);
    }

    public B withParent(Long... parents) {
        return withArray((parent, category) -> setField("parent", parent, category), parents);
    }

    public B withChildren(Collection<CategoryResource>... childrens) {
        return withArray((children, category) -> setField("children", new ArrayList<>(children), category), childrens);
    }
}
