package org.innovateuk.ifs.file.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.file.resource.FileTypeResource;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FileTypeRestServiceImplTest extends BaseRestServiceUnitTest<FileTypeRestServiceImpl> {

    private static final String fileTypeRestURL = "/file/file-type";

    @Override
    protected FileTypeRestServiceImpl registerRestServiceUnderTest() {
        return new FileTypeRestServiceImpl();
    }

    @Test
    public void findOne() {

        long fileTypeId = 1L;

        FileTypeResource responseBody = new FileTypeResource();

        setupGetWithRestResultExpectations(String.format("%s/%s", fileTypeRestURL, fileTypeId), FileTypeResource.class, responseBody);

        FileTypeResource response = service.findOne(fileTypeId).getSuccess();
        assertNotNull(response);
        assertEquals(responseBody, response);

        setupGetWithRestResultVerifications(String.format("%s/%s", fileTypeRestURL, fileTypeId), null, FileTypeResource.class);
    }

    @Test
    public void findByName() {

        String name = "name";

        FileTypeResource responseBody = new FileTypeResource();

        setupGetWithRestResultExpectations(String.format("%s/find-by-name/%s", fileTypeRestURL, name), FileTypeResource.class, responseBody);

        FileTypeResource response = service.findByName(name).getSuccess();
        assertNotNull(response);
        assertEquals(responseBody, response);

        setupGetWithRestResultVerifications(String.format("%s/find-by-name/%s", fileTypeRestURL, name), null, FileTypeResource.class);
    }
}

