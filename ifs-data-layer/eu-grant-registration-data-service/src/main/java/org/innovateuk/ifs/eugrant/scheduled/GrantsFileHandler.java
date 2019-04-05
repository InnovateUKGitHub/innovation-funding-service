package org.innovateuk.ifs.eugrant.scheduled;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.codehaus.plexus.util.FileUtils.deleteDirectory;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.eugrant.scheduled.ScheduledEuGrantFileImporter.getUriFromString;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * A component to handle the location and the deletion of EU Grant csv files.
 */
@Component
public class GrantsFileHandler {

    private URI sourceFileUrl;

    GrantsFileHandler(@Value("${ifs.eu.data.service.grant.importer.file.uri}") String sourceFileUri)
            throws URISyntaxException {

        ServiceResult<URI> uri = getUriFromString(sourceFileUri);

        if (uri.isFailure()) {
            throw new URISyntaxException(sourceFileUri, uri.getFailure().getErrors().get(0).getErrorKey());
        }

        this.sourceFileUrl = uri.getSuccess();
    }

    ServiceResult<List<File>> getSourceFileIfExists() {
        if (Files.exists(Paths.get(sourceFileUrl))) {
            File dir = new File(sourceFileUrl);
            File[] files = dir.listFiles((directory, filename) -> filename.endsWith(".csv"));
            return serviceSuccess(files != null ? asList(files) : emptyList());
        } else {
            return serviceFailure(notFoundError(File.class, sourceFileUrl.toString()));
        }
    }

    ServiceResult<Void> deleteSourceFile() {
        try {
            deleteDirectory(new File(sourceFileUrl));
            return serviceSuccess();
        } catch (IOException e){
            return serviceFailure(new Error(
                    "Could not delete source file", singletonList(sourceFileUrl.toString()), INTERNAL_SERVER_ERROR));
        }
    }
}
