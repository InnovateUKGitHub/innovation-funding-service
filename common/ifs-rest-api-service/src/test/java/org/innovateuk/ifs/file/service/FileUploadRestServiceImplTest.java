package org.innovateuk.ifs.file.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.CREATED;

public class FileUploadRestServiceImplTest extends BaseRestServiceUnitTest<FileUploadRestServiceImpl> {

    private static final String fileUploadRestURL = "/external-system-files";

    @Override
    protected FileUploadRestServiceImpl registerRestServiceUnderTest() {
        return new FileUploadRestServiceImpl();
    }

    @Test
    public void uploadFile() {
        String fileName = "original.csv";
        String url = String.format("%s/upload-file?fileType=%s&fileName=%s", fileUploadRestURL, "AssessmentOnly", fileName);

        String fileContentString = "12345678901234567";
        byte[] fileContent = fileContentString.getBytes();

        FileEntryResource response = new FileEntryResource();

        setupFileUploadWithRestResultExpectations(url, FileEntryResource.class,
                fileContentString, "application/csv", 1000, response, CREATED);

        RestResult<FileEntryResource> result = service.uploadFile("AssessmentOnly", "application/csv",
                        1000, fileName, fileContent);

        assertTrue(result.isSuccess());
        assertEquals(response, result.getSuccess());
    }
}
