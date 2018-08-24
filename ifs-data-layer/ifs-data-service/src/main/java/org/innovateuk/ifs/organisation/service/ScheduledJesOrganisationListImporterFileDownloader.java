package org.innovateuk.ifs.organisation.service;

import org.apache.commons.io.FileUtils;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.organisation.service.ScheduledJesOrganisationListImporter.createServiceFailureFromIoException;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * TODO DW - document this class
 */
@Component
class ScheduledJesOrganisationListImporterFileDownloader {

    ServiceResult<File> downloadFile(URL jesFileToDownload, int connectionTimeoutMillis, int readTimeoutMillis) {

        return getTemporaryDownloadFile().andOnSuccess(temporaryDownloadFile -> {

            try {
                FileUtils.copyURLToFile(jesFileToDownload, temporaryDownloadFile, connectionTimeoutMillis, readTimeoutMillis);
                return serviceSuccess(temporaryDownloadFile);
            } catch (IOException e) {
                return createServiceFailureFromIoException(e);
            }
        });
    }

    private ServiceResult<File> getTemporaryDownloadFile() {
        try {
            return serviceSuccess(File.createTempFile("jeslist", "jeslist"));
        } catch (IOException e) {
            return serviceFailure(new Error(e.getMessage(), INTERNAL_SERVER_ERROR));
        }
    }
}
