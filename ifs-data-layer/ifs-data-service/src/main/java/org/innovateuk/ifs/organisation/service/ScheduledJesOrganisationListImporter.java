package org.innovateuk.ifs.organisation.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceFailure;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.domain.Academic;
import org.innovateuk.ifs.organisation.repository.AcademicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * TODO DW - document this class
 */
@Component
public class ScheduledJesOrganisationListImporter {

    private static final Log LOG = LogFactory.getLog(ScheduledJesOrganisationListImporter.class);

    @Value("${ifs.data.service.jes.organisation.importer.connection.timeout.millis}")
    private int connectionTimeoutMillis = 10000;

    @Value("${ifs.data.service.jes.organisation.importer.read.timeout.millis}")
    private int readTimeoutMillis = 10000;

    private AcademicRepository academicRepository;

    @Autowired
    ScheduledJesOrganisationListImporter(@Autowired AcademicRepository academicRepository,
                                         @Value("${ifs.data.service.jes.organisation.importer.connection.timeout.millis}") int connectionTimeoutMillis,
                                         @Value("${ifs.data.service.jes.organisation.importer.read.timeout.millis}") int readTimeoutMillis) {

        this.academicRepository = academicRepository;
        this.connectionTimeoutMillis = connectionTimeoutMillis;
        this.readTimeoutMillis = readTimeoutMillis;
    }

    @Scheduled(cron = "${ifs.data.service.jes.organisation.importer.cron.expression}")
    @Transactional
    public void importJesList() {

        LOG.info("Importing Je-S organisation list...");

        ServiceResult<List<String>> downloadResult = getJesFileDownloadUrl().
                andOnSuccess(jesFileToDownload -> downloadFile(jesFileToDownload).
                andOnSuccess(downloadedFile -> readDownloadedFile(downloadedFile).
                andOnSuccessDo(lines -> lines.forEach(System.out::println)))).
                andOnSuccessDo(lines -> deleteExistingAcademicEntries()).
                andOnSuccessDo(this::importNewAcademicEntries);

        downloadResult.handleSuccessOrFailureNoReturn(
                this::logFailure,
                this::logSuccess);
    }

    private void importNewAcademicEntries(List<String> lines) {
        List<Academic> newEntries = simpleMap(lines, Academic::new);
        academicRepository.save(newEntries);
    }

    private void deleteExistingAcademicEntries() {
        academicRepository.deleteAll();
    }

    private void logSuccess(List<String> success) {
        LOG.info("Imported " + success.size() + " Je-S organisations successfully!");
    }

    private void logFailure(ServiceFailure failure) {
        LOG.error("Failed to import Je-S organisations.  Received errors: " + simpleMap(failure.getErrors(), Error::getDisplayString));
    }

    private ServiceResult<List<String>> readDownloadedFile(File downloadedFile) {
        try {
            return serviceSuccess(FileUtils.readLines(downloadedFile, Charset.defaultCharset()));
        } catch (IOException e) {
            return createServiceFailureFromIoException(e);
        }
    }

    private ServiceResult<File> downloadFile(URL jesFileToDownload) {

        return getTemporaryDownloadLocation().andOnSuccess(temporaryDownloadLocation -> {

            try {
                FileUtils.copyURLToFile(jesFileToDownload, temporaryDownloadLocation, connectionTimeoutMillis, readTimeoutMillis);
                return serviceSuccess(temporaryDownloadLocation);
            } catch (IOException e) {
                return createServiceFailureFromIoException(e);
            }
        });
    }

    private ServiceResult<File> getTemporaryDownloadLocation() {
        try {
            return serviceSuccess(File.createTempFile("jeslist", "jeslist"));
        } catch (IOException e) {
            return serviceFailure(new Error(e.getMessage(), INTERNAL_SERVER_ERROR));
        }
    }

    private ServiceResult<URL> getJesFileDownloadUrl() {
        try {
            return serviceSuccess(new URL("https://je-s.rcuk.ac.uk/file"));
        } catch (MalformedURLException e) {
            return serviceFailure(new Error(e.getMessage(), INTERNAL_SERVER_ERROR));
        }
    }

    private <T> ServiceResult<T> createServiceFailureFromIoException(IOException e) {
        return serviceFailure(new Error(e.getMessage(), extractHttpCodeFromExceptionIfPossible(e)));
    }

    private HttpStatus extractHttpCodeFromExceptionIfPossible(IOException e) {

        Matcher httpStatusCodeMatcher = Pattern.compile("^Server returned HTTP response code: (\\d+)").matcher(e.getMessage());

        if (httpStatusCodeMatcher.find()) {
            int httpNumericStatusCode = Integer.parseInt(httpStatusCodeMatcher.group(1));
            return HttpStatus.valueOf(httpNumericStatusCode);
        }

        return BAD_REQUEST;
    }

    // TODO DW - remove!
    public static void main(String[] args) {
        new ScheduledJesOrganisationListImporter().importJesList();
    }
}
