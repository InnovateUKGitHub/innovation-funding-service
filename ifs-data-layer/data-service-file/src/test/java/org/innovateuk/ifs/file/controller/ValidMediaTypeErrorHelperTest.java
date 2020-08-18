package org.innovateuk.ifs.file.controller;

import org.innovateuk.ifs.file.resource.FileTypeCategory;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.file.resource.FileTypeCategory.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

public class ValidMediaTypeErrorHelperTest {

    @Test
    public void pdfOnlyMessage() {
        assertSpecialisedMessageProduced(
                mediaTypeFromCategories(PDF),
                "UNSUPPORTED_MEDIA_TYPE_PDF_ONLY"
        );
    }

    @Test
    public void spreadsheetOnlyMessage() {
        assertSpecialisedMessageProduced(
                mediaTypeFromCategories(SPREADSHEET),
                "UNSUPPORTED_MEDIA_TYPE_SPREADSHEET_ONLY"
        );
    }

    @Test
    public void documentOnlyMessage() {
        assertSpecialisedMessageProduced(
                mediaTypeFromCategories(DOCUMENT),
                "UNSUPPORTED_MEDIA_TYPE_DOCUMENT_ONLY"
        );
    }

    @Test
    public void pdfAndSpreadsheetOnlyMessage() {
        assertSpecialisedMessageProduced(
                mediaTypeFromCategories(PDF, SPREADSHEET),
                "UNSUPPORTED_MEDIA_TYPE_SPREADSHEET_OR_PDF_ONLY"
        );
    }

    @Test
    public void openDocumentOnlyMessage() {
        assertSpecialisedMessageProduced(
                mediaTypeFromCategories(OPEN_DOCUMENT, OPEN_SPREADSHEET),
                "UNSUPPORTED_MEDIA_TYPE_OPEN_DOCUMENT_OR_OPEN_SPREADSHEET_ONLY"
        );
    }

    @Test
    public void pdfOrDocumentOnlyMessage() {
        assertSpecialisedMessageProduced(
                mediaTypeFromCategories(PDF, DOCUMENT),
                "UNSUPPORTED_MEDIA_TYPE_PDF_OR_DOCUMENT_ONLY"
        );
    }

    @Test
    public void pdfDocumentOrSpreadsheetOnlyMessage() {
        assertSpecialisedMessageProduced(
                mediaTypeFromCategories(PDF, DOCUMENT, SPREADSHEET),
                "UNSUPPORTED_MEDIA_TYPE_SPREADSHEET_OR_PDF_OR_DOCUMENT_ONLY"
        );
    }

    @Test
    public void openSpreadSheetOrOpenDocumentOrPdfOnlyMessage() {
        assertSpecialisedMessageProduced(
                mediaTypeFromCategories(OPEN_SPREADSHEET, OPEN_DOCUMENT, PDF),
                "UNSUPPORTED_MEDIA_TYPE_PDF_OR_OPEN_DOCUMENT_OR_OPEN_SPREADSHEET_ONLY"
        );
    }

    @Test
    public void noDirectMatchWithValidMediaTypes() {
        assertSpecialisedMessageProduced(singletonList(MediaType.APPLICATION_JSON), UNSUPPORTED_MEDIA_TYPE.name());
    }

    private void assertSpecialisedMessageProduced(List<MediaType> mediaTypes, String expectedErrorKey) {
        String errorKey = new ValidMediaTypeErrorHelper()
                .findErrorKey(mediaTypes);

        assertEquals(expectedErrorKey, errorKey);
    }

    private List<MediaType> mediaTypeFromCategories(FileTypeCategory... fileTypeCategories) {
        return Arrays.stream(fileTypeCategories)
                .flatMap(category -> category.getMimeTypes().stream())
                .map(MediaType::valueOf)
                .collect(toList());
    }
}
