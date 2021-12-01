package org.innovateuk.ifs.organisation.service;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.domain.Academic;
import org.innovateuk.ifs.organisation.repository.AcademicRepository;
import org.innovateuk.ifs.transactional.TransactionalHelper;
import org.junit.Test;
import org.mockito.Mock;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.service.ServiceFailureTestHelper.assertThatServiceFailureIs;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

public class ScheduledJesOrganisationListImporterTest extends BaseUnitTestMocksTest {

    private static final String JES_FILE_STRING = "file:///tmp/jes-download-file.csv";
    private static final String ARCHIVE_FILE_STRING = "file:///archived-jes-list.csv";
    private static final URL JES_FILE_URL;
    private static final URL ARCHIVE_FILE_URL;
    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 4000;

    static {
        try {
            JES_FILE_URL = new URL(JES_FILE_STRING);
            ARCHIVE_FILE_URL = new URL(ARCHIVE_FILE_STRING);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Mock
    private AcademicRepository academicRepositoryMock;

    @Mock
    private ScheduledJesOrganisationListImporterFileDownloader fileDownloaderMock;

    @Mock
    private ScheduledJesOrganisationListImporterOrganisationExtractor organisationExtractorMock;

    @Mock
    private TransactionalHelper transactionalHelperMock;

    @Test
    public void importJesList() throws IOException {

        ScheduledJesOrganisationListImporter job = new ScheduledJesOrganisationListImporter(
                academicRepositoryMock,
                fileDownloaderMock,
                organisationExtractorMock,
                transactionalHelperMock,
                CONNECTION_TIMEOUT,
                READ_TIMEOUT,
                true,
                JES_FILE_STRING,
                ARCHIVE_FILE_STRING,
                true);

        List<String> expectedDownloadedOrganisations = asList("Org 1", "Org 2", "Org 3");
        List<Academic> expectedAcademicsToSave = simpleMap(expectedDownloadedOrganisations, Academic::new);
        File downloadedFile = File.createTempFile("jestest", "jestest");

        when(fileDownloaderMock.jesSourceFileExists(JES_FILE_URL)).thenReturn(true);
        when(fileDownloaderMock.copyJesSourceFile(JES_FILE_URL, CONNECTION_TIMEOUT, READ_TIMEOUT)).thenReturn(serviceSuccess(downloadedFile));
        when(fileDownloaderMock.archiveSourceFile(JES_FILE_URL, ARCHIVE_FILE_URL)).thenReturn(serviceSuccess());
        when(organisationExtractorMock.extractOrganisationsFromFile(downloadedFile)).thenReturn(serviceSuccess(expectedDownloadedOrganisations));

        ServiceResult<List<String>> result = job.importJesList();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getSuccess()).isEqualTo(expectedDownloadedOrganisations);

        verify(fileDownloaderMock, times(2)).jesSourceFileExists(JES_FILE_URL);
        verify(fileDownloaderMock, times(1)).copyJesSourceFile(JES_FILE_URL, CONNECTION_TIMEOUT, READ_TIMEOUT);
        verify(fileDownloaderMock, times(1)).archiveSourceFile(JES_FILE_URL, ARCHIVE_FILE_URL);
        verify(organisationExtractorMock, times(1)).extractOrganisationsFromFile(downloadedFile);
        verify(academicRepositoryMock, times(1)).deleteAll();
        verify(academicRepositoryMock, times(1)).saveAll(expectedAcademicsToSave);
    }

    @Test
    public void importJesListWhenImportIsDisabled() {

        ScheduledJesOrganisationListImporter job = new ScheduledJesOrganisationListImporter(
                academicRepositoryMock,
                fileDownloaderMock,
                organisationExtractorMock,
                transactionalHelperMock,
                CONNECTION_TIMEOUT,
                READ_TIMEOUT,
                false,
                JES_FILE_STRING,
                ARCHIVE_FILE_STRING,
                true);

        ServiceResult<List<String>> result = job.importJesList();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getSuccess()).isEqualTo(emptyList());

        verifyZeroInteractions(fileDownloaderMock, organisationExtractorMock, academicRepositoryMock);
    }

    @Test
    public void importJesListWhenDownloadFileFails() throws IOException {

        ScheduledJesOrganisationListImporter job = new ScheduledJesOrganisationListImporter(
                academicRepositoryMock,
                fileDownloaderMock,
                organisationExtractorMock,
                transactionalHelperMock,
                CONNECTION_TIMEOUT,
                READ_TIMEOUT,
                true,
                JES_FILE_STRING,
                ARCHIVE_FILE_STRING,
                true);

        ServiceResult<File> downloadFileFailure = serviceFailure(new Error("Service was unavailable", SERVICE_UNAVAILABLE));
        when(fileDownloaderMock.jesSourceFileExists(JES_FILE_URL)).thenReturn(true);
        when(fileDownloaderMock.copyJesSourceFile(JES_FILE_URL, CONNECTION_TIMEOUT, READ_TIMEOUT)).thenReturn(downloadFileFailure);

        ServiceResult<List<String>> result = job.importJesList();

        assertThatServiceFailureIs(result, new Error("Service was unavailable", SERVICE_UNAVAILABLE));

        verify(fileDownloaderMock, times(1)).jesSourceFileExists(JES_FILE_URL);
        verify(fileDownloaderMock, times(1)).copyJesSourceFile(JES_FILE_URL, CONNECTION_TIMEOUT, READ_TIMEOUT);
        verify(fileDownloaderMock, never()).archiveSourceFile(JES_FILE_URL, ARCHIVE_FILE_URL);
        verifyZeroInteractions(organisationExtractorMock, academicRepositoryMock);
    }

    @Test
    public void importJesListWhenExtractOrganisationsFails() throws IOException {

        ScheduledJesOrganisationListImporter job = new ScheduledJesOrganisationListImporter(
                academicRepositoryMock,
                fileDownloaderMock,
                organisationExtractorMock,
                transactionalHelperMock,
                CONNECTION_TIMEOUT,
                READ_TIMEOUT,
                true,
                JES_FILE_STRING,
                ARCHIVE_FILE_STRING,
                true);

        File downloadedFile = File.createTempFile("jestest", "jestest");

        when(fileDownloaderMock.jesSourceFileExists(JES_FILE_URL)).thenReturn(true);
        when(fileDownloaderMock.copyJesSourceFile(JES_FILE_URL, CONNECTION_TIMEOUT, READ_TIMEOUT)).thenReturn(serviceSuccess(downloadedFile));
        when(fileDownloaderMock.archiveSourceFile(JES_FILE_URL, ARCHIVE_FILE_URL)).thenReturn(serviceSuccess());
        when(organisationExtractorMock.extractOrganisationsFromFile(downloadedFile)).thenReturn(serviceFailure(new Error("Extract fails ", BAD_REQUEST)));

        ServiceResult<List<String>> result = job.importJesList();

        assertThatServiceFailureIs(result, new Error("Extract fails ", BAD_REQUEST));

        verify(fileDownloaderMock, times(2)).jesSourceFileExists(JES_FILE_URL);
        verify(fileDownloaderMock, times(1)).copyJesSourceFile(JES_FILE_URL, CONNECTION_TIMEOUT, READ_TIMEOUT);
        verify(fileDownloaderMock, times(1)).archiveSourceFile(JES_FILE_URL, ARCHIVE_FILE_URL);
        verify(organisationExtractorMock, times(1)).extractOrganisationsFromFile(downloadedFile);

        verifyZeroInteractions(academicRepositoryMock);
    }
}
