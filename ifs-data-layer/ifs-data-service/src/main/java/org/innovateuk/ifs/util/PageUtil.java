package org.innovateuk.ifs.util;

import org.innovateuk.ifs.commons.resource.PageResource;
import org.springframework.data.domain.Page;

import java.util.function.Function;
import java.util.stream.Collectors;

public class PageUtil {

    public static <R> PageResource<R> toPageResource(Page<R> page) {
        return new PageResource<R>(
                page.getTotalElements(),
                page.getTotalPages(),
                page.getContent(),
                page.getNumber(),
                page.getSize()
        );
    }

    public static <R, D> PageResource<R> toPageResource(Page<D> page, Function<D, R> mapper) {
        return new PageResource<R>(
                page.getTotalElements(),
                page.getTotalPages(),
                page.getContent().stream().map(mapper).collect(Collectors.toList()),
                page.getNumber(),
                page.getSize()
        );
    }
}
