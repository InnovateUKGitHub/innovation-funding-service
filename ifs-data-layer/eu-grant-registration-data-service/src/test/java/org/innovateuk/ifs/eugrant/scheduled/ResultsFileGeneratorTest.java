package org.innovateuk.ifs.eugrant.scheduled;

import org.apache.commons.io.FileUtils;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

import static java.lang.Thread.currentThread;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.eugrant.builder.EuGrantResourceBuilder.newEuGrantResource;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * TODO DW - document this class
 */
public class ResultsFileGeneratorTest {

    @Test
    public void generateResultsFile() throws URISyntaxException, IOException {

        File temporaryResultsFolder = Files.createTempDirectory("csv-results-test").toFile();

        URL originalCsvUrl = currentThread().getContextClassLoader().getResource("test-eu-grants.csv");
        File originalCsvFile = new File(originalCsvUrl.toURI());

        URL expectedCsvResultsFileUrl = currentThread().getContextClassLoader().getResource("test-eu-grants-expected-results.csv");
        File expectedCsvResultsFile = new File(expectedCsvResultsFileUrl.toURI());

        ResultsFileGenerator generator = new ResultsFileGenerator(temporaryResultsFolder.toURI().toString());

        List<ServiceResult<EuGrantResource>> importResults = asList(
            serviceSuccess(newEuGrantResource().withId(UUID.fromString("911dd9cb-4ecc-4220-8053-0af04670ee82")).build()),
            serviceFailure(new Error("Import error message, with a comma in it", BAD_REQUEST)));

        ServiceResult<File> generatedFileResult = generator.generateResultsFile(importResults, originalCsvFile);

        assertThat(generatedFileResult.isSuccess()).isEqualTo(true);

        File generatedFile = generatedFileResult.getSuccess();
        assertThat(generatedFile.getParentFile()).isEqualTo(temporaryResultsFolder);

        List<String> actualResultsFileContent = FileUtils.readLines(generatedFile);
        List<String> expectedResultsFileContent = FileUtils.readLines(expectedCsvResultsFile);
        assertThat(actualResultsFileContent).isEqualTo(expectedResultsFileContent);

    }
}
