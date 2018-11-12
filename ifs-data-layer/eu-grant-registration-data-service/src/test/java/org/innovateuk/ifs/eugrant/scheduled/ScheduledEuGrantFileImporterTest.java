package org.innovateuk.ifs.eugrant.scheduled;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.security.WebUserSecuritySetter;
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
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.commons.error.CommonErrors.internalServerErrorError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
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

    @Mock
    private GrantsImportResultHandler grantsImportResultHandlerMock;

    @Mock
    private WebUserSecuritySetter webUserSecuritySetter;

    @Before
    public void setup() {

         importer = new ScheduledEuGrantFileImporter(
                 grantsFileUploaderMock,
                 grantsFileExtractorMock,
                 grantSaverMock,
                 resultsFileGeneratorMock,
                 grantsImportResultHandlerMock,
                 webUserSecuritySetter);
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

        importer.importEuGrantsFile();

        verify(grantsImportResultHandlerMock).recordResult(createLambdaMatcher(result -> {
            assertThat(result.isSuccess()).isTrue();
            Pair<File, List<ServiceResult<EuGrantResource>>> success = result.getSuccess();
            assertThat(success.getLeft()).isEqualTo(resultsFile);
            assertThat(success.getRight()).isEqualTo(combinedListOfSuccessesAndFailures);
        }));

        verify(webUserSecuritySetter, times(1)).setWebUser();
        verify(grantsFileUploaderMock, times(1)).getSourceFileIfExists();
        verify(grantsFileExtractorMock, times(1)).processFile(sourceFile);
        verify(grantSaverMock, times(1)).saveGrant(successfullyExtractedGrant);
        verify(resultsFileGeneratorMock, times(1)).generateResultsFile(combinedListOfSuccessesAndFailures, sourceFile);
        verify(webUserSecuritySetter, times(1)).clearWebUser();
    }

    @Test
    public void importEuGrantsFileFailureHandling() throws IOException {

        File sourceFile = File.createTempFile("temp", "temp");

        when(grantsFileUploaderMock.getSourceFileIfExists()).thenReturn(serviceSuccess(sourceFile));
        when(grantsFileExtractorMock.processFile(sourceFile)).thenReturn(serviceFailure(internalServerErrorError()));

        importer.importEuGrantsFile();

        verify(grantsImportResultHandlerMock).recordResult(createLambdaMatcher(result -> {
            assertThatServiceFailureIs(result, internalServerErrorError());
        }));

        verify(webUserSecuritySetter, times(1)).setWebUser();
        verify(grantsFileUploaderMock, times(1)).getSourceFileIfExists();
        verify(grantsFileExtractorMock, times(1)).processFile(sourceFile);
        verify(grantSaverMock, never()).saveGrant(any());
        verify(resultsFileGeneratorMock, never()).generateResultsFile(any(), any());
        verify(webUserSecuritySetter, times(1)).clearWebUser();
    }

    @Test
    public void importEuGrantsFileNoFileToProcess() throws IOException {

        when(grantsFileUploaderMock.getSourceFileIfExists()).thenReturn(
                serviceFailure(notFoundError(File.class, "/tmp/some/path.csv")));

        importer.importEuGrantsFile();

        verify(grantsFileUploaderMock, times(1)).getSourceFileIfExists();

        verify(grantsImportResultHandlerMock, never()).recordResult(any());
        verify(webUserSecuritySetter, never()).setWebUser();
        verify(grantsFileExtractorMock, never()).processFile(any());
        verify(grantSaverMock, never()).saveGrant(any());
        verify(resultsFileGeneratorMock, never()).generateResultsFile(any(), any());
        verify(webUserSecuritySetter, never()).clearWebUser();
    }
}
