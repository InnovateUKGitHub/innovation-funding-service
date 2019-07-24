package org.innovateuk.ifs.form.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.transactional.FileEntryService;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import java.io.InputStream;
import java.util.Optional;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class FormInputServiceImplTest  extends BaseServiceUnitTest<FormInputServiceImpl> {

    @Mock
    private FormInputRepository formInputRepository;

    @Mock
    private FileEntryService fileEntryServiceMock;

    @Test
    public void findTemplateFile() throws Exception {
        long formInputId = 1L;
        FileEntry fileEntry = newFileEntry().build();
        FormInput formInput = newFormInput()
                .withFile(fileEntry)
                .build();

        FileEntryResource fileEntryResource = newFileEntryResource().build();
        when(formInputRepository.findById(formInputId)).thenReturn(Optional.of(formInput));
        when(fileEntryServiceMock.findOne(fileEntry.getId())).thenReturn(serviceSuccess(fileEntryResource));

        FileEntryResource response = service.findFile(formInputId).getSuccess();

        assertEquals(fileEntryResource, response);
    }

    @Test
    public void downloadTemplateFile() throws Exception {
        final long formInputId = 1L;
        final long fileId = 2L;
        final FileEntry fileEntry = new FileEntry(fileId, "somefile.pdf", MediaType.APPLICATION_PDF, 1111L);
        FormInput formInput = newFormInput()
                .withFile(fileEntry)
                .build();
        FileEntryResource fileEntryResource = new FileEntryResource();
        fileEntryResource.setId(fileId);

        when(formInputRepository.findById(formInputId)).thenReturn(Optional.of(formInput));
        when(fileEntryServiceMock.findOne(fileId)).thenReturn(serviceSuccess(fileEntryResource));
        final Supplier<InputStream> contentSupplier = () -> null;
        when(fileServiceMock.getFileByFileEntryId(fileEntry.getId())).thenReturn(ServiceResult.serviceSuccess(contentSupplier));

        FileAndContents fileAndContents = service.downloadFile(formInputId).getSuccess();

        assertEquals(fileAndContents.getContentsSupplier(), contentSupplier);
        assertEquals(fileAndContents.getFileEntry(), fileEntryResource);
    }

    @Override
    protected FormInputServiceImpl supplyServiceUnderTest() {
        return new FormInputServiceImpl();
    }
}
