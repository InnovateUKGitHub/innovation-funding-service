package org.innovateuk.ifs.file.resource;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import java.util.Set;

import static java.util.Collections.singleton;
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
        Set<String> expectedTypes = ImmutableSet.of("application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.oasis.opendocument.spreadsheet");
        assertEquals(SPREADSHEET.getMimeTypes(), expectedTypes);
    }
}
