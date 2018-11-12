package org.innovateuk.ifs.eugrant.scheduled;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.eugrant.scheduled.ScheduledEuGrantFileImporter.createServiceFailureFromIoException;
import static org.innovateuk.ifs.eugrant.scheduled.ScheduledEuGrantFileImporter.getUriFromString;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

/**
 * A component to create a results file based upon a source EU Grant csv.
 *
 * The output results file will contain the contents of the original source file with
 * the addition of 2 extra columns - a "Short code" column for recording the Short codes
 * of the successfully imported rows, and an "Import failure reason" column for
 * recording any import failure reasons for rows that failed to import.
 */
@Component
public class GrantResultsFileGenerator {

    private static final DateTimeFormatter RESULTS_FILE_SUFFIX_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_hh-mm-ss");

    private static final List<String> ADDITIONAL_COLUMN_HEADERS = asList("Short code", "Import failure reason");


    private URI resultsFileUri;

    GrantResultsFileGenerator(@Value("${ifs.eu.data.service.grant.importer.results.file.location.uri}") String resultsFileUri)
                        throws URISyntaxException {

        ServiceResult<URI> uri = getUriFromString(resultsFileUri);

        if (uri.isFailure()) {
            throw new URISyntaxException(resultsFileUri, uri.getFailure().getErrors().get(0).getErrorKey());
        }

        this.resultsFileUri = uri.getSuccess();
    }

    ServiceResult<File> generateResultsFile(List<ServiceResult<EuGrantResource>> importResults, File originalFile) {

        return readDataFromCsv(originalFile).
                andOnSuccess(originalData -> addImportResultsToOriginalData(originalData, importResults)).
                andOnSuccess(this::createResultsFile);
    }

    private ServiceResult<List<List<String>>> addImportResultsToOriginalData(List<List<String>> originalData, List<ServiceResult<EuGrantResource>> importResults) {

        List<String> newHeaders = combineLists(originalData.get(0), ADDITIONAL_COLUMN_HEADERS);

        List<List<String>> originalDataMinusHeaders = originalData.subList(1, originalData.size());

        List<List<String>> originalDataPlusImportStatusColumns = zipAndMap(originalDataMinusHeaders, importResults, (originalRow, importResultForRow) -> {

            List<String> importResultsColumns = importResultForRow.handleSuccessOrFailure(
                    failure -> asList("", failure.getErrors().get(0).getErrorKey()),
                    success -> asList(success.getShortCode(), ""));

            return combineLists(originalRow, importResultsColumns);
        });

        List<List<String>> finalSetOfData = combineLists(newHeaders, originalDataPlusImportStatusColumns);

        return serviceSuccess(finalSetOfData);
    }

    private ServiceResult<File> createResultsFile(List<List<String>> data) {

        String dateTimeSuffix = ZonedDateTime.now().format(RESULTS_FILE_SUFFIX_FORMAT);
        File resultsFileFolder = new File(resultsFileUri);
        File resultsFile = new File(resultsFileFolder, "eu-grants-import-result-" + dateTimeSuffix + ".csv");

        try (FileWriter fileWriter = new FileWriter(resultsFile)) {

            try (CSVWriter writer = new CSVWriter(fileWriter)) {
                writer.writeAll(simpleMap(data, row -> row.toArray(new String[] {})));
            }

        } catch (IOException e) {
            return createServiceFailureFromIoException(e);
        }

        return serviceSuccess(resultsFile);
    }

    private ServiceResult<List<List<String>>> readDataFromCsv(File originalFile) {

        try (FileReader fileReader = new FileReader(originalFile)) {

            try (CSVReader reader = new CSVReader(fileReader)) {

                try {
                    List<String[]> data = reader.readAll();
                    List<List<String>> dataInLists = simpleMap(data, Arrays::asList);
                    return serviceSuccess(dataInLists);
                } catch (IOException e) {
                    return createServiceFailureFromIoException(e);
                }
            }

        } catch (IOException e) {
            return createServiceFailureFromIoException(e);
        }
    }

}
