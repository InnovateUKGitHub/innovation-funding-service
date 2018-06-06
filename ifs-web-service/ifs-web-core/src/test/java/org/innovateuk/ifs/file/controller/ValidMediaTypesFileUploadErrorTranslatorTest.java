package org.innovateuk.ifs.file.controller;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.file.resource.FileTypeCategory;
import org.junit.Test;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.file.resource.FileTypeCategory.PDF;
import static org.innovateuk.ifs.file.resource.FileTypeCategory.SPREADSHEET;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleJoiner;
import static org.junit.Assert.assertArrayEquals;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

public class ValidMediaTypesFileUploadErrorTranslatorTest {

    private Error pdfOnlyErrorFromDataLayer = fieldError(
            null,
            "application/madeup",
            UNSUPPORTED_MEDIA_TYPE.name(),
            singletonList(mediaTypesString(PDF))
    );

    private Error spreadsheetOnlyErrorFromDataLayer = fieldError(
            null,
            "application/madeup",
            UNSUPPORTED_MEDIA_TYPE.name(),
            singletonList(mediaTypesString(SPREADSHEET))
    );

    private Error pdfOrSpreadsheetOnlyErrorFromDataLayer = fieldError(
            null,
            "application/madeup",
            UNSUPPORTED_MEDIA_TYPE.name(),
            singletonList(
                    mediaTypesString(SPREADSHEET) + ", " +
                            mediaTypesString(PDF)
            )
    );

    private Error notDirectMatchingMediaTypesErrorFromDataLayer = fieldError(
            null,
            "application/madeup",
            UNSUPPORTED_MEDIA_TYPE.name(),
            singletonList(mediaTypesString(PDF) + ", application/nomatch")
    );

    @Test
    public void testPdfOnlyMessage() {
        assertSpecialisedMessageProduced(
                pdfOnlyErrorFromDataLayer,
                ValidMediaTypesFileUploadErrorTranslator.UNSUPPORTED_MEDIA_TYPE_PDF_ONLY_MESSAGE_KEY
        );
    }

    @Test
    public void testSpreadsheetOnlyMessage() {
        assertSpecialisedMessageProduced(
                spreadsheetOnlyErrorFromDataLayer,
                ValidMediaTypesFileUploadErrorTranslator.UNSUPPORTED_MEDIA_TYPE_SPREADSHEET_ONLY_MESSAGE_KEY
        );
    }

    @Test
    public void testPdfAndSpreadsheetOnlyMessage() {
        assertSpecialisedMessageProduced(
                pdfOrSpreadsheetOnlyErrorFromDataLayer,
                ValidMediaTypesFileUploadErrorTranslator.UNSUPPORTED_MEDIA_TYPE_PDF_OR_SPREADSHEET_ONLY_MESSAGE_KEY
        );
    }

    @Test
    public void testNoDirectMatchWithValidMediaTypes() {
        assertSpecialisedMessageProduced(notDirectMatchingMediaTypesErrorFromDataLayer, UNSUPPORTED_MEDIA_TYPE.name());
    }

    private void assertSpecialisedMessageProduced(Error errorFromDataLayer, String expectedErrorKey) {

        List<Error> errors = new ValidMediaTypesFileUploadErrorTranslator()
                .translateFileUploadErrors(e -> "formInput[123]", singletonList(errorFromDataLayer));

        Error expectedSpecialisedError = fieldError("formInput[123]", "application/madeup", expectedErrorKey, errorFromDataLayer.getArguments());

        assertArrayEquals(new Error[] {expectedSpecialisedError}, errors.toArray());
    }

    private String mediaTypesString(FileTypeCategory fileTypeCategory) {
        return simpleJoiner(fileTypeCategory.getMediaTypes(), ", ");
    }
}
