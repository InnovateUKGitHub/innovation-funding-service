package org.innovateuk.ifs.organisation.service;

import org.apache.commons.io.FileUtils;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.organisation.service.ScheduledJesOrganisationListImporter.createServiceFailureFromIoException;

/**
 * Component to extract Organisations from the downloaded Je-S file
 */
@Component
public class ScheduledJesOrganisationListImporterOrganisationExtractor {

    ServiceResult<List<String>> extractOrganisationsFromFile(File downloadedFile) {
        try {
            return serviceSuccess(FileUtils.readLines(downloadedFile, Charset.defaultCharset()));
        } catch (IOException e) {
            return createServiceFailureFromIoException(e);
        }
    }
}
