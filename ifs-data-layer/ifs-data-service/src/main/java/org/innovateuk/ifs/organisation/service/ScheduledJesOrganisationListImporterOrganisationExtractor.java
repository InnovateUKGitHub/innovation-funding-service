package org.innovateuk.ifs.organisation.service;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
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
            List<String> organisationNames = Files.readLines(downloadedFile, Charsets.UTF_8);
            return serviceSuccess(organisationNames.subList(1, organisationNames.size()));
        } catch (IOException e) {
            return createServiceFailureFromIoException(e);
        }
    }
}
