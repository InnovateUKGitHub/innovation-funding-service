package org.innovateuk.ifs.commons.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;
import java.util.function.BiConsumer;

public abstract class PageResourceBuilder<T extends PageResource<C>, S extends PageResourceBuilder<T, S, C>, C> extends BaseBuilder<T, S> {

    protected PageResourceBuilder(List<BiConsumer<Integer, T>> newMultiActions) {
        super(newMultiActions);
    }

    public S withTotalElements(Long... totalElements) {
        return withArraySetFieldByReflection("totalElements", totalElements);
    }

    public S withTotalPages(Integer... totalPages) {
        return withArraySetFieldByReflection("totalPages", totalPages);
    }

    public S withContent(List<C>... content) {
        return withArraySetFieldByReflection("content", content);
    }

    public S withNumber(Integer... number) {
        return withArraySetFieldByReflection("number", number);
    }

    public S withSize(Integer... size) {
        return withArraySetFieldByReflection("size", size);
    }
}
