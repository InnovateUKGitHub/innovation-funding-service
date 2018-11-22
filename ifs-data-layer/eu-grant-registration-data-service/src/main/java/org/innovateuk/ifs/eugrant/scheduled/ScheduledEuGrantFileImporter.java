package org.innovateuk.ifs.eugrant.scheduled;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.security.WebUserSecuritySetter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * A scheduled job that looks for an EU Grant csv file containing rows of EU Grant project information, and imports
 * them into the EU Grant Registration database.
 *
 * It will produce a report file with the results of the import, recording successfully imported Grants' Short Codes
 * and import failure reasons if and when they should occur.
 */
@Component
public class ScheduledEuGrantFileImporter {

    private static final Log LOG = LogFactory.getLog(ScheduledEuGrantFileImporter.class);

    private GrantsFileHandler grantsFileHandler;
    private GrantsRecordExtractor grantsRecordsExtractor;
    private GrantSubmitter grantSubmitter;
    private GrantResultsFileGenerator resultsFileGenerator;
    private GrantsImportResultHandler grantsImportResultHandler;
    private WebUserSecuritySetter webUserSecuritySetter;

    @Autowired
    ScheduledEuGrantFileImporter(GrantsFileHandler grantsFileHandler,
                                 GrantsRecordExtractor grantsRecordsExtractor,
                                 GrantSubmitter grantSubmitter,
                                 GrantResultsFileGenerator resultsFileGenerator,
                                 GrantsImportResultHandler grantsImportResultHandler,
                                 WebUserSecuritySetter webUserSecuritySetter) {

        this.grantsFileHandler = grantsFileHandler;
        this.grantsRecordsExtractor = grantsRecordsExtractor;
        this.grantSubmitter = grantSubmitter;
        this.resultsFileGenerator = resultsFileGenerator;
        this.grantsImportResultHandler = grantsImportResultHandler;
        this.webUserSecuritySetter = webUserSecuritySetter;
    }

    @Scheduled(cron = "${ifs.eu.data.service.grant.importer.cron.expression}")
    void importEuGrantsFile() {

        ServiceResult<File> sourceFileCheck = grantsFileHandler.getSourceFileIfExists();

        if (isNotFoundError(sourceFileCheck)) {
            return;
        }

        LOG.info("Beginning import of grants...");

        webUserSecuritySetter.setWebUser();

        try {
            ServiceResult<Pair<File, List<ServiceResult<EuGrantResource>>>> importResult = sourceFileCheck.
                andOnSuccess(sourceFile -> grantsRecordsExtractor.processFile(sourceFile).
                andOnSuccess(this::saveSuccessfullyExtractedGrants).
                andOnSuccess(results -> resultsFileGenerator.generateResultsFile(results, sourceFile).
                andOnSuccessReturn(resultsFile -> Pair.of(resultsFile, results))));

            grantsImportResultHandler.recordResult(importResult);

        } finally {

            grantsFileHandler.deleteSourceFile();
            webUserSecuritySetter.clearWebUser();
        }
    }

    private ServiceResult<List<ServiceResult<EuGrantResource>>> saveSuccessfullyExtractedGrants(List<ServiceResult<EuGrantResource>> grantsExtractResults) {

        List<ServiceResult<EuGrantResource>> creationResults = simpleMap(grantsExtractResults, extractResult ->
                extractResult.andOnSuccess(grantSubmitter::createAndSubmitGrant));

        return serviceSuccess(creationResults);
    }

    private boolean isNotFoundError(ServiceResult<File> sourceFileCheck) {
        return sourceFileCheck.isFailure() &&
                simpleAnyMatch(sourceFileCheck.getFailure().getErrors(), e -> NOT_FOUND.equals(e.getStatusCode()));
    }

    static ServiceResult<URI> getUriFromString(String s) {
        try {
            return serviceSuccess(new URI(s));
        } catch (URISyntaxException e) {
            return serviceFailure(new Error(e.getMessage(), BAD_REQUEST));
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
