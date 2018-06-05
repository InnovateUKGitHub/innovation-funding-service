package org.innovateuk.ifs.file.resource;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.file.resource.FileTypeCategory.PDF;
import static org.innovateuk.ifs.file.resource.FileTypeCategory.SPREADSHEET;
import static org.junit.Assert.assertEquals;

public class FileTypeCategoryTest {

    @Test
    public void testGetMediaTypesPdf() {
        assertEquals(PDF.getMediaTypes(), singletonList("application/pdf"));
    }

    @Test
    public void testGetMediaTypesSpreadsheet() {

        List<String> expectedTypes = asList("application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.oasis.opendocument.spreadsheet");

        assertEquals(SPREADSHEET.getMediaTypes(), expectedTypes);
    }
}
