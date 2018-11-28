package org.innovateuk.ifs.eugrant.scheduled;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.eugrant.scheduled.CsvUtils.readDataFromCsv;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * A component for extracting data from the EU Grant csv into a structured form.
 */
@Component
public class GrantsRecordExtractor {

    private GrantResourceBuilder grantResourceBuilder;

    @Autowired
    GrantsRecordExtractor(GrantResourceBuilder grantResourceBuilder) {
        this.grantResourceBuilder = grantResourceBuilder;
    }

    ServiceResult<List<ServiceResult<EuGrantResource>>> processFile(File file) {

        return readDataFromCsv(file).
            andOnSuccess(this::filterOutEmptyRows).
            andOnSuccess(this::mapCsvRowsToHeaders).
            andOnSuccess(grantResourceBuilder::convertDataRowsToEuGrantResources);
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

    private ServiceResult<List<List<String>>> filterOutEmptyRows(List<List<String>> headersAndDataRows) {
        Predicate<List<String>> emptyRow = row -> simpleAllMatch(row, StringUtils::isEmpty);
        List<List<String>> filteredList = simpleFilterNot(headersAndDataRows, emptyRow);
        return serviceSuccess(filteredList);
    }
}
