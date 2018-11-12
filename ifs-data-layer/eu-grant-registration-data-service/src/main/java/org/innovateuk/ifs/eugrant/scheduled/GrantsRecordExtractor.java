package org.innovateuk.ifs.eugrant.scheduled;

import com.opencsv.CSVReader;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.eugrant.scheduled.ScheduledEuGrantFileImporter.createServiceFailureFromIoException;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * A component for extracting data from the EU Grant csv into a structured form.
 */
@Component
public class GrantsRecordExtractor {

    private GrantResourceBuilder grantResourceBuilder;

    GrantsRecordExtractor(@Autowired GrantResourceBuilder grantResourceBuilder) {
        this.grantResourceBuilder = grantResourceBuilder;
    }

    ServiceResult<List<ServiceResult<EuGrantResource>>> processFile(File file) {

        return readDataFromCsv(file).
            andOnSuccess(this::mapCsvRowsToHeaders).
            andOnSuccess(grantResourceBuilder::convertDataRowsToEuGrantResources);

    }

    private ServiceResult<List<List<String>>> readDataFromCsv(File file) {

        try (FileReader fileReader = new FileReader(file)) {

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

    private ServiceResult<List<Map<CsvHeader, String>>> mapCsvRowsToHeaders(List<List<String>> headersAndDataRows) {

        if (headersAndDataRows.size() < 2) {
            return serviceFailure(new Error("EU Grants csv is empty", BAD_REQUEST));
        }

        List<String> headerRow = headersAndDataRows.get(0);

        Map<CsvHeader, Integer> headersToColumnIndexes = simpleToMap(asList(CsvHeader.values()),
                Function.identity(),
                expectedHeader -> getIndexForHeader(expectedHeader.getHeaderText(), headerRow));

        Map<CsvHeader, Integer> missingHeaders = simpleFilter(headersToColumnIndexes, (header, index) -> index == -1);

        if (!missingHeaders.isEmpty()) {
            List<String> missingHeaderText = simpleMap(missingHeaders.keySet(), CsvHeader::getHeaderText);
            String errorMessage = "Missing csv column headers " + missingHeaderText + " from given column headers " + headerRow;
            return serviceFailure(new Error(errorMessage, BAD_REQUEST));
        }

        List<List<String>> dataRows = headersAndDataRows.subList(1, headersAndDataRows.size());
        List<Map<CsvHeader, String>> dataToHeaders = simpleMap(dataRows, row -> mapDataToCsvHeaders(row, headersToColumnIndexes));
        return serviceSuccess(dataToHeaders);
    }

    private int getIndexForHeader(String expectedHeaderText, List<String> headerColumns) {

        for (String headerColumn : headerColumns) {
            if (headerColumn.toLowerCase().startsWith(expectedHeaderText.toLowerCase())) {
                return headerColumns.indexOf(headerColumn);
            }
        }

        return -1;
    }

    private Map<CsvHeader, String> mapDataToCsvHeaders(List<String> row, Map<CsvHeader, Integer> headersToColumnIndexes) {

        return simpleToMap(asList(CsvHeader.values()),
                Function.identity(),
                header -> row.get(headersToColumnIndexes.get(header)));
    }
}
