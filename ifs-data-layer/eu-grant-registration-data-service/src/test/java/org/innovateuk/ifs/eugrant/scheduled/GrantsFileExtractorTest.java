package org.innovateuk.ifs.eugrant.scheduled;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.currentThread;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.eugrant.builder.EuGrantResourceBuilder.newEuGrantResource;
import static org.innovateuk.ifs.eugrant.scheduled.CsvHeader.*;
import static org.innovateuk.ifs.service.ServiceFailureTestHelper.assertThatServiceFailureIs;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.CollectionFunctions.zip;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * TODO DW - document this class
 */
@RunWith(MockitoJUnitRunner.class)
public class GrantsFileExtractorTest {

    @InjectMocks
    private GrantsFileExtractor extractor;

    @Mock
    private GrantResourceBuilder grantResourceBuilderMock;

    @Test
    public void processFile() throws URISyntaxException {
        assertCsvFilesAreProcessedSuccessfully("test-eu-grants.csv");
    }

    @Test
    public void processFileDifferentColumnHeaderOrder() throws URISyntaxException {
        assertCsvFilesAreProcessedSuccessfully("test-eu-grants-different-order.csv");
    }

    @Test
    public void processFileWithMissingColumns() throws URISyntaxException {

        URL testCsvUrl = currentThread().getContextClassLoader().getResource("test-eu-grants-missing-column.csv");
        File testCsvFile = new File(testCsvUrl.toURI());

        ServiceResult<List<ServiceResult<EuGrantResource>>> results = extractor.processFile(testCsvFile);

        String expectedErrorMessage = "Missing csv column headers [Participant identification code] from given " +
                "column headers [Organisation type, Organisation name, Registration number (Companies House), Full " +
                "name, Job title, Email, Telephone, Grant agreement number, Type of action, Project name, Project " +
                "start date, Project end date, EU funding contribution for above project (EUR), Is your " +
                "organisation the project co-ordinator on this project?]";

        assertThatServiceFailureIs(results, new Error(expectedErrorMessage, BAD_REQUEST));

        verify(grantResourceBuilderMock, never()).convertDataRowsToEuGrantResources(any());
    }

    private void assertCsvFilesAreProcessedSuccessfully(String filename) throws URISyntaxException {
        URL testCsvUrl = currentThread().getContextClassLoader().getResource(filename);
        File testCsvFile = new File(testCsvUrl.toURI());

        List<Map<CsvHeader, String>> expectedExtractedData = asList(

                asMap(ORGANISATION_TYPE, "Research", ORGANISATION_NAME, "The University of Sheffield",
                        COMPANIES_HOUSE_REGISTRATION_NUMBER, "", CONTACT_FULL_NAME, "Bob Bobbins",
                        CONTACT_JOB_TITLE, "Project manager", CONTACT_EMAIL_ADDRESS, "bob.bobbins@example.com",
                        CONTACT_TELEPHONE_NUMBER, "01234 567890", GRANT_AGREEMENT_NUMBER, "111222",
                        PIC, "998592400", ACTION_TYPE, "(CSA) Coordination and support action",
                        PROJECT_NAME, "An interesting project 1", PROJECT_START_DATE, "01/12/2018",
                        PROJECT_END_DATE, "28/02/2021", PROJECT_EU_FUNDING_CONTRIBUTION, "132470.07",
                        PROJECT_COORDINATOR, "BENEFICIARY"),

                asMap(ORGANISATION_TYPE, "Business", ORGANISATION_NAME, "Empire Ltd",
                        COMPANIES_HOUSE_REGISTRATION_NUMBER, "123456789", CONTACT_FULL_NAME, "Steve Smith",
                        CONTACT_JOB_TITLE, "Project administrator", CONTACT_EMAIL_ADDRESS, "steve.smith@example.com",
                        CONTACT_TELEPHONE_NUMBER, "09876 543210", GRANT_AGREEMENT_NUMBER, "333444",
                        PIC, "999763772", ACTION_TYPE, "(SME-1) SME Instrument phase 1",
                        PROJECT_NAME, "An interesting project 2", PROJECT_START_DATE, "30/01/2018",
                        PROJECT_END_DATE, "01/04/2022", PROJECT_EU_FUNDING_CONTRIBUTION, "437766.84",
                        PROJECT_COORDINATOR, "COORDINATOR")
        );

        List<EuGrantResource> mockGeneratedEuGrantResources = newEuGrantResource().build(2);

        List<ServiceResult<EuGrantResource>> mockGeneratedResults =
                simpleMap(mockGeneratedEuGrantResources, ServiceResult::serviceSuccess);

        when(grantResourceBuilderMock.convertDataRowsToEuGrantResources(createExtractedDataExpectation(expectedExtractedData))).thenReturn(serviceSuccess(mockGeneratedResults));

        ServiceResult<List<ServiceResult<EuGrantResource>>> results = extractor.processFile(testCsvFile);

        assertThat(results.isSuccess()).isTrue();
        assertThat(results.getSuccess()).isEqualTo(mockGeneratedResults);

        verify(grantResourceBuilderMock, times(1)).convertDataRowsToEuGrantResources(createExtractedDataExpectation(expectedExtractedData));
    }

    private List<Map<CsvHeader, String>> createExtractedDataExpectation(List<Map<CsvHeader, String>> expectedExtractedData) {

        return createLambdaMatcher(actualExtractedData -> {

            assertThat(actualExtractedData).hasSize(expectedExtractedData.size());

            zip(expectedExtractedData, actualExtractedData, (expectedRow, actualRow) -> {
                expectedRow.forEach((header, expectedValue) -> {

                    String actualValue = actualRow.get(header);

                    if (!actualValue.equals(expectedValue)) {
                        System.out.println("Expected header " + header + " to have value " + expectedValue + " but found " + actualValue);
                    }

                    assertThat(actualValue).
                            withFailMessage("Expected header " + header + " to have value " + expectedValue + " but found " + actualValue).
                            isEqualTo(expectedValue);
                });
            });
        });
    }
}
