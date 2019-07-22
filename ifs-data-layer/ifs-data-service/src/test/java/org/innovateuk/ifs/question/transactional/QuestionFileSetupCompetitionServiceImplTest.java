package org.innovateuk.ifs.question.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.transactional.FileEntryService;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import java.io.InputStream;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class QuestionFileSetupCompetitionServiceImplTest extends BaseServiceUnitTest<QuestionFileSetupCompetitionServiceImpl> {

    @Mock
    private FormInputRepository formInputRepository;

    @Mock
    private FileEntryService fileEntryServiceMock;

    @Override
    protected QuestionFileSetupCompetitionServiceImpl supplyServiceUnderTest() {
        return new QuestionFileSetupCompetitionServiceImpl();
    }

    @Test
    public void findTemplateFile() throws Exception {
        long questionId = 1L;
        FileEntry fileEntry = newFileEntry().build();
        FormInput formInput = newFormInput()
                .withFile(fileEntry)
                .build();

        FileEntryResource fileEntryResource = newFileEntryResource().build();
        when(formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.APPLICATION, FormInputType.TEMPLATE_DOCUMENT)).thenReturn(formInput);
        when(fileEntryServiceMock.findOne(fileEntry.getId())).thenReturn(serviceSuccess(fileEntryResource));

        FileEntryResource response = service.findTemplateFile(questionId).getSuccess();

        assertEquals(fileEntryResource, response);
    }

    @Test
    public void downloadTemplateFile() throws Exception {
        final long questionId = 1L;
        final long fileId = 2L;
        final FileEntry fileEntry = new FileEntry(fileId, "somefile.pdf", MediaType.APPLICATION_PDF, 1111L);
        FormInput formInput = newFormInput()
                .withFile(fileEntry)
                .build();
        FileEntryResource fileEntryResource = new FileEntryResource();
        fileEntryResource.setId(fileId);

        when(formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.APPLICATION, FormInputType.TEMPLATE_DOCUMENT)).thenReturn(formInput);
        when(fileEntryServiceMock.findOne(fileId)).thenReturn(serviceSuccess(fileEntryResource));
        final Supplier<InputStream> contentSupplier = () -> null;
        when(fileServiceMock.getFileByFileEntryId(fileEntry.getId())).thenReturn(ServiceResult.serviceSuccess(contentSupplier));

        FileAndContents fileAndContents = service.downloadTemplateFile(questionId).getSuccess();

        assertEquals(fileAndContents.getContentsSupplier(), contentSupplier);
        assertEquals(fileAndContents.getFileEntry(), fileEntryResource);
    }


    @Test
    public void deleteTemplateFile_submitted() throws Exception {
        final long questionId = 1L;
        final long fileId = 101L;
        final FileEntry fileEntry = new FileEntry(fileId, "somefile.pdf", MediaType.APPLICATION_PDF, 1111L);
        FormInput formInput = newFormInput()
                .withFile(fileEntry)
                .build();

        when(formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.APPLICATION, FormInputType.TEMPLATE_DOCUMENT)).thenReturn(formInput);
        when(fileServiceMock.deleteFileIgnoreNotFound(fileId)).thenReturn(ServiceResult.serviceSuccess(fileEntry));

        ServiceResult<Void> response = service.deleteTemplateFile(questionId);

        assertTrue(response.isSuccess());
        assertNull(formInput.getFile());
    }
}