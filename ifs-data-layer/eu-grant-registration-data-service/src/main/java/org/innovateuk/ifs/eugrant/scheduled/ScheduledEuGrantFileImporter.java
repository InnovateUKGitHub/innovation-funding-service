package org.innovateuk.ifs.eugrant.scheduled;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;
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
import static org.innovateuk.ifs.util.CollectionFunctions.simpleAnyMatch;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * TODO DW - document this class
 */
@Component
public class ScheduledEuGrantFileImporter {

    private static final Log LOG = LogFactory.getLog(ScheduledEuGrantFileImporter.class);

    private GrantsFileUploader grantsFileUploader;
    private GrantsFileExtractor grantsFileExtractor;
    private GrantSaver grantsSaver;
    private ResultsFileGenerator resultsFileGenerator;


    @Autowired
    ScheduledEuGrantFileImporter(@Autowired GrantsFileUploader grantsFileUploader,
                                 @Autowired GrantsFileExtractor grantsFileExtractor,
                                 @Autowired GrantSaver grantsSaver,
                                 @Autowired ResultsFileGenerator resultsFileGenerator) {

        this.grantsFileUploader = grantsFileUploader;
        this.grantsFileExtractor = grantsFileExtractor;
        this.grantsSaver = grantsSaver;
        this.resultsFileGenerator = resultsFileGenerator;
    }

    @Scheduled(cron = "${ifs.eu.data.service.grant.importer.cron.expression}")
    ServiceResult<File> importEuGrantsFile() {

        ServiceResult<File> sourceFileCheck = grantsFileUploader.getFileIfExists();

        if (isNotFoundError(sourceFileCheck)) {
            return sourceFileCheck;
        }

        ServiceResult<File> importResult =
                sourceFileCheck.andOnSuccess(sourceFile ->
                    grantsFileExtractor.processFile(sourceFile).
                        andOnSuccess(this::saveSuccessfullyExtractedGrants).
                        andOnSuccess(results -> resultsFileGenerator.generateResultsFile(results, sourceFile))
                );

        return importResult.handleSuccessOrFailureNoReturn(
                failure -> LOG.error("Unable to complete grant file import.  Failure is: " + importResult.getFailure()),
                success -> LOG.info("Grants imported successfully!  Results file can be found at " + success.getPath()));
    }

    private ServiceResult<List<ServiceResult<EuGrantResource>>> saveSuccessfullyExtractedGrants(List<ServiceResult<EuGrantResource>> grantsExtractResults) {

        List<ServiceResult<EuGrantResource>> creationResults = simpleMap(grantsExtractResults, extractResult ->
                extractResult.andOnSuccess(grantsSaver::saveGrant));

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
