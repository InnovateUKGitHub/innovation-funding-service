package org.innovateuk.ifs.organisation.service;

import org.apache.commons.io.FileUtils;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * TODO DW - document this class
 */
public class ScheduledJesOrganisationListImporterOrganisationExtractorTest {

    @Test
    public void extractOrganisationsFromFile() throws IOException {

        ScheduledJesOrganisationListImporterOrganisationExtractor extractor =
                new ScheduledJesOrganisationListImporterOrganisationExtractor();

        File testFile = File.createTempFile("jestest", "jestest");
        FileUtils.writeLines(testFile, asList("Organisation names", "Org 1", "Org 2", "Org 3"));

        ServiceResult<List<String>> organisationExtractionResult =
                extractor.extractOrganisationsFromFile(testFile);

        assertThat(organisationExtractionResult.isSuccess()).isTrue();

        List<String> organisationNames = organisationExtractionResult.getSuccess();
        assertThat(organisationNames).containsExactly("Org 1", "Org 2", "Org 3");
    }
}
