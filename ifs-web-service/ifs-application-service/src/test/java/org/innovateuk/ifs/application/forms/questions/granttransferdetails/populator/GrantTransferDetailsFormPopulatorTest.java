package org.innovateuk.ifs.application.forms.questions.granttransferdetails.populator;

import org.innovateuk.ifs.application.forms.questions.granttransferdetails.form.GrantTransferDetailsForm;
import org.innovateuk.ifs.granttransfer.resource.EuActionTypeResource;
import org.innovateuk.ifs.granttransfer.resource.EuGrantTransferResource;
import org.innovateuk.ifs.granttransfer.service.EuGrantTransferRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.granttransfer.resource.EuGrantTransferResourceBuilder.newEuGrantTransferResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GrantTransferDetailsFormPopulatorTest {

    @InjectMocks
    private GrantTransferDetailsFormPopulator populator;

    @Mock
    private EuGrantTransferRestService euGrantTransferRestService;

    @Test
    public void populateFundingForm() {
        long applicationId = 1L;
        EuActionTypeResource euActionTypeResource = new EuActionTypeResource();
        euActionTypeResource.setId(30L);

        EuGrantTransferResource euFundingResource = newEuGrantTransferResource()
                .withActionType(euActionTypeResource)
                .withFundingContribution(BigDecimal.valueOf(100000L))
                .withGrantAgreementNumber("123456")
                .withProjectCoordinator(true)
                .withProjectStartDate(LocalDate.now())
                .withProjectEndDate(LocalDate.now().plusYears(1L))
                .withProjectName("Project Name")
                .withParticipantId("123456789")
                .build();

        GrantTransferDetailsForm grantTransferDetailsForm = new GrantTransferDetailsForm();

        when(euGrantTransferRestService.findDetailsByApplicationId(applicationId)).thenReturn(restSuccess(euFundingResource));

        populator.populate(grantTransferDetailsForm, applicationId);

        assertEquals(euFundingResource.getGrantAgreementNumber(), grantTransferDetailsForm.getGrantAgreementNumber());
        assertEquals(euFundingResource.getActionType().getId(), grantTransferDetailsForm.getActionType());
        assertEquals(euFundingResource.getFundingContribution(), grantTransferDetailsForm.getFundingContribution());
        assertEquals(euFundingResource.isProjectCoordinator(), grantTransferDetailsForm.getProjectCoordinator());
        assertEquals(euFundingResource.getProjectStartDate().getMonth().getValue(), grantTransferDetailsForm.getStartDateMonth().intValue());
        assertEquals(euFundingResource.getProjectStartDate().getYear(), grantTransferDetailsForm.getStartDateYear().intValue());
        assertEquals(euFundingResource.getProjectEndDate().getMonth().getValue(), grantTransferDetailsForm.getEndDateMonth().intValue());
        assertEquals(euFundingResource.getProjectEndDate().getYear(), grantTransferDetailsForm.getEndDateYear().intValue());
        assertEquals(euFundingResource.getProjectName(), grantTransferDetailsForm.getProjectName());
        assertEquals(euFundingResource.getParticipantId(), grantTransferDetailsForm.getParticipantId());
    }
}
