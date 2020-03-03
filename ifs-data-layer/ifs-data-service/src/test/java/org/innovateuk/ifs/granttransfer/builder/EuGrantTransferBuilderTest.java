package org.innovateuk.ifs.granttransfer.builder;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.granttransfer.domain.EuActionType;
import org.innovateuk.ifs.granttransfer.domain.EuGrantTransfer;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.granttransfer.builder.EuGrantTransferBuilder.newEuGrantTransfer;
import static org.junit.Assert.assertEquals;

public class EuGrantTransferBuilderTest {

    @Test
    public void buildOne() {
        Long expectedId = 1L;
        Application expectedApplication = newApplication().build();
        String expectedGrantAgreementNumber = "1234";
        String expectedParticipantId = "123";
        LocalDate expectedProjectStartDate = LocalDate.now();
        LocalDate expectedProjectEndDate = LocalDate.now().plusDays(1);
        BigDecimal expectedFundingContribution = BigDecimal.ONE;
        Boolean expectedProjectCoordinator = true;
        EuActionType expectedActionType = new EuActionType();
        FileEntry expectedGrantAgreement = newFileEntry().build();
        FileEntry expectedCalculationSpreadsheet = newFileEntry().build();

        EuGrantTransfer grantAgreement = newEuGrantTransfer()
                .withId(expectedId)
                .withApplication(expectedApplication)
                .withGrantAgreementNumber(expectedGrantAgreementNumber)
                .withParticipantId(expectedParticipantId)
                .withProjectStartDate(expectedProjectStartDate)
                .withProjectEndDate(expectedProjectEndDate)
                .withFundingContribution(expectedFundingContribution)
                .withProjectCoordinator(expectedProjectCoordinator)
                .withActionType(expectedActionType)
                .withGrantAgreement(expectedGrantAgreement)
                .withCalculationSpreadsheet(expectedCalculationSpreadsheet)
                .build();

        assertEquals(expectedId, grantAgreement.getId());
        assertEquals(expectedApplication, grantAgreement.getApplication());
        assertEquals(expectedGrantAgreementNumber, grantAgreement.getGrantAgreementNumber());
        assertEquals(expectedParticipantId, grantAgreement.getParticipantId());
        assertEquals(expectedProjectStartDate, grantAgreement.getProjectStartDate());
        assertEquals(expectedProjectEndDate, grantAgreement.getProjectEndDate());
        assertEquals(expectedFundingContribution, grantAgreement.getFundingContribution());
        assertEquals(expectedActionType, grantAgreement.getActionType());
        assertEquals(expectedGrantAgreement, grantAgreement.getGrantAgreement());
        assertEquals(expectedCalculationSpreadsheet, grantAgreement.getCalculationSpreadsheet());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        Application[] expectedApplications = {newApplication().build(), newApplication().build()};
        String[] expectedGrantAgreementNumbers = {"1234", "4321"};
        String[] expectedParticipantIds = {"123", "321}"};
        LocalDate[] expectedProjectStartDates = {LocalDate.now(), LocalDate.now().minusDays(1)};
        LocalDate[] expectedProjectEndDates = {LocalDate.now().plusDays(1), LocalDate.now().plusDays(2)};
        BigDecimal[] expectedFundingContributions = {BigDecimal.ONE, BigDecimal.ZERO};
        Boolean[] expectedProjectCoordinators = {true, false};
        EuActionType[] expectedActionTypes = {new EuActionType(), new EuActionType()};
        FileEntry[] expectedGrantAgreements = {newFileEntry().build(), newFileEntry().build()};
        FileEntry[] expectedCalculationSpreadsheets = {newFileEntry().build(), newFileEntry().build()};

        List<EuGrantTransfer> grantAgreements = newEuGrantTransfer()
                .withId(expectedIds)
                .withApplication(expectedApplications)
                .withGrantAgreementNumber(expectedGrantAgreementNumbers)
                .withParticipantId(expectedParticipantIds)
                .withProjectStartDate(expectedProjectStartDates)
                .withProjectEndDate(expectedProjectEndDates)
                .withFundingContribution(expectedFundingContributions)
                .withProjectCoordinator(expectedProjectCoordinators)
                .withActionType(expectedActionTypes)
                .withGrantAgreement(expectedGrantAgreements)
                .withCalculationSpreadsheet(expectedCalculationSpreadsheets)
                .build(2);

        EuGrantTransfer first = grantAgreements.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedApplications[0], first.getApplication());
        assertEquals(expectedGrantAgreementNumbers[0], first.getGrantAgreementNumber());
        assertEquals(expectedParticipantIds[0], first.getParticipantId());
        assertEquals(expectedProjectStartDates[0], first.getProjectStartDate());
        assertEquals(expectedProjectEndDates[0], first.getProjectEndDate());
        assertEquals(expectedFundingContributions[0], first.getFundingContribution());
        assertEquals(expectedActionTypes[0], first.getActionType());
        assertEquals(expectedGrantAgreements[0], first.getGrantAgreement());
        assertEquals(expectedCalculationSpreadsheets[0], first.getCalculationSpreadsheet());

        EuGrantTransfer second = grantAgreements.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedApplications[1], second.getApplication());
        assertEquals(expectedGrantAgreementNumbers[1], second.getGrantAgreementNumber());
        assertEquals(expectedParticipantIds[1], second.getParticipantId());
        assertEquals(expectedProjectStartDates[1], second.getProjectStartDate());
        assertEquals(expectedProjectEndDates[1], second.getProjectEndDate());
        assertEquals(expectedFundingContributions[1], second.getFundingContribution());
        assertEquals(expectedActionTypes[1], second.getActionType());
        assertEquals(expectedGrantAgreements[1], second.getGrantAgreement());
        assertEquals(expectedCalculationSpreadsheets[1], second.getCalculationSpreadsheet());

    }
}