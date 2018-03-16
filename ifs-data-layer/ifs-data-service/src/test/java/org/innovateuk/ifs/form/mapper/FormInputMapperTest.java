package org.innovateuk.ifs.form.mapper;

import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.file.resource.FileTypeCategory;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FormInputMapperTest {

    @Mock
    private FormInputRepository formInputRepositoryMock;

    @Mock
    private CompetitionMapper competitionMapper;

    @Mock
    private QuestionMapper questionMapper;

    @InjectMocks
    private FormInputMapperImpl formInputMapperImpl;

    @Test
    public void mapToDomain() {
        FormInput formInput = newFormInput()
                .withId(1L)
                .build();
        when(formInputRepositoryMock.findOne(any())).thenReturn(formInput);

        FormInputResource formInputResource = newFormInputResource()
                .withAllowedFileTypes(singletonList(FileTypeCategory.PDF))
                .build();

        FormInput result = formInputMapperImpl.mapToDomain(formInputResource);

        assertEquals(result.getAllowedFileTypes(), "PDF");
    }

    @Test
    public void mapToDomain_fileTypeCategoryShouldMapToDisplayName() {
        FormInput formInput = newFormInput()
                .withId(1L)
                .build();
        when(formInputRepositoryMock.findOne(any())).thenReturn(formInput);

        FormInputResource formInputResource = newFormInputResource()
                .withAllowedFileTypes(singletonList(FileTypeCategory.SPREADSHEET))
                .build();

        FormInput result = formInputMapperImpl.mapToDomain(formInputResource);

        assertEquals(result.getAllowedFileTypes(), "Spreadsheet");
    }

    @Test
    public void mapToDomain_multipleFileTypeCategoriesShouldBeConcatenated() {
        FormInput formInput = newFormInput()
                .withId(1L)
                .build();
        when(formInputRepositoryMock.findOne(any())).thenReturn(formInput);

        FormInputResource formInputResource = newFormInputResource()
                .withAllowedFileTypes(asList(FileTypeCategory.SPREADSHEET, FileTypeCategory.PDF))
                .build();

        FormInput result = formInputMapperImpl.mapToDomain(formInputResource);

        assertEquals(result.getAllowedFileTypes(), "Spreadsheet,PDF");
    }

    @Test
    public void mapToResource() {
        FormInput formInput = newFormInput()
                .withAllowedFileTypes("PDF")
                .withId(1L)
                .build();

        FormInputResource result = formInputMapperImpl.mapToResource(formInput);

        assertTrue(result.getAllowedFileTypes().contains(FileTypeCategory.PDF));
    }

    @Test
    public void mapToResource_displayNameShouldMapToFileCategory() {
        FormInput formInput = newFormInput()
                .withAllowedFileTypes("Spreadsheet")
                .withId(1L)
                .build();

        FormInputResource result = formInputMapperImpl.mapToResource(formInput);

        assertTrue(result.getAllowedFileTypes().contains(FileTypeCategory.SPREADSHEET));
    }

    @Test
    public void mapToResource_concatenatedFileTypesShouldBeMappedToCollection() {
        FormInput formInput = newFormInput()
                .withAllowedFileTypes("PDF,Spreadsheet")
                .withId(1L)
                .build();

        FormInputResource result = formInputMapperImpl.mapToResource(formInput);

        assertTrue(result.getAllowedFileTypes().contains(FileTypeCategory.PDF));
        assertTrue(result.getAllowedFileTypes().contains(FileTypeCategory.SPREADSHEET));
    }

    @Test
    public void mapToResource_emptyFileTypeStringShouldResultInEmptyList() {
        FormInput formInput = newFormInput()
                .withAllowedFileTypes("")
                .withId(1L)
                .build();

        FormInputResource result = formInputMapperImpl.mapToResource(formInput);

        assertTrue(result.getAllowedFileTypes().isEmpty());
    }

    @Test
    public void mapToResource_nullFileTypeStringShouldResultInEmptyList() {
        FormInput formInput = newFormInput()
                .withId(1L)
                .build();

        FormInputResource result = formInputMapperImpl.mapToResource(formInput);

        assertTrue(result.getAllowedFileTypes().isEmpty());
    }
}