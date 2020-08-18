package org.innovateuk.ifs.file.controller;

import org.apache.commons.collections.SetUtils;
import org.innovateuk.ifs.file.resource.FileTypeCategory;
import org.springframework.http.MediaType;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.innovateuk.ifs.file.resource.FileTypeCategory.*;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

/**
 * Translates file upload errors from the into specific error keys depending on the media types that are valid to be uploaded.
 */
public class ValidMediaTypeErrorHelper {

    /* Groups of FileTypeCategory that are supported as upload validation types. .*/
    private static final Set<EnumSet<FileTypeCategory>> UPLOAD_TYPES = asSet(
            EnumSet.of(PDF),
            EnumSet.of(SPREADSHEET),
            EnumSet.of(DOCUMENT),
            EnumSet.of(PDF, SPREADSHEET),
            EnumSet.of(PDF, DOCUMENT),
            EnumSet.of(PDF, DOCUMENT, SPREADSHEET),
            EnumSet.of(OPEN_DOCUMENT, OPEN_SPREADSHEET),
            EnumSet.of(OPEN_SPREADSHEET, OPEN_DOCUMENT, PDF)
    );

    public String findErrorKey(List<MediaType> validMediaTypesList) {
        Set<MediaType> validMediaTypes = new HashSet<>(validMediaTypesList);

        Optional<EnumSet<FileTypeCategory>> fileTypeCategories = UPLOAD_TYPES.stream()
                .filter(types -> SetUtils.isEqualSet(validMediaTypes, collectMediaTypes(types)))
                .findFirst();

        Optional<String> errorKey = fileTypeCategories
                .map(types -> types.stream().map(FileTypeCategory::name).collect(Collectors.joining("_OR_")));

        return errorKey
                .map(message -> String.format("%s_%s_%s", UNSUPPORTED_MEDIA_TYPE.name(), message, "ONLY"))
                .orElse(UNSUPPORTED_MEDIA_TYPE.name());
    }

    private Set<MediaType> collectMediaTypes(EnumSet<FileTypeCategory> types) {
        return types.stream()
                .flatMap(type -> type.getMimeTypes().stream())
                .map(MediaType::valueOf)
                .collect(toSet());
    }
}
