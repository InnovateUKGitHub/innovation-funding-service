package org.innovateuk.ifs.category.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.category.domain.Category;

import java.util.List;
import java.util.function.BiConsumer;

public abstract class CategoryBuilder<C extends Category, B extends CategoryBuilder> extends BaseBuilder<C, B> {

    protected CategoryBuilder(List<BiConsumer<Integer, C>> multiActions) {
        super(multiActions);
    }

    public B withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public B withName(String... names) {
        return withArraySetFieldByReflection("name", names);
    }

    public B withDescription(String ... descriptions) { return withArraySetFieldByReflection("description", descriptions); }

    public B withPriority(Integer... priorities) { return withArraySetFieldByReflection("priority", priorities); }

}
