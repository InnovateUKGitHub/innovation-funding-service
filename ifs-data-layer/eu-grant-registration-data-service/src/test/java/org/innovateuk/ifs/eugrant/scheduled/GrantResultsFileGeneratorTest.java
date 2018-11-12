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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

import static java.lang.Thread.currentThread;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.eugrant.builder.EuGrantResourceBuilder.newEuGrantResource;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class GrantResultsFileGeneratorTest {

    @Test
    public void generateResultsFile() throws URISyntaxException, IOException {

        File temporaryResultsFolder = Files.createTempDirectory("csv-results-test").toFile();

        URL originalCsvUrl = currentThread().getContextClassLoader().getResource("test-eu-grants.csv");
        File originalCsvFile = new File(requireNonNull(originalCsvUrl).toURI());

        URL expectedCsvResultsFileUrl = currentThread().getContextClassLoader().getResource("test-eu-grants-expected-results.csv");
        File expectedCsvResultsFile = new File(requireNonNull(expectedCsvResultsFileUrl).toURI());

        GrantResultsFileGenerator generator = new GrantResultsFileGenerator(temporaryResultsFolder.toURI().toString());

        List<ServiceResult<EuGrantResource>> importResults = asList(
            serviceSuccess(newEuGrantResource().withShortCode("abc12").build()),
            serviceFailure(new Error("Import error message, with a comma in it", BAD_REQUEST)));

        ServiceResult<File> generatedFileResult = generator.generateResultsFile(importResults, originalCsvFile);

        assertThat(generatedFileResult.isSuccess()).isEqualTo(true);

        File generatedFile = generatedFileResult.getSuccess();
        assertThat(generatedFile.getParentFile()).isEqualTo(temporaryResultsFolder);

        List<String> actualResultsFileContent = FileUtils.readLines(generatedFile, Charset.defaultCharset());
        List<String> expectedResultsFileContent = FileUtils.readLines(expectedCsvResultsFile, Charset.defaultCharset());
        assertThat(actualResultsFileContent).isEqualTo(expectedResultsFileContent);
    }

    @Test
    public void newGrantResultsFileGeneratorInvalidUri() {

        try {
            new GrantResultsFileGenerator("not a valid uri");
        } catch (URISyntaxException e) {
            assertThat(e.getInput()).isEqualTo("not a valid uri");
            assertThat(e.getMessage()).containsIgnoringCase("Illegal character in path at index 3");
        }
    }
}
