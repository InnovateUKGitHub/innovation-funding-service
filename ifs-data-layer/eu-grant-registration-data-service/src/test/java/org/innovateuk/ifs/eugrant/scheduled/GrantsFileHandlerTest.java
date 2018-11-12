package org.innovateuk.ifs.eugrant.scheduled;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.service.ServiceFailureTestHelper.assertThatServiceFailureIs;

public class GrantsFileHandlerTest {

    @Test
    public void getSourceFileIfExists() throws IOException, URISyntaxException {

        File existingSourceFile = File.createTempFile("temp", "temp");

        GrantsFileHandler handler = new GrantsFileHandler(existingSourceFile.toURI().toString());

        ServiceResult<File> result = handler.getSourceFileIfExists();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getSuccess()).isEqualTo(existingSourceFile);
    }

    @Test
    public void getSourceFileIfExistsDoesntExist() throws URISyntaxException {

        File nonExistentSourceFile = new File("non-existent-eu-grant-file");

        GrantsFileHandler handler = new GrantsFileHandler(nonExistentSourceFile.toURI().toString());

        ServiceResult<File> result = handler.getSourceFileIfExists();

        assertThat(result.isFailure()).isTrue();
        assertThatServiceFailureIs(result, notFoundError(File.class, nonExistentSourceFile.toURI().toString()));
    }

    @Test
    public void deleteSourceFile() throws IOException, URISyntaxException {

        File existingSourceFile = File.createTempFile("temp", "temp");

        GrantsFileHandler handler = new GrantsFileHandler(existingSourceFile.toURI().toString());

        ServiceResult<Void> result = handler.deleteSourceFile();

        assertThat(result.isSuccess()).isTrue();
        assertThat(existingSourceFile).doesNotExist();
    }

    @Test
    public void deleteSourceFileFailureToRemove() throws URISyntaxException {

        File nonExistentSourceFile = new File("non-existent-eu-grant-file");

        GrantsFileHandler handler = new GrantsFileHandler(nonExistentSourceFile.toURI().toString());

        ServiceResult<Void> result = handler.deleteSourceFile();

        assertThat(result.isFailure()).isTrue();
    }

    @Test
    public void newGrantsFileUploaderInvalidUri() {

        try {
            new GrantsFileHandler("not a valid uri");
        } catch (URISyntaxException e) {
            assertThat(e.getInput()).isEqualTo("not a valid uri");
            assertThat(e.getMessage()).containsIgnoringCase("Illegal character in path at index 3");
        }
    }
}
