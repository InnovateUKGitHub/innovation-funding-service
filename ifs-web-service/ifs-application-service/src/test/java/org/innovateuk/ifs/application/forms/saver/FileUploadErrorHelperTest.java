package org.innovateuk.ifs.application.forms.saver;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestFailure;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.file.resource.FileTypeCategories;
import org.junit.Test;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.junit.Assert.assertArrayEquals;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

public class FileUploadErrorHelperTest {

    private Error pdfOnlyErrorFromDataLayer = fieldError(null, "application/madeup", UNSUPPORTED_MEDIA_TYPE.name(),
            singletonList(FileTypeCategories.PDF.getMediaTypeString()));

    private Error spreadsheetOnlyErrorFromDataLayer = fieldError(null, "application/madeup", UNSUPPORTED_MEDIA_TYPE.name(),
            singletonList(FileTypeCategories.SPREADSHEET.getMediaTypeString()));

    private Error pdfOrSpreadsheetOnlyErrorFromDataLayer = fieldError(null, "application/madeup", UNSUPPORTED_MEDIA_TYPE.name(),
            singletonList(FileTypeCategories.SPREADSHEET.getMediaTypeString() + ", " + FileTypeCategories.PDF.getMediaTypeString()));

    private Error notDirectMatchingMediaTypesErrorFromDataLayer = fieldError(null, "application/madeup", UNSUPPORTED_MEDIA_TYPE.name(),
            singletonList(FileTypeCategories.PDF.getMediaTypeString() + ", application/nomatch"));


    @Test
    public void testPdfOnlyMessage() {
        assertSpecialisedMessageProduced(pdfOnlyErrorFromDataLayer, FileUploadErrorHandler.UNSUPPORTED_MEDIA_TYPE_PDF_ONLY_MESSAGE_KEY);
    }

    @Test
    public void testSpreadsheetOnlyMessage() {
        assertSpecialisedMessageProduced(spreadsheetOnlyErrorFromDataLayer, FileUploadErrorHandler.UNSUPPORTED_MEDIA_TYPE_SPREADSHEET_ONLY_MESSAGE_KEY);
    }

    @Test
    public void testPdfAndSpreadsheetOnlyMessage() {
        assertSpecialisedMessageProduced(pdfOrSpreadsheetOnlyErrorFromDataLayer, FileUploadErrorHandler.UNSUPPORTED_MEDIA_TYPE_PDF_OR_SPREADSHEET_ONLY_MESSAGE_KEY);
    }

    @Test
    public void testNoDirectMatchWithValidMediaTypes() {
        assertSpecialisedMessageProduced(notDirectMatchingMediaTypesErrorFromDataLayer, UNSUPPORTED_MEDIA_TYPE.name());
    }

    private void assertSpecialisedMessageProduced(Error errorFromDataLayer, String expectedErrorKey) {

        ValidationMessages validationMessages = new FileUploadErrorHandler().generateFileUploadError(123L, RestFailure.error(singletonList(errorFromDataLayer)));

        Error expectedSpecialisedError = fieldError("formInput[123]", "application/madeup", expectedErrorKey);

        assertArrayEquals(new Error[] {expectedSpecialisedError}, validationMessages.getErrors().toArray());
    }
}
