package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.*;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.assessment.domain.AverageAssessorScore;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.granttransfer.domain.EuGrantTransfer;
import org.innovateuk.ifs.workflow.audit.ProcessHistory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

@Rollback
@Transactional
public class ApplicationEoiServiceIntegrationTest extends BaseApplicationMigrationSetupTest  {

    @Autowired
    private ApplicationEoiService applicationEoiService;

    @Test
    public void createFullApplicationFromEoi() {
        setupEoiApplication();

        loginCompAdmin();

        ServiceResult<Long> result = applicationEoiService.createFullApplicationFromEoi(applicationId);

        assertThat(result.isSuccess(), equalTo(true));

        Optional<Application> optionalEoiApplication =  applicationRepository.findByPreviousApplicationId(applicationId);
        assertTrue(optionalEoiApplication.isPresent());

        Application eoiApplication = optionalEoiApplication.get();
        assertNotEquals(applicationId, eoiApplication.getId());

        Optional<Application> optionalFullApplication =  applicationRepository.findByEoiApplicationId(eoiApplication.getId());
        assertTrue(optionalFullApplication.isPresent());

        Application fullApplication = optionalFullApplication.get();
        assertEquals(applicationId, optionalFullApplication.get().getId());
        assertFalse(fullApplication.getApplicationExpressionOfInterestConfig().isEnabledForExpressionOfInterest());

        ApplicationExpressionOfInterestConfig fullApplicationExpressionOfInterestConfig = fullApplication.getApplicationExpressionOfInterestConfig();
        applicationExpressionOfInterestConfigRepository.findById(fullApplicationExpressionOfInterestConfig.getId()).stream()
                .forEach(applicationExpressionOfInterestConfig -> {
                    assertNotNull(applicationExpressionOfInterestConfig);
                    assertFalse(applicationExpressionOfInterestConfig.isEnabledForExpressionOfInterest());
                    assertEquals(applicationId, applicationExpressionOfInterestConfig.getApplication().getId());
                    assertEquals(eoiApplication.getId(), applicationExpressionOfInterestConfig.getEoiApplicationId());
                });

        applicationHorizonWorkProgrammeRepository.findByApplicationId(fullApplication.getId()).stream()
                .forEach(applicationHorizonWorkProgramme -> {
                    assertNotNull(applicationHorizonWorkProgramme);
                    assertEquals(applicationId, applicationHorizonWorkProgramme.getApplicationId());
                });

        applicationFinanceRepository.findByApplicationId(fullApplication.getId()).stream()
                .forEach(applicationFinance -> {
                    assertNotNull(applicationFinance);
                    assertEquals(applicationId, applicationFinance.getApplication().getId());

                    applicationFinanceRowRepository.findByTargetId(applicationFinance.getId()).stream()
                            .forEach(applicationFinanceRow -> {
                                assertNotNull(applicationFinanceRow);
                                assertNotNull(applicationFinanceRow);

                                financeRowMetaValueRepository.financeRowId(applicationFinanceRow.getId()).stream()
                                        .forEach(financeRowMetaValue -> {
                                            assertNotNull(financeRowMetaValue);
                                            assertNotNull(financeRowMetaValue.getFinanceRowMetaField());
                                            assertNotNull(financeRowMetaValue.getValue());
                                        });
                            });
                });

        applicationOrganisationAddressRepository.findByApplicationId(fullApplication.getId()).stream()
                .forEach(applicationOrganisationAddress -> {
                    assertNotNull(applicationOrganisationAddress);
                    assertEquals(applicationId, applicationOrganisationAddress.getApplication().getId());
                });

        Optional<AverageAssessorScore> averageAssessorScore = averageAssessorScoreRepository.findByApplicationId(fullApplication.getId());
        assertTrue(averageAssessorScore.isPresent());
        assertEquals(applicationId, averageAssessorScore.get().getApplication().getId());

        EuGrantTransfer euGrantTransfer = euGrantTransferRepository.findByApplicationId(fullApplication.getId());
        assertNotNull(euGrantTransfer);
        assertEquals(applicationId, euGrantTransfer.getApplication().getId());

        processRoleRepository.findByApplicationId(fullApplication.getId()).stream()
                .forEach(processRole -> {
                    assertNotNull(processRole);
                    assertEquals(applicationId.longValue(), processRole.getApplicationId());
                });

        formInputResponseRepository.findByApplicationId(fullApplication.getId()).stream()
                .forEach(formInputResponse -> {
                    assertNotNull(formInputResponse);
                    assertEquals(applicationId, formInputResponse.getApplication().getId());
                });

        questionStatusRepository.findByApplicationId(fullApplication.getId()).stream()
                .forEach(questionStatus -> {
                    assertNotNull(questionStatus);
                    assertEquals(applicationId, questionStatus.getApplication().getId());
                });

        List<ApplicationProcess> applicationProcess = applicationProcessRepository.findByTargetId(fullApplication.getId());
        assertEquals(1, applicationProcess.size());
        assertEquals(applicationProcess.get(0).getTarget().getId(), applicationId);
        assertEquals(applicationProcess.get(0).getProcessState(), ApplicationState.OPENED);

        List<ProcessHistory> applicationProcessHistory = processHistoryRepository.findByProcessId(applicationProcess.get(0).getId());
        assertEquals(0, applicationProcessHistory.size());

        assertTrue(fullApplication.getCompletion().compareTo(BigDecimal.ZERO) > 0);
    }
}
