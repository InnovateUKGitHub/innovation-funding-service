package org.innovateuk.ifs.file.service;

import org.springframework.http.MediaType;

import java.util.List;
import java.util.function.Function;

/**
 * An interface representing a component that can generate a set of MediaTypes from a given context, represented by type {@link MediaTypeContext}.
 */
public interface MediaTypesGenerator<MediaTypeContext> extends Function<MediaTypeContext, List<MediaType>> {
}
