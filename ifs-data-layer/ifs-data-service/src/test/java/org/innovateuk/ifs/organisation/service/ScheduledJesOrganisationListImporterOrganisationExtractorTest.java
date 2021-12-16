package org.innovateuk.ifs.organisation.service;

import com.google.common.base.Joiner;
import com.google.common.io.Files;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.nio.charset.Charset.defaultCharset;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class ScheduledJesOrganisationListImporterOrganisationExtractorTest {

    @Test
    public void extractOrganisationsFromFile() throws IOException {

        ScheduledJesOrganisationListImporterOrganisationExtractor extractor =
                new ScheduledJesOrganisationListImporterOrganisationExtractor();

        File testFile = File.createTempFile("jestest", "jestest");
        String content = Joiner.on('\n').join("Organisation names", "Org 1", "Org 2", "Org 3");
        Files.asCharSink(testFile, defaultCharset()).write(content);

        ServiceResult<List<String>> organisationExtractionResult =
                extractor.extractOrganisationsFromFile(testFile);

        assertThat(organisationExtractionResult.isSuccess()).isTrue();

        List<String> organisationNames = organisationExtractionResult.getSuccess();
        assertThat(organisationNames).containsExactly("Org 1", "Org 2", "Org 3");
    }
}
