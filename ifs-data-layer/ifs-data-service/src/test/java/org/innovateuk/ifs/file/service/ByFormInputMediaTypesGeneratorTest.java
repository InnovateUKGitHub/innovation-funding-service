package org.innovateuk.ifs.file.service;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.file.resource.FileTypeCategories;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.http.MediaType;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ByFormInputMediaTypesGeneratorTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private ByFormInputMediaTypesGenerator generator;

    @Test
    public void testPdf() {
        assertExpectedMediaTypesForFileTypeCategories(singletonList(FileTypeCategories.PDF), "application/pdf");
    }

    @Test
    public void testSpreadsheet() {
        assertExpectedMediaTypesForFileTypeCategories(singletonList(FileTypeCategories.SPREADSHEET),
                "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.oasis.opendocument.spreadsheet");
    }

    @Test
    public void testPdfAndSpreadsheet() {
        assertExpectedMediaTypesForFileTypeCategories(asList(FileTypeCategories.PDF, FileTypeCategories.SPREADSHEET),
                "application/pdf", "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.oasis.opendocument.spreadsheet");
    }

    @Test
    public void testNone() {
        assertExpectedMediaTypesForFileTypeCategories(emptyList());
    }

    private void assertExpectedMediaTypesForFileTypeCategories(List<FileTypeCategories> fileTypeCategories, String... expectedMediaTypes) {

        FormInputResource formInput = newFormInputResource().
                withAllowedFileTypes(fileTypeCategories).
                build();

        when(formInputServiceMock.findFormInput(formInput.getId())).thenReturn(serviceSuccess(formInput));

        List<MediaType> mediaTypes = generator.apply(formInput.getId());
        assertArrayEquals(expectedMediaTypes, simpleMap(mediaTypes, MediaType::toString).toArray());

        verify(formInputServiceMock).findFormInput(formInput.getId());
    }
}
