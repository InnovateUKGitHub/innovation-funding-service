package org.innovateuk.ifs.organisation.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceFailure;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.domain.Academic;
import org.innovateuk.ifs.organisation.repository.AcademicRepository;
import org.innovateuk.ifs.transactional.TransactionalHelper;
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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Scheduled job to refresh the Je-S organisation lookup list periodically from the Je-S website's csv file of
 * academic organisations
 */
@Component
public class ScheduledJesOrganisationListImporter {

    private static final Log LOG = LogFactory.getLog(ScheduledJesOrganisationListImporter.class);

    private AcademicRepository academicRepository;
    private ScheduledJesOrganisationListImporterFileDownloader fileDownloader;
    private ScheduledJesOrganisationListImporterOrganisationExtractor organisationExtractor;
    private TransactionalHelper transactionalHelper;

    private boolean importEnabled;
    private String jesSourceFileUrl;
    private String archiveLocation;
    private int connectionTimeoutMillis = 10000;
    private int readTimeoutMillis = 10000;
    private boolean deleteJesSourceFile;

    @Autowired
    ScheduledJesOrganisationListImporter(@Autowired AcademicRepository academicRepository,
                                         @Autowired ScheduledJesOrganisationListImporterFileDownloader fileDownloader,
                                         @Autowired ScheduledJesOrganisationListImporterOrganisationExtractor organisationExtractor,
                                         @Autowired TransactionalHelper transactionalHelper,
                                         @Value("${ifs.data.service.jes.organisation.importer.connection.timeout.millis}") int connectionTimeoutMillis,
                                         @Value("${ifs.data.service.jes.organisation.importer.read.timeout.millis}") int readTimeoutMillis,
                                         @Value("${ifs.data.service.jes.organisation.importer.enabled}") boolean importEnabled,
                                         @Value("${ifs.data.service.jes.organisation.importer.download.url}") String jesSourceFileUrl,
                                         @Value("${ifs.data.service.jes.organisation.importer.archive.location}") String archiveLocation,
                                         @Value("${ifs.data.service.jes.organisation.importer.archive.source.file}") boolean deleteJesSourceFile) {

        this.academicRepository = academicRepository;
        this.organisationExtractor = organisationExtractor;
        this.transactionalHelper = transactionalHelper;
        this.jesSourceFileUrl = jesSourceFileUrl;
        this.connectionTimeoutMillis = connectionTimeoutMillis;
        this.readTimeoutMillis = readTimeoutMillis;
        this.importEnabled = importEnabled;
        this.fileDownloader = fileDownloader;
        this.deleteJesSourceFile = deleteJesSourceFile;
        this.archiveLocation = archiveLocation;
    }

    @Transactional
    @Scheduled(cron = "${ifs.data.service.jes.organisation.importer.cron.expression}")
    public ServiceResult<List<String>> importJesList() {

        if (!importEnabled) {
            LOG.debug("Je-S organisation list import currently disabled");
            return serviceSuccess(emptyList());
        }

        ServiceResult<URL> jesSourceFileUrlResult = getUrlFromString(jesSourceFileUrl);
        ServiceResult<URL> archiveLocationUrlResult = getUrlFromString(archiveLocation);


        if (jesSourceFileUrlResult.isFailure()) {
            LOG.warn("Could not determine Je-S organisation list file URI.  Got " + jesSourceFileUrlResult.getFailure());
            return serviceFailure(jesSourceFileUrlResult.getFailure());
        }

        if (archiveLocationUrlResult.isFailure()) {
            LOG.warn("Could not determine archive file URI.  Got " + archiveLocationUrlResult.getFailure());
            return serviceFailure(archiveLocationUrlResult.getFailure());
        }

        URL jesFileToDownload = jesSourceFileUrlResult.getSuccess();
        URL archiveFile = archiveLocationUrlResult.getSuccess();

        if (!fileDownloader.jesSourceFileExists(jesFileToDownload)) {
            LOG.debug("No Je-S organisation list file to import");
            return serviceSuccess(emptyList());
        }

        LOG.info("Importing Je-S organisation list...");

        ServiceResult<List<String>> downloadResult = downloadFile(jesFileToDownload).
                andOnSuccess(downloadedFile -> archiveJesSourceFileIfExists(jesFileToDownload, archiveFile).
                andOnSuccess(() -> readDownloadedFile(downloadedFile)).
                andOnSuccessDo(organisationNames -> logDownloadedOrganisations(organisationNames)).
                andOnSuccessDo(organisationNames -> deleteExistingAcademicEntries()).
                andOnSuccessDo(organisationNames -> importNewAcademicEntries(organisationNames)));

        return downloadResult.handleSuccessOrFailureNoReturn(
                this::logFailure,
                this::logSuccess);
    }

    private ServiceResult<Void> archiveJesSourceFileIfExists(URL jesSourceFile, URL archiveLocation) {

        if (!fileDownloader.jesSourceFileExists(jesSourceFile)) {
            return serviceSuccess();
        }

        return fileDownloader.archiveSourceFile(jesSourceFile, archiveLocation);
    }

    private void logDownloadedOrganisations(List<String> organisationNames) {
        organisationNames.forEach(name -> LOG.debug("Found Je-S organisation " + name + " to import..."));
    }

    private void importNewAcademicEntries(List<String> organisationNames) {
        List<Academic> newEntries = simpleMap(organisationNames, Academic::new);
        academicRepository.saveAll(newEntries);
    }

    private void deleteExistingAcademicEntries() {
        academicRepository.deleteAll();
        transactionalHelper.flushWithNoCommit();
    }

    private void logSuccess(List<String> success) {
        LOG.info("Imported " + success.size() + " Je-S organisations successfully!");
    }

    private void logFailure(ServiceFailure failure) {
        LOG.error("Failed to import Je-S organisations.  Received errors: " + simpleMap(failure.getErrors(), Error::getDisplayString));
    }

    private ServiceResult<List<String>> readDownloadedFile(File downloadedFile) {
        return organisationExtractor.extractOrganisationsFromFile(downloadedFile);
    }

    private ServiceResult<File> downloadFile(URL jesFileToDownload) {
        return fileDownloader.copyJesSourceFile(jesFileToDownload, connectionTimeoutMillis, readTimeoutMillis);
    }

    private ServiceResult<URL> getUrlFromString(String s) {
        try {
            return serviceSuccess(new URL(s));
        } catch (MalformedURLException e) {
            return serviceFailure(new Error(e.getMessage(), INTERNAL_SERVER_ERROR));
        }
    }

    static <T> ServiceResult<T> createServiceFailureFromIoException(IOException e) {
        return serviceFailure(new Error(e.getMessage(), extractHttpCodeFromExceptionIfPossible(e)));
    }

    private static HttpStatus extractHttpCodeFromExceptionIfPossible(IOException e) {

        Matcher httpStatusCodeMatcher = Pattern.compile("^Server returned HTTP response code: (\\d+)").matcher(e.getMessage());

        if (httpStatusCodeMatcher.find()) {
            int httpNumericStatusCode = Integer.parseInt(httpStatusCodeMatcher.group(1));
            return HttpStatus.valueOf(httpNumericStatusCode);
        }

        return BAD_REQUEST;
    }
}
