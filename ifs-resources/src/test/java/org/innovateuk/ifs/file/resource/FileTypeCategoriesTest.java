package org.innovateuk.ifs.file.resource;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

public class FileTypeCategoriesTest {

    @Test
    public void testGetMediaTypesPdf() {
        assertEquals(FileTypeCategories.PDF.getMediaTypes(), singletonList("application/pdf"));
    }

    @Test
    public void testGetMediaTypesSpreadsheet() {

        List<String> expectedTypes = asList("application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.oasis.opendocument.spreadsheet");

        assertEquals(FileTypeCategories.SPREADSHEET.getMediaTypes(), expectedTypes);
    }
}
