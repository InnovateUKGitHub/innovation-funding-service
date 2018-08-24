package org.innovateuk.ifs.organisation.service;

import org.apache.commons.io.FileUtils;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import static java.lang.Thread.currentThread;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class ScheduledJesOrganisationListImporterFileDownloaderTest {

    @Test
    public void downloadFile() throws URISyntaxException, IOException {

        ScheduledJesOrganisationListImporterFileDownloader fileDownloader = new ScheduledJesOrganisationListImporterFileDownloader();
        URL dummyFileUrl = currentThread().getContextClassLoader().getResource("test-jes-download.csv");
        ServiceResult<File> fileDownloadResult = fileDownloader.downloadFile(dummyFileUrl, 5000, 500);

        assertThat(fileDownloadResult.isSuccess()).isTrue();

        File dummyFile = fileDownloadResult.getSuccess();

        List<String> fileContents = FileUtils.readLines(dummyFile, Charset.defaultCharset());

        assertThat(fileContents).containsExactly(
                "Organisation name",
                "AB Agri Ltd",
                "Aberystwyth University",
                "Abriachan Forest Trust");
    }

    @Test
    public void downloadFileWhenFileNotFound() throws URISyntaxException, IOException {

        ScheduledJesOrganisationListImporterFileDownloader fileDownloader = new ScheduledJesOrganisationListImporterFileDownloader();

        String nonExistentFileUrl = File.createTempFile("jestest", "jestest").getAbsolutePath() + "does-not-exist";
        URL dummyFileUrl = new File(nonExistentFileUrl).toURI().toURL();

        ServiceResult<File> fileDownloadResult = fileDownloader.downloadFile(dummyFileUrl, 5000, 500);

        assertThat(fileDownloadResult.isFailure()).isTrue();

        List<Error> errors = fileDownloadResult.getFailure().getErrors();
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getStatusCode()).isEqualTo(BAD_REQUEST);
    }
}
