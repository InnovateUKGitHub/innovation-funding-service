package org.innovateuk.ifs.application.forms.questions.granttransferdetails.saver;

import org.innovateuk.ifs.application.forms.questions.granttransferdetails.form.GrantTransferDetailsForm;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.granttransfer.resource.EuGrantTransferResource;
import org.innovateuk.ifs.granttransfer.service.EuGrantTransferRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Collections;

import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GrantTransferDetailsSaverTest {

    @InjectMocks
    private GrantTransferDetailsSaver saver;

    @Mock
    private EuGrantTransferRestService euGrantTransferRestService;

    @Test
    public void save() {
        long applicationId = 3L;
        GrantTransferDetailsForm form = new GrantTransferDetailsForm();
        form.setActionType(1L);
        form.setEndDateMonth(1);
        form.setEndDateYear(2019);
        form.setStartDateMonth(1);
        form.setStartDateYear(2019);
        form.setFundingContribution(BigDecimal.TEN);
        form.setGrantAgreementNumber("123456");
        form.setParticipantId("987654321");
        form.setProjectCoordinator(true);
        form.setProjectName("MyProject");

        when(euGrantTransferRestService.findDetailsByApplicationId(applicationId)).thenReturn(restFailure(Collections.emptyList(), HttpStatus.NOT_FOUND));
        when(euGrantTransferRestService.updateGrantTransferDetails(any(), eq(applicationId))).thenReturn(restSuccess());

        RestResult<Void> result = saver.save(form, applicationId);

        assertTrue(result.isSuccess());

        ArgumentCaptor<EuGrantTransferResource> argumentCaptor = ArgumentCaptor.forClass(EuGrantTransferResource.class);

        verify(euGrantTransferRestService).updateGrantTransferDetails(argumentCaptor.capture(), eq(applicationId));

        EuGrantTransferResource grantTransferResource = argumentCaptor.getValue();

        assertEquals(grantTransferResource.getGrantAgreementNumber(), form.getGrantAgreementNumber());
        assertEquals(grantTransferResource.getActionType().getId(), form.getActionType());
        assertEquals(grantTransferResource.getFundingContribution(), form.getFundingContribution());
        assertEquals(grantTransferResource.getProjectCoordinator(), form.getProjectCoordinator());
        assertEquals(grantTransferResource.getProjectStartDate().getMonth().getValue(), form.getStartDateMonth().intValue());
        assertEquals(grantTransferResource.getProjectStartDate().getYear(), form.getStartDateYear().intValue());
        assertEquals(grantTransferResource.getProjectEndDate().getMonth().getValue(), form.getEndDateMonth().intValue());
        assertEquals(grantTransferResource.getProjectEndDate().getYear(), form.getEndDateYear().intValue());
        assertEquals(grantTransferResource.getProjectName(), form.getProjectName());
        assertEquals(grantTransferResource.getParticipantId(), form.getParticipantId());
    }


}
