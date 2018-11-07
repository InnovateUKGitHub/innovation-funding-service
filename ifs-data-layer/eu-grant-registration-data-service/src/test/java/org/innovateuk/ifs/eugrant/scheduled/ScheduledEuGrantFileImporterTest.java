package org.innovateuk.ifs.eugrant.scheduled;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.innovateuk.ifs.commons.error.CommonErrors.internalServerErrorError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.eugrant.builder.EuGrantResourceBuilder.newEuGrantResource;
import static org.innovateuk.ifs.service.ServiceFailureTestHelper.assertThatServiceFailureIs;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * TODO DW - document this class
 */
@RunWith(MockitoJUnitRunner.class)
public class ScheduledEuGrantFileImporterTest {

    private ScheduledEuGrantFileImporter importer;

    @Mock
    private GrantsFileUploader grantsFileUploaderMock;

    @Mock
    private GrantsFileExtractor grantsFileExtractorMock;

    @Mock
    private GrantsImporter grantsImporterMock;

    @Mock
    private ResultsFileGenerator resultsFileGeneratorMock;

    @Before
    public void setup() {

         importer = new ScheduledEuGrantFileImporter(
                 grantsFileUploaderMock,
                 grantsFileExtractorMock,
                 grantsImporterMock,
                 resultsFileGeneratorMock);
    }

    @Test
    public void importEuGrantsFile() throws IOException {

        File sourceFile = File.createTempFile("temp", "temp");

        List<ServiceResult<EuGrantResource>> extractionResults = asList(
                serviceSuccess(newEuGrantResource().build()),
                serviceFailure(new Error("Could not extract!", BAD_REQUEST)));

        List<ServiceResult<UUID>> importResults = asList(
                serviceSuccess(UUID.randomUUID()),
                serviceFailure(new Error("Could not extract!", BAD_REQUEST)));

        File resultsFile = File.createTempFile("temp", "temp");

        when(grantsFileUploaderMock.getFileIfExists()).thenReturn(serviceSuccess(sourceFile));
        when(grantsFileExtractorMock.processFile(sourceFile)).thenReturn(serviceSuccess(extractionResults));
        when(grantsImporterMock.importGrants(extractionResults)).thenReturn(serviceSuccess(importResults));
        when(resultsFileGeneratorMock.generateResultsFile(importResults, sourceFile)).thenReturn(serviceSuccess(resultsFile));

        ServiceResult<File> result = importer.importEuGrantsFile();

        assertThat(result.isSuccess()).isTrue();

        verify(grantsFileUploaderMock, times(1)).getFileIfExists();
        verify(grantsFileExtractorMock, times(1)).processFile(sourceFile);
        verify(grantsImporterMock, times(1)).importGrants(extractionResults);
        verify(resultsFileGeneratorMock, times(1)).generateResultsFile(importResults, sourceFile);
    }

    @Test
    public void importEuGrantsFileFailureHandling() throws IOException {

        File sourceFile = File.createTempFile("temp", "temp");

        when(grantsFileUploaderMock.getFileIfExists()).thenReturn(serviceSuccess(sourceFile));
        when(grantsFileExtractorMock.processFile(sourceFile)).thenReturn(serviceFailure(internalServerErrorError()));

        ServiceResult<File> result = importer.importEuGrantsFile();

        assertThatServiceFailureIs(result, internalServerErrorError());

        verify(grantsFileUploaderMock, times(1)).getFileIfExists();
        verify(grantsFileExtractorMock, times(1)).processFile(sourceFile);
        verify(grantsImporterMock, never()).importGrants(any());
        verify(resultsFileGeneratorMock, never()).generateResultsFile(any(), any());
    }
}
