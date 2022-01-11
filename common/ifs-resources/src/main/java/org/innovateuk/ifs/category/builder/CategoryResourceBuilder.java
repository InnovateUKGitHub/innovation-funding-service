package org.innovateuk.ifs.category.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.category.resource.CategoryResource;

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
        return withArraySetFieldByReflection("name", names);
    }
}
