package org.innovateuk.ifs.file.service;

import org.springframework.http.MediaType;

import java.util.List;
import java.util.function.Function;

/**
 * Generate a set of MediaTypes from a given context represented by type T.
 */
public interface MediaTypesGenerator<T> extends Function<T, List<MediaType>> {
}
