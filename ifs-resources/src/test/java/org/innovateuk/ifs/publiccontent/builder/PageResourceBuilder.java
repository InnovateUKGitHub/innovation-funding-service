package org.innovateuk.ifs.publiccontent.builder;

import org.innovateuk.ifs.BaseBuilder;

import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

public abstract class PageResourceBuilder<T, C> extends BaseBuilder<T, PageResourceBuilder<T, C>> {

    protected PageResourceBuilder(List<BiConsumer<Integer, T>> newMultiActions) {
        super(newMultiActions);
    }

    public PageResourceBuilder<T, C> withTotalElements(long totalElements) {
        return with(pageResource -> setField("totalElements", totalElements, pageResource));
    }

    public PageResourceBuilder<T, C> withTotalPages(long totalElements) {
        return with(pageResource -> setField("totalElements", totalElements, pageResource));
    }

    public PageResourceBuilder<T, C> withNumber(long totalElements) {
        return with(pageResource -> setField("number", totalElements, pageResource));
    }

    public PageResourceBuilder<T, C> withSize(long totalElements) {
        return with(pageResource -> setField("size", totalElements, pageResource));
    }

    public PageResourceBuilder<T, C> withContent(List<C> contentItems) {
        return with(pageResource -> setField("content", contentItems, pageResource));
    }
}
