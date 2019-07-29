package org.innovateuk.ifs.file.controller;

import org.apache.commons.collections.SetUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.file.resource.FileTypeCategory;
import org.springframework.http.MediaType;

import java.util.*;
import java.util.stream.Collectors;

import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.innovateuk.ifs.file.resource.FileTypeCategory.*;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

/**
 * Translates file upload errors from the into specific error keys depending on the media types that are valid to be uploaded.
 */
public class ValidMediaTypeErrorHelper {

    public static final String UNSUPPORTED_MEDIA_TYPE_PDF_ONLY_MESSAGE_KEY = "UNSUPPORTED_MEDIA_TYPE_PDF_ONLY";
    public static final String UNSUPPORTED_MEDIA_TYPE_SPREADSHEET_ONLY_MESSAGE_KEY = "UNSUPPORTED_MEDIA_TYPE_SPREADSHEET_ONLY";
    public static final String UNSUPPORTED_MEDIA_TYPE_PDF_OR_SPREADSHEET_ONLY_MESSAGE_KEY = "UNSUPPORTED_MEDIA_TYPE_PDF_AND_SPREADSHEET_ONLY";
    public static final String UNSUPPORTED_MEDIA_TYPE_PDF_OR_DOCUMENT_ONLY_MESSAGE_KEY = "UNSUPPORTED_MEDIA_TYPE_PDF_AND_DOCUMENT_ONLY";
    public static final String UNSUPPORTED_MEDIA_TYPE_PDF_OR_DOCUMENT_OR_SPREADSHEET_ONLY_MESSAGE_KEY = "UNSUPPORTED_MEDIA_TYPE_PDF_AND_DOCUMENT_AND_SPREADSHEET_ONLY";
    public static final String UNSUPPORTED_MEDIA_TYPE_OPEN_DOCUMENT_OR_SPREADSHEET_ONLY_MESSAGE_KEY = "UNSUPPORTED_MEDIA_TYPE_OPEN_DOCUMENT_AND_SPREADSHEET_ONLY";


    private static final Set<Pair<EnumSet<FileTypeCategory>, String>> TYPE_TO_ERROR_KEY_SET = asSet(
            Pair.of(EnumSet.of(PDF), UNSUPPORTED_MEDIA_TYPE_PDF_ONLY_MESSAGE_KEY),
            Pair.of(EnumSet.of(SPREADSHEET), UNSUPPORTED_MEDIA_TYPE_SPREADSHEET_ONLY_MESSAGE_KEY),
            Pair.of(EnumSet.of(PDF, SPREADSHEET), UNSUPPORTED_MEDIA_TYPE_PDF_OR_SPREADSHEET_ONLY_MESSAGE_KEY),
            Pair.of(EnumSet.of(PDF, DOCUMENT), UNSUPPORTED_MEDIA_TYPE_PDF_OR_DOCUMENT_ONLY_MESSAGE_KEY),
            Pair.of(EnumSet.of(PDF, DOCUMENT, SPREADSHEET), UNSUPPORTED_MEDIA_TYPE_PDF_OR_DOCUMENT_OR_SPREADSHEET_ONLY_MESSAGE_KEY),
            Pair.of(EnumSet.of(OPEN_DOCUMENT, OPEN_SPREADSHEET), UNSUPPORTED_MEDIA_TYPE_OPEN_DOCUMENT_OR_SPREADSHEET_ONLY_MESSAGE_KEY)
    );

    public String findErrorKey(List<MediaType> validMediaTypesList) {
        Set<MediaType> validMediaTypes = new HashSet<>(validMediaTypesList);

        Optional<String> errorMessage = TYPE_TO_ERROR_KEY_SET.stream()
                .filter(typeToErrorKey -> SetUtils.isEqualSet(validMediaTypes, collectMediaTypes(typeToErrorKey.getKey())))
                .map(Pair::getRight)
                .findFirst();

        return errorMessage.orElse(UNSUPPORTED_MEDIA_TYPE.name());
    }

    private Set<MediaType> collectMediaTypes(EnumSet<FileTypeCategory> types) {
        return types.stream()
                .flatMap(type -> type.getMimeTypes().stream())
                .map(MediaType::valueOf)
                .collect(Collectors.toSet());
    }
}
