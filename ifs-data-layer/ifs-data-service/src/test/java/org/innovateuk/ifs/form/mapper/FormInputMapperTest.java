package org.innovateuk.ifs.form.mapper;

import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.file.resource.FileTypeCategory.PDF;
import static org.innovateuk.ifs.file.resource.FileTypeCategory.SPREADSHEET;
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
                .withAllowedFileTypes(new HashSet<>(singletonList(PDF)))
                .build();

        FormInput result = formInputMapperImpl.mapToDomain(formInputResource);

        assertEquals(result.getAllowedFileTypes(), new HashSet<>(singletonList(PDF)));
    }

    @Test
    public void mapToDomain_fileTypeCategoryShouldMapToDisplayName() {
        FormInput formInput = newFormInput()
                .withId(1L)
                .build();
        when(formInputRepositoryMock.findOne(any())).thenReturn(formInput);

        FormInputResource formInputResource = newFormInputResource()
                .withAllowedFileTypes(new HashSet<>(singletonList(SPREADSHEET)))
                .build();

        FormInput result = formInputMapperImpl.mapToDomain(formInputResource);

        assertEquals(result.getAllowedFileTypes(), new HashSet<>(singletonList(SPREADSHEET)));
    }

    @Test
    public void mapToResource() {
        FormInput formInput = newFormInput()
                .withAllowedFileTypes(new HashSet<>(singletonList(PDF)))
                .withId(1L)
                .build();

        FormInputResource result = formInputMapperImpl.mapToResource(formInput);

        assertTrue(result.getAllowedFileTypes().contains(PDF));
    }

    @Test
    public void mapToResource_concatenatedFileTypesShouldBeMappedToCollection() {
        FormInput formInput = newFormInput()
                .withAllowedFileTypes(new HashSet<>(asList(PDF, SPREADSHEET)))
                .withId(1L)
                .build();

        FormInputResource result = formInputMapperImpl.mapToResource(formInput);

        assertTrue(result.getAllowedFileTypes().contains(PDF));
        assertTrue(result.getAllowedFileTypes().contains(SPREADSHEET));
    }

    @Test
    public void mapToResource_emptyFileTypeStringShouldResultInEmptyList() {
        FormInput formInput = newFormInput()
                .withAllowedFileTypes(emptySet())
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