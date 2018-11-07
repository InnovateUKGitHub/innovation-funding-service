package org.innovateuk.ifs.eugrant.scheduled;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.service.ServiceFailureTestHelper.assertThatServiceFailureIs;

/**
 * TODO DW - document this class
 */
public class GrantsFileUploaderTest {

    @Test
    public void getFileIfExists() throws IOException, URISyntaxException {

        File existingSourceFile = File.createTempFile("temp", "temp");

        GrantsFileUploader uploader = new GrantsFileUploader(existingSourceFile.toURI().toString());

        ServiceResult<File> result = uploader.getFileIfExists();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getSuccess()).isEqualTo(existingSourceFile);
    }

    @Test
    public void getFileIfExistsDoesntExist() throws URISyntaxException {

        File nonExistentSourceFile = new File("non-existent-eu-grant-file");

        GrantsFileUploader uploader = new GrantsFileUploader(nonExistentSourceFile.toURI().toString());

        ServiceResult<File> result = uploader.getFileIfExists();

        assertThat(result.isFailure()).isTrue();
        assertThatServiceFailureIs(result, notFoundError(File.class, nonExistentSourceFile.toURI().toString()));
    }

    @Test
    public void newGrantsFileUploaderInvalidUri() {

        try {
            new GrantsFileUploader("not a valid uri");
        } catch (URISyntaxException e) {
            assertThat(e.getInput()).isEqualTo("not a valid uri");
            assertThat(e.getMessage()).containsIgnoringCase("Illegal character in path at index 3");
        }
    }
}
