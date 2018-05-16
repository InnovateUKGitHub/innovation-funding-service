package org.innovateuk.ifs.file.controller;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.file.resource.FileTypeCategory;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

/**
 * An implementation of {@link FileUploadErrorTranslator} that can translate file upload errors from the data layer into
 * specific web layer data messages depending on the media types that are valid to be uploaded.
 */
public class ValidMediaTypesFileUploadErrorTranslator implements FileUploadErrorTranslator {

    public static final String UNSUPPORTED_MEDIA_TYPE_PDF_ONLY_MESSAGE_KEY = "UNSUPPORTED_MEDIA_TYPE_PDF_ONLY";
    public static final String UNSUPPORTED_MEDIA_TYPE_SPREADSHEET_ONLY_MESSAGE_KEY = "UNSUPPORTED_MEDIA_TYPE_SPREADSHEET_ONLY";
    public static final String UNSUPPORTED_MEDIA_TYPE_PDF_OR_SPREADSHEET_ONLY_MESSAGE_KEY = "UNSUPPORTED_MEDIA_TYPE_PDF_AND_SPREADSHEET_ONLY";

    private static final EnumSet<FileTypeCategory> PDF_ONLY_VALID_MEDIA_TYPES = EnumSet.of(FileTypeCategory.PDF);
    private static final EnumSet<FileTypeCategory> SPREADSHEET_ONLY_VALID_MEDIA_TYPES = EnumSet.of(FileTypeCategory.SPREADSHEET);
    private static final EnumSet<FileTypeCategory> PDF_AND_SPREADSHEET_ONLY_VALID_MEDIA_TYPES = EnumSet.of(
            FileTypeCategory.PDF,
            FileTypeCategory.SPREADSHEET
    );

    private static final List<EnumSet<FileTypeCategory>> VALID_MEDIA_TYPE_SETS = asList(
            PDF_ONLY_VALID_MEDIA_TYPES,
            SPREADSHEET_ONLY_VALID_MEDIA_TYPES,
            PDF_AND_SPREADSHEET_ONLY_VALID_MEDIA_TYPES
    );

    private static final Map<EnumSet<FileTypeCategory>, String> VALID_MEDIA_TYPES_TO_ERROR_MESSAGE = asMap(
            PDF_ONLY_VALID_MEDIA_TYPES, UNSUPPORTED_MEDIA_TYPE_PDF_ONLY_MESSAGE_KEY,
            SPREADSHEET_ONLY_VALID_MEDIA_TYPES, UNSUPPORTED_MEDIA_TYPE_SPREADSHEET_ONLY_MESSAGE_KEY,
            PDF_AND_SPREADSHEET_ONLY_VALID_MEDIA_TYPES, UNSUPPORTED_MEDIA_TYPE_PDF_OR_SPREADSHEET_ONLY_MESSAGE_KEY
    );

    @Override
    public List<Error> translateFileUploadErrors(Function<Error, String> fieldIdFn, List<Error> originalErrors) {
        return simpleMap(originalErrors, e -> translateFileUploadErrors(fieldIdFn, e));
    }

    private Error translateFileUploadErrors(Function<Error, String> fieldIdFn, Error error) {

        Object fieldRejectedValue = error.getFieldRejectedValue();
        List<Object> arguments = error.getArguments();
        String fieldId = fieldIdFn.apply(error);

        if (UNSUPPORTED_MEDIA_TYPE.name().equals(error.getErrorKey()) && !arguments.isEmpty()) {

            String validMediaTypesString = (String) arguments.get(0);
            List<String> validMediaTypes = simpleMap(asList(validMediaTypesString.split(",")), StringUtils::trim);

            Optional<EnumSet<FileTypeCategory>> mediaTypeSetSupportingErrorConditions = simpleFindFirst(VALID_MEDIA_TYPE_SETS, set -> {
                List<String> allMediaTypesInThisSet = flattenLists(simpleMap(set, FileTypeCategory::getMediaTypes));
                return allMediaTypesInThisSet.size() == validMediaTypes.size() && allMediaTypesInThisSet.containsAll(validMediaTypes);
            });

            String errorMessage = mediaTypeSetSupportingErrorConditions.map(VALID_MEDIA_TYPES_TO_ERROR_MESSAGE::get)
                    .orElse(error.getErrorKey());

            return fieldError(fieldId, fieldRejectedValue, errorMessage, arguments);
        } else {
            return fieldError(fieldId, fieldRejectedValue, error.getErrorKey(), arguments);
        }
    }
}
