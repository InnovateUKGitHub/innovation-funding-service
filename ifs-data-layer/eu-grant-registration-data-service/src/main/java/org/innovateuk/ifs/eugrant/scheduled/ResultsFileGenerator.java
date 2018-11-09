package org.innovateuk.ifs.eugrant.scheduled;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.eugrant.scheduled.ScheduledEuGrantFileImporter.createServiceFailureFromIoException;
import static org.innovateuk.ifs.eugrant.scheduled.ScheduledEuGrantFileImporter.getUriFromString;

/**
 * TODO DW - document this class
 */
@Component
public class ResultsFileGenerator {

    private static final Log LOG = LogFactory.getLog(ResultsFileGenerator.class);

    private URI resultsFileUri;

    @Autowired
    ResultsFileGenerator(@Value("${ifs.eu.data.service.grant.importer.results.file.location.uri}") String resultsFileUri)
                        throws URISyntaxException {

        ServiceResult<URI> uri = getUriFromString(resultsFileUri);

        if (uri.isFailure()) {
            throw new URISyntaxException(resultsFileUri, uri.getFailure().getErrors().get(0).getErrorKey());
        }

        this.resultsFileUri = uri.getSuccess();
    }

    ServiceResult<File> generateResultsFile(List<ServiceResult<EuGrantResource>> results, File originalFile) {
        try {
            return serviceSuccess(Files.createTempFile("", "").toFile());
        } catch (IOException e) {
            LOG.error("Error whilst generating eu grants results file at " + resultsFileUri, e);
            return createServiceFailureFromIoException(e);
        }
    }
}
