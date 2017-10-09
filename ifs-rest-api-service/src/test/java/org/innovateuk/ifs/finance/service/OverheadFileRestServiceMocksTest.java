package org.innovateuk.ifs.finance.service;


import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.OK;

public class OverheadFileRestServiceMocksTest extends BaseRestServiceUnitTest<OverheadFileRestServiceImpl> {

    private static final String overheadFileRestURL = "/overheadcalculation";

    @Test
    public void testAddOverheadFile() {

        String expectedUrl = overheadFileRestURL + "/overheadCalculationDocument?overheadId=123&filename=original.pdf";
        FileEntryResource returnedFileEntry = new FileEntryResource();

        setupFileUploadWithRestResultExpectations(
                expectedUrl, FileEntryResource.class, "New content", "text/plain", 1000L, returnedFileEntry, OK);

        FileEntryResource createdFileEntry =
                service.updateOverheadCalculationFile(123L, "text/plain", 1000L, "original.pdf", "New content".getBytes()).getSuccessObject();

        Assert.assertEquals(returnedFileEntry, createdFileEntry);
    }

    @Test
    public void testGetOverheadFileDetails() {

        String expectedUrl = overheadFileRestURL + "/overheadCalculationDocumentDetails?overheadId=123";
        FileEntryResource returnedFileEntry = new FileEntryResource();

        setupGetWithRestResultExpectations(expectedUrl, FileEntryResource.class, returnedFileEntry, OK);

        FileEntryResource retrievedFileEntry = service.getOverheadFileDetails(123L).getSuccessObject();

        Assert.assertEquals(returnedFileEntry, retrievedFileEntry);
    }

    @Test
    public void testGetOverheadFileDetailsUsingProjectFinanceRowId() {

        String expectedUrl = overheadFileRestURL + "/projectOverheadCalculationDocumentDetails?overheadId=123";
        FileEntryResource returnedFileEntry = new FileEntryResource();

        setupGetWithRestResultExpectations(expectedUrl, FileEntryResource.class, returnedFileEntry, OK);

        FileEntryResource retrievedFileEntry = service.getOverheadFileDetailsUsingProjectFinanceRowId(123L).getSuccessObject();

        Assert.assertEquals(returnedFileEntry, retrievedFileEntry);
    }

    @Test
    public void testGetOverheadFileContent() {

        String expectedUrl = overheadFileRestURL + "/overheadCalculationDocument?overheadId=123";
        ByteArrayResource returnedFileContents = new ByteArrayResource("Retrieved content".getBytes());

        setupGetWithRestResultExpectations(expectedUrl, ByteArrayResource.class, returnedFileContents, OK);

        ByteArrayResource retrievedFileEntry = service.getOverheadFile(123L).getSuccessObject();

        assertEquals(returnedFileContents, retrievedFileEntry);
    }

    @Test
    public void testGetOverheadFileUsingProjectFinanceRowId() {

        String expectedUrl = overheadFileRestURL + "/projectOverheadCalculationDocument?overheadId=123";
        ByteArrayResource returnedFileContents = new ByteArrayResource("Retrieved content".getBytes());

        setupGetWithRestResultExpectations(expectedUrl, ByteArrayResource.class, returnedFileContents, OK);

        ByteArrayResource retrievedFileEntry = service.getOverheadFileUsingProjectFinanceRowId(123L).getSuccessObject();

        assertEquals(returnedFileContents, retrievedFileEntry);
    }

    @Test
    public void testDeleteOverheadFile() {

        String expectedUrl = overheadFileRestURL + "/overheadCalculationDocument?overheadId=123";

        setupDeleteWithRestResultExpectations(expectedUrl);

        service.removeOverheadCalculationFile(123L);

        setupDeleteWithRestResultVerifications(expectedUrl);
    }

    @Override
    protected OverheadFileRestServiceImpl registerRestServiceUnderTest() {
        OverheadFileRestServiceImpl serviceUnderTest = new OverheadFileRestServiceImpl();
        ReflectionTestUtils.setField(serviceUnderTest, "restUrl", overheadFileRestURL);
        return serviceUnderTest;
    }

}
