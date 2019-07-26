package org.innovateuk.ifs.file.resource;

import org.junit.Test;

import java.util.Set;

import static java.util.Collections.singleton;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.innovateuk.ifs.file.resource.FileTypeCategory.PDF;
import static org.innovateuk.ifs.file.resource.FileTypeCategory.SPREADSHEET;
import static org.junit.Assert.assertEquals;

public class FileTypeCategoryTest {

    @Test
    public void testGetMediaTypesPdf() {
        assertEquals(PDF.getMimeTypes(), singleton("application/pdf"));
    }

    @Test
    public void testGetMediaTypesSpreadsheet() {

        Set<String> expectedTypes = asSet("application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.oasis.opendocument.spreadsheet");

        assertEquals(SPREADSHEET.getMimeTypes(), expectedTypes);
    }
}
