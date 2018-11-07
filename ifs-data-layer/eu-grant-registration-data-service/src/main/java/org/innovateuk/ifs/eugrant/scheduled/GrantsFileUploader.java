package org.innovateuk.ifs.eugrant.scheduled;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.eugrant.scheduled.ScheduledEuGrantFileImporter.getUrlFromString;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * TODO DW - document this class
 */
@Component
public class GrantsFileUploader {

    private static final Log LOG = LogFactory.getLog(GrantsFileUploader.class);

    private URL sourceFileUrl;

    @Autowired
    GrantsFileUploader(@Value("${ifs.eu.data.service.grant.importer.file.location}") String sourceFileUrl) {
        this.sourceFileUrl = getUrlFromString(sourceFileUrl).getSuccess();
    }

    ServiceResult<File> getFileIfExists() {
        try {
            boolean exists = Files.exists(Paths.get(sourceFileUrl.toURI()));

            if (exists) {
                return serviceSuccess(new File(sourceFileUrl.toURI()));
            } else {
                return serviceFailure(notFoundError(File.class, sourceFileUrl.toString()));
            }
        } catch (URISyntaxException e) {
            LOG.error("Error attempting to check for existence of eu-grant file at URL " + sourceFileUrl, e);
            return serviceFailure(new Error(e.getMessage(), INTERNAL_SERVER_ERROR));
        }
    }

}
