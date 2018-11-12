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
import static org.assertj.core.api.Assertions.assertThat;
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
    private GrantsFileHandler grantsFileUploaderMock;

    @Mock
    private GrantsRecordExtractor grantsFileExtractorMock;

    @Mock
    private GrantResourceSaver grantSaverMock;

    @Mock
    private GrantResultsFileGenerator resultsFileGeneratorMock;

    @Before
    public void setup() {

         importer = new ScheduledEuGrantFileImporter(
                 grantsFileUploaderMock,
                 grantsFileExtractorMock,
                 grantSaverMock,
                 resultsFileGeneratorMock);
    }

    @Test
    public void importEuGrantsFile() throws IOException {

        File sourceFile = File.createTempFile("temp", "temp");

        List<ServiceResult<EuGrantResource>> extractionResults = asList(
                serviceSuccess(newEuGrantResource().build()),
                serviceFailure(new Error("Could not extract!", BAD_REQUEST)));

        EuGrantResource saveGrantResults = newEuGrantResource().withId(UUID.randomUUID()).build();

        File resultsFile = File.createTempFile("temp", "temp");

        when(grantsFileUploaderMock.getSourceFileIfExists()).thenReturn(serviceSuccess(sourceFile));
        when(grantsFileExtractorMock.processFile(sourceFile)).thenReturn(serviceSuccess(extractionResults));

        EuGrantResource successfullyExtractedGrant = extractionResults.get(0).getSuccess();
        when(grantSaverMock.saveGrant(successfullyExtractedGrant)).thenReturn(serviceSuccess(saveGrantResults));

        List<ServiceResult<EuGrantResource>> combinedListOfSuccessesAndFailures = asList(serviceSuccess(saveGrantResults), extractionResults.get(1));
        when(resultsFileGeneratorMock.generateResultsFile(combinedListOfSuccessesAndFailures, sourceFile)).thenReturn(serviceSuccess(resultsFile));

        ServiceResult<File> result = importer.importEuGrantsFile();

        assertThat(result.isSuccess()).isTrue();

        verify(grantsFileUploaderMock, times(1)).getSourceFileIfExists();
        verify(grantsFileExtractorMock, times(1)).processFile(sourceFile);
        verify(grantSaverMock, times(1)).saveGrant(successfullyExtractedGrant);
        verify(resultsFileGeneratorMock, times(1)).generateResultsFile(combinedListOfSuccessesAndFailures, sourceFile);
    }

    @Test
    public void importEuGrantsFileFailureHandling() throws IOException {

        File sourceFile = File.createTempFile("temp", "temp");

        when(grantsFileUploaderMock.getSourceFileIfExists()).thenReturn(serviceSuccess(sourceFile));
        when(grantsFileExtractorMock.processFile(sourceFile)).thenReturn(serviceFailure(internalServerErrorError()));

        ServiceResult<File> result = importer.importEuGrantsFile();

        assertThatServiceFailureIs(result, internalServerErrorError());

        verify(grantsFileUploaderMock, times(1)).getSourceFileIfExists();
        verify(grantsFileExtractorMock, times(1)).processFile(sourceFile);
        verify(grantSaverMock, never()).saveGrant(any());
        verify(resultsFileGeneratorMock, never()).generateResultsFile(any(), any());
    }
}
