package org.innovateuk.ifs.file.controller;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.file.resource.FileTypeCategories;

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
 * A helper class to handle some of the intricacies of translating file upload errors from the data layer into
 * appropriate errors for the user
 */
public class MediaTypeMatchingFileUploadErrorTranslator implements FileUploadErrorTranslator {

    public static final String UNSUPPORTED_MEDIA_TYPE_PDF_ONLY_MESSAGE_KEY = "UNSUPPORTED_MEDIA_TYPE_PDF_ONLY";
    public static final String UNSUPPORTED_MEDIA_TYPE_SPREADSHEET_ONLY_MESSAGE_KEY = "UNSUPPORTED_MEDIA_TYPE_SPREADSHEET_ONLY";
    public static final String UNSUPPORTED_MEDIA_TYPE_PDF_OR_SPREADSHEET_ONLY_MESSAGE_KEY = "UNSUPPORTED_MEDIA_TYPE_PDF_AND_SPREADSHEET_ONLY";

    private static final EnumSet<FileTypeCategories> PDF_ONLY_VALID_MEDIA_TYPES = EnumSet.of(FileTypeCategories.PDF);
    private static final EnumSet<FileTypeCategories> SPREADSHEET_ONLY_VALID_MEDIA_TYPES = EnumSet.of(FileTypeCategories.SPREADSHEET);
    private static final EnumSet<FileTypeCategories> PDF_AND_SPREADSHEET_ONLY_VALID_MEDIA_TYPES = EnumSet.of(FileTypeCategories.PDF, FileTypeCategories.SPREADSHEET);

    private static final List<EnumSet<FileTypeCategories>> VALID_MEDIA_TYPE_SETS =
            asList(PDF_ONLY_VALID_MEDIA_TYPES, SPREADSHEET_ONLY_VALID_MEDIA_TYPES, PDF_AND_SPREADSHEET_ONLY_VALID_MEDIA_TYPES);

    private static final Map<EnumSet<FileTypeCategories>, String> VALID_MEDIA_TYPES_TO_ERROR_MESSAGE = asMap(
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

            Optional<EnumSet<FileTypeCategories>> mediaTypeSetSupportingErrorConditions = simpleFindFirst(VALID_MEDIA_TYPE_SETS, set -> {
                List<String> allMediaTypesInThisSet = flattenLists(simpleMap(set, FileTypeCategories::getMediaTypes));
                return allMediaTypesInThisSet.size() == validMediaTypes.size() && allMediaTypesInThisSet.containsAll(validMediaTypes);
            });

            String errorMessage = mediaTypeSetSupportingErrorConditions.map(VALID_MEDIA_TYPES_TO_ERROR_MESSAGE::get).
                    orElse(error.getErrorKey());

            return fieldError(fieldId, fieldRejectedValue, errorMessage);
        } else {
            return fieldError(fieldId, fieldRejectedValue, error.getErrorKey());
        }
    }
}
