package org.innovateuk.ifs.file.service;

import org.innovateuk.ifs.file.service.MediaTypesGenerator;
import org.springframework.http.MediaType;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Generates a list of valid MediaTypes from a list of media type strings
 */
public class ByMediaTypeStringsMediaTypesGenerator implements MediaTypesGenerator<List<String>> {

    @Override
    public List<MediaType> apply(List<String> validMediaTypes) {
        return simpleMap(validMediaTypes, MediaType::valueOf);
    }
}
