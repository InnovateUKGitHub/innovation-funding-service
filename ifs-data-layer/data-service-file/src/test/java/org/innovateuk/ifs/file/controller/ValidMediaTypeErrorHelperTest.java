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
    public void testPdfOnlyMessage() {
        assertSpecialisedMessageProduced(
                mediaTypeFromCategories(PDF),
                ValidMediaTypeErrorHelper.UNSUPPORTED_MEDIA_TYPE_PDF_ONLY_MESSAGE_KEY
        );
    }

    @Test
    public void testSpreadsheetOnlyMessage() {
        assertSpecialisedMessageProduced(
                mediaTypeFromCategories(SPREADSHEET),
                ValidMediaTypeErrorHelper.UNSUPPORTED_MEDIA_TYPE_SPREADSHEET_ONLY_MESSAGE_KEY
        );
    }

    @Test
    public void testPdfAndSpreadsheetOnlyMessage() {
        assertSpecialisedMessageProduced(
                mediaTypeFromCategories(PDF, SPREADSHEET),
                ValidMediaTypeErrorHelper.UNSUPPORTED_MEDIA_TYPE_PDF_OR_SPREADSHEET_ONLY_MESSAGE_KEY
        );
    }

    @Test
    public void testOpenDocumentOnlyMessage() {
        assertSpecialisedMessageProduced(
                mediaTypeFromCategories(OPEN_DOCUMENT, OPEN_SPREADSHEET),
                ValidMediaTypeErrorHelper.UNSUPPORTED_MEDIA_TYPE_OPEN_DOCUMENT_OR_SPREADSHEET_ONLY_MESSAGE_KEY
        );
    }

    @Test
    public void testNoDirectMatchWithValidMediaTypes() {
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
