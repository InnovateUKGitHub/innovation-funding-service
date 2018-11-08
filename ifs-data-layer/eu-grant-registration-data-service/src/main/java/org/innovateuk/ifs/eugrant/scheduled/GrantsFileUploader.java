package org.innovateuk.ifs.eugrant.scheduled;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.eugrant.scheduled.ScheduledEuGrantFileImporter.getUriFromString;

/**
 * TODO DW - document this class
 */
@Component
public class GrantsFileUploader {

    private static final Log LOG = LogFactory.getLog(GrantsFileUploader.class);

    private URI sourceFileUrl;

    @Autowired
    GrantsFileUploader(@Value("${ifs.eu.data.service.grant.importer.file.uri}") String sourceFileUri)
            throws URISyntaxException {

        ServiceResult<URI> uri = getUriFromString(sourceFileUri);

        if (uri.isFailure()) {
            throw new URISyntaxException(sourceFileUri, uri.getFailure().getErrors().get(0).getErrorKey());
        }

        this.sourceFileUrl = uri.getSuccess();
    }

    ServiceResult<File> getFileIfExists() {

        if (Files.exists(Paths.get(sourceFileUrl))) {
            return serviceSuccess(new File(sourceFileUrl));
        } else {
            return serviceFailure(notFoundError(File.class, sourceFileUrl.toString()));
        }
    }

}
