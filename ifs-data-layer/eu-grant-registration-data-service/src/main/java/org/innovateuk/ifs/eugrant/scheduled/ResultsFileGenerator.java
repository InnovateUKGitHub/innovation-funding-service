package org.innovateuk.ifs.eugrant.scheduled;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.eugrant.scheduled.ScheduledEuGrantFileImporter.getUrlFromString;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * TODO DW - document this class
 */
@Component
public class ResultsFileGenerator {

    private static final Log LOG = LogFactory.getLog(ResultsFileGenerator.class);

    private URL resultsFileUrl;

    @Autowired
    ResultsFileGenerator(@Value("${ifs.eu.data.service.grant.importer.results.file.location}") String resultsFileUrl) {
        this.resultsFileUrl = getUrlFromString(resultsFileUrl).getSuccess();
    }

    ServiceResult<File> generateResultsFile(List<ServiceResult<UUID>> results, File originalFile) {
        try {
            return serviceSuccess(Files.createTempFile("", "").toFile());
        } catch (IOException e) {
            LOG.error("Error whilst generating eu grants results file at " + resultsFileUrl, e);
            return createServiceFailureFromIoException(e);
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
