package org.innovateuk.ifs.file.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileType;
import org.innovateuk.ifs.file.mapper.FileTypeMapper;
import org.innovateuk.ifs.file.repository.FileTypeRepository;
import org.innovateuk.ifs.file.resource.FileTypeResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests the CompetitionSetupProjectDocumentServiceImpl with mocked repository.
 */
public class FileTypeServiceImplTest extends BaseServiceUnitTest<FileTypeServiceImpl> {

    @Mock
    private FileTypeMapper fileTypeMapperMock;

    @Mock
    private FileTypeRepository fileTypeRepositoryMock;

    @Override
    protected FileTypeServiceImpl supplyServiceUnderTest() {
        return new FileTypeServiceImpl();
    }

    @Test
    public void findOne() {

        Long fileTypeId = 1L;

        FileType fileType = new FileType();
        FileTypeResource fileTypeResource = new FileTypeResource();

        when(fileTypeRepositoryMock.findById(fileTypeId)).thenReturn(Optional.of(fileType));
        when(fileTypeMapperMock.mapToResource(fileType)).thenReturn(fileTypeResource);

        ServiceResult<FileTypeResource> result = service.findOne(fileTypeId);

        assertTrue(result.isSuccess());
        assertEquals(fileTypeResource, result.getSuccess());

        verify(fileTypeRepositoryMock).findById(fileTypeId);
    }

    @Test
    public void findByName() {

        String name = "name";

        FileType fileType = new FileType();
        FileTypeResource fileTypeResource = new FileTypeResource();

        when(fileTypeRepositoryMock.findByName(name)).thenReturn(fileType);
        when(fileTypeMapperMock.mapToResource(fileType)).thenReturn(fileTypeResource);

        ServiceResult<FileTypeResource> result = service.findByName(name);

        assertTrue(result.isSuccess());
        assertEquals(fileTypeResource, result.getSuccess());

        verify(fileTypeRepositoryMock).findByName(name);
    }
}
