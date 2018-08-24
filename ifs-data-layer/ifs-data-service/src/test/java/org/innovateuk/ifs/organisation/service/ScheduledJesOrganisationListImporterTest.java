package org.innovateuk.ifs.organisation.service;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.domain.Academic;
import org.innovateuk.ifs.organisation.repository.AcademicRepository;
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
import static org.innovateuk.ifs.commons.service.ServiceFailureTestHelper.assertThatServiceFailureIs;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

public class ScheduledJesOrganisationListImporterTest extends BaseUnitTestMocksTest {

    private static final String JES_FILE_STRING = "http://jes.download.url.example.com";
    private static final URL JES_FILE_URL;
    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 4000;

    static {
        try {
            JES_FILE_URL = new URL(JES_FILE_STRING);
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

    @Test
    public void importJesList() throws IOException {

        ScheduledJesOrganisationListImporter job = new ScheduledJesOrganisationListImporter(
                academicRepositoryMock,
                fileDownloaderMock,
                organisationExtractorMock,
                CONNECTION_TIMEOUT,
                READ_TIMEOUT,
                true,
                JES_FILE_STRING);

        List<String> expectedDownloadedOrganisations = asList("Org 1", "Org 2", "Org 3");
        List<Academic> expectedAcademicsToSave = simpleMap(expectedDownloadedOrganisations, Academic::new);
        File downloadedFile = File.createTempFile("jestest", "jestest");

        when(fileDownloaderMock.downloadFile(JES_FILE_URL, CONNECTION_TIMEOUT, READ_TIMEOUT)).thenReturn(serviceSuccess(downloadedFile));
        when(organisationExtractorMock.extractOrganisationsFromFile(downloadedFile)).thenReturn(serviceSuccess(expectedDownloadedOrganisations));

        ServiceResult<List<String>> result = job.importJesList();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getSuccess()).isEqualTo(expectedDownloadedOrganisations);

        verify(fileDownloaderMock, times(1)).downloadFile(JES_FILE_URL, CONNECTION_TIMEOUT, READ_TIMEOUT);
        verify(organisationExtractorMock, times(1)).extractOrganisationsFromFile(downloadedFile);
        verify(academicRepositoryMock, times(1)).deleteAll();
        verify(academicRepositoryMock, times(1)).save(expectedAcademicsToSave);
    }

    @Test
    public void importJesListWhenImportIsDisabled() {

        ScheduledJesOrganisationListImporter job = new ScheduledJesOrganisationListImporter(
                academicRepositoryMock,
                fileDownloaderMock,
                organisationExtractorMock,
                CONNECTION_TIMEOUT,
                READ_TIMEOUT,
                false,
                JES_FILE_STRING);

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
                CONNECTION_TIMEOUT,
                READ_TIMEOUT,
                true,
                JES_FILE_STRING);

        ServiceResult<File> downloadFileFailure = serviceFailure(new Error("Service was unavailable", SERVICE_UNAVAILABLE));
        when(fileDownloaderMock.downloadFile(JES_FILE_URL, CONNECTION_TIMEOUT, READ_TIMEOUT)).thenReturn(downloadFileFailure);

        ServiceResult<List<String>> result = job.importJesList();

        assertThatServiceFailureIs(result, new Error("Service was unavailable", SERVICE_UNAVAILABLE));

        verify(fileDownloaderMock, times(1)).downloadFile(JES_FILE_URL, CONNECTION_TIMEOUT, READ_TIMEOUT);
        verifyZeroInteractions(organisationExtractorMock, academicRepositoryMock);
    }

    @Test
    public void importJesListWhenExtractOrganisationsFails() throws IOException {

        ScheduledJesOrganisationListImporter job = new ScheduledJesOrganisationListImporter(
                academicRepositoryMock,
                fileDownloaderMock,
                organisationExtractorMock,
                CONNECTION_TIMEOUT,
                READ_TIMEOUT,
                true,
                JES_FILE_STRING);

        File downloadedFile = File.createTempFile("jestest", "jestest");

        when(fileDownloaderMock.downloadFile(JES_FILE_URL, CONNECTION_TIMEOUT, READ_TIMEOUT)).thenReturn(serviceSuccess(downloadedFile));
        when(organisationExtractorMock.extractOrganisationsFromFile(downloadedFile)).thenReturn(serviceFailure(new Error("Extract fails ", BAD_REQUEST)));

        ServiceResult<List<String>> result = job.importJesList();

        assertThatServiceFailureIs(result, new Error("Extract fails ", BAD_REQUEST));

        verify(fileDownloaderMock, times(1)).downloadFile(JES_FILE_URL, CONNECTION_TIMEOUT, READ_TIMEOUT);
        verify(organisationExtractorMock, times(1)).extractOrganisationsFromFile(downloadedFile);

        verifyZeroInteractions(academicRepositoryMock);
    }
}
