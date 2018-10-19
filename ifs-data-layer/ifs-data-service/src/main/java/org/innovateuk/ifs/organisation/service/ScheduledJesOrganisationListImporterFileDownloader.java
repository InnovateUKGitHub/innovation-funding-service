package org.innovateuk.ifs.organisation.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.organisation.service.ScheduledJesOrganisationListImporter.createServiceFailureFromIoException;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Component used to deal with the handling of the file download and cleanup exercises of refreshing the Je-S list of organisations
 */
@Component
class ScheduledJesOrganisationListImporterFileDownloader {

    private static final Log LOG = LogFactory.getLog(ScheduledJesOrganisationListImporterFileDownloader.class);

    boolean jesSourceFileExists(URL jesSourceFile) {
        try {
            return new File(jesSourceFile.toURI()).exists();
        } catch (URISyntaxException e) {
            return false;
        }
    }

    ServiceResult<File> copyJesSourceFile(URL jesSourceFile, int connectionTimeoutMillis, int readTimeoutMillis) {

        return createTemporaryDownloadFile().andOnSuccess(temporaryDownloadFile -> {

            try {
                FileUtils.copyURLToFile(jesSourceFile, temporaryDownloadFile, connectionTimeoutMillis, readTimeoutMillis);
                return serviceSuccess(temporaryDownloadFile);
            } catch (IOException e) {
                return createServiceFailureFromIoException(e);
            }
        });
    }

    ServiceResult<Void> archiveSourceFile(URL jesSourceFile, URL archiveFile) {
        return deleteSourceFileIfExists(archiveFile).andOnSuccess(() -> moveFile(jesSourceFile, archiveFile));
    }


    private ServiceResult<Void> deleteSourceFileIfExists(URL archiveFile) {
        try {
            if (!new File(archiveFile.toURI()).exists()) {
                return serviceSuccess();
            }

            if (new File(archiveFile.toURI()).delete()) {
                return serviceSuccess();
            } else {
                return serviceFailure(new Error("jes.file.unable.to.delete", INTERNAL_SERVER_ERROR));
            }

        } catch (URISyntaxException e) {
            return serviceFailure(new Error("jes.filename.unable.to.parse", INTERNAL_SERVER_ERROR));
        }
    }

    private ServiceResult<Void> moveFile(URL sourceFile, URL newLocation) {
        try {
            if (new File(sourceFile.toURI()).renameTo(new File(newLocation.toURI()))) {
                return serviceSuccess();
            } else {
                return serviceFailure(new Error("jes.file.unable.to.archive", INTERNAL_SERVER_ERROR));
            }
        } catch (URISyntaxException e) {
            return serviceFailure(new Error("jes.filename.unable.to.parse", INTERNAL_SERVER_ERROR));
        }

    }


    private ServiceResult<File> createTemporaryDownloadFile() {
        try {
            return serviceSuccess(File.createTempFile("jeslist", "jeslist"));
        } catch (IOException e) {
            return serviceFailure(new Error(e.getMessage(), INTERNAL_SERVER_ERROR));
        }
    }
}
