package org.innovateuk.ifs.file.service;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.file.resource.FileTypeCategory;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.transactional.FormInputService;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.file.resource.FileTypeCategory.PDF;
import static org.innovateuk.ifs.file.resource.FileTypeCategory.SPREADSHEET;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ByFormInputMediaTypesGeneratorTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private ByFormInputMediaTypesGenerator generator;

    @Mock
    private FormInputService formInputServiceMock;

    @Test
    public void pdf() {
        assertExpectedMediaTypesForFileTypeCategory(
                asSet(PDF),
                "application/pdf"
        );
    }

    @Test
    public void spreadsheet() {
        assertExpectedMediaTypesForFileTypeCategory(
                asSet(SPREADSHEET),
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.oasis.opendocument.spreadsheet"
        );
    }

    @Test
    public void pdfAndSpreadsheet() {
        assertExpectedMediaTypesForFileTypeCategory(
                asSet(PDF, SPREADSHEET),
                "application/pdf",
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.oasis.opendocument.spreadsheet"
        );
    }

    @Test
    public void noneMatch() {
        assertExpectedMediaTypesForFileTypeCategory(emptySet());
    }

    private void assertExpectedMediaTypesForFileTypeCategory(
            Set<FileTypeCategory> fileTypeCategories,
            String... expectedMediaTypes
    ) {
        FormInputResource formInput = newFormInputResource()
                .withAllowedFileTypes(new HashSet<>(fileTypeCategories))
                .build();
        when(formInputServiceMock.findFormInput(formInput.getId())).thenReturn(serviceSuccess(formInput));

        List<MediaType> mediaTypes = generator.apply(formInput.getId());

        assertThat(simpleMap(mediaTypes, MediaType::toString))
                .containsOnlyOnce(expectedMediaTypes);

        verify(formInputServiceMock).findFormInput(formInput.getId());
    }
}
