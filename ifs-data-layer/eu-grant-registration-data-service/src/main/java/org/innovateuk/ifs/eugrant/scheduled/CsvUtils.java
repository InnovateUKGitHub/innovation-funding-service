package org.innovateuk.ifs.eugrant.scheduled;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.innovateuk.ifs.commons.service.ServiceResult;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.eugrant.scheduled.ScheduledEuGrantFileImporter.createServiceFailureFromIoException;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Utility class to help with common CSV actions.
 */
public class CsvUtils {

    /**
     * Method to read the contents of a CSV as a List of Lists of Strings.  Each outer list represents a row, and each
     * inner list represents a set of cells for that row.
     */
    static ServiceResult<List<List<String>>> readDataFromCsv(File csvFile) {

        try (FileReader fileReader = new FileReader(csvFile)) {

            try (CSVReader reader = new CSVReader(fileReader)) {

                try {
                    List<String[]> data = reader.readAll();
                    List<List<String>> dataInLists = simpleMap(data, CsvUtils::trim);
                    return serviceSuccess(dataInLists);
                } catch (IOException e) {
                    return createServiceFailureFromIoException(e);
                }
            }

        } catch (IOException e) {
            return createServiceFailureFromIoException(e);
        }
    }

    private static List<String> trim(String[] data) {
        return Arrays.stream(data).map(String::trim).collect(toList());
    }

    /**
     * Method to write a List of Lists of Strings into a CSV.  Each outer list represents a row, and each inner list
     * represents a set of cells for that row.
     */
    static ServiceResult<File> writeDataToCsv(List<List<String>> data, File csvFile) {
        try (FileWriter fileWriter = new FileWriter(csvFile)) {

            try (CSVWriter writer = new CSVWriter(fileWriter)) {
                writer.writeAll(simpleMap(data, row -> row.toArray(new String[] {})));
            }

        } catch (IOException e) {
            return createServiceFailureFromIoException(e);
        }

        return serviceSuccess(csvFile);
    }

}
