package org.innovateuk.ifs.file.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.junit.Test;
import org.mockito.Mock;

import java.io.InputStream;
import java.util.function.Supplier;

import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

public class FileUploadServiceImplTest extends BaseServiceUnitTest<FileUploadServiceImpl> {

    @Mock
    private BuildDataFromFile buildDataFromFileMock;

    @Override
    protected FileUploadServiceImpl supplyServiceUnderTest() {
        return new FileUploadServiceImpl();
    }

    @Test
    public void uploadFile() {
        FileEntryResource createdResource = newFileEntryResource()
                .build();
        Supplier<InputStream> inputStreamSupplier = () -> null;

        doNothing().when(buildDataFromFileMock).buildFromFile(inputStreamSupplier.get());

        ServiceResult<FileEntryResource> response = service.uploadFile("fileType", createdResource, inputStreamSupplier);

        assertTrue(response.isSuccess());

        verify(buildDataFromFileMock).buildFromFile(inputStreamSupplier.get());
    }
}
