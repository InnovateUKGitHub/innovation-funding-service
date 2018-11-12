package org.innovateuk.ifs.eugrant.scheduled;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.eugrant.scheduled.ScheduledEuGrantFileImporter.getUriFromString;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * TODO DW - document this class
 */
@Component
public class GrantsFileHandler {

    private static final Log LOG = LogFactory.getLog(GrantsFileHandler.class);

    private URI sourceFileUrl;

    @Autowired
    GrantsFileHandler(@Value("${ifs.eu.data.service.grant.importer.file.uri}") String sourceFileUri)
            throws URISyntaxException {

        ServiceResult<URI> uri = getUriFromString(sourceFileUri);

        if (uri.isFailure()) {
            throw new URISyntaxException(sourceFileUri, uri.getFailure().getErrors().get(0).getErrorKey());
        }

        this.sourceFileUrl = uri.getSuccess();
    }

    ServiceResult<File> getSourceFileIfExists() {

        if (Files.exists(Paths.get(sourceFileUrl))) {
            return serviceSuccess(new File(sourceFileUrl));
        } else {
            return serviceFailure(notFoundError(File.class, sourceFileUrl.toString()));
        }
    }

    ServiceResult<Void> deleteSourceFile() {

        if (new File(sourceFileUrl).delete()) {
            return serviceSuccess();
        } else {
            return serviceFailure(new Error(
                    "Could not delete source file", singletonList(sourceFileUrl.toString()), INTERNAL_SERVER_ERROR));
        }
    }
}
