package org.innovateuk.ifs.eugrant.funding.populator;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.eugrant.EuActionTypeResource;
import org.innovateuk.ifs.eugrant.EuFundingResource;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.funding.form.EuFundingForm;
import org.innovateuk.ifs.eugrant.overview.service.EuGrantCookieService;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.innovateuk.ifs.eugrant.builder.EuActionTypeResourceBuilder.newEuActionTypeResource;
import static org.innovateuk.ifs.eugrant.builder.EuFundingResourceBuilder.newEuFundingResource;
import static org.innovateuk.ifs.eugrant.builder.EuGrantResourceBuilder.newEuGrantResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class EuFundingFormPopulatorTest extends BaseServiceUnitTest<EuFundingFormPopulator> {

    @Mock
    private EuGrantCookieService euGrantCookieService;

    @Override
    protected EuFundingFormPopulator supplyServiceUnderTest() {
        return new EuFundingFormPopulator();
    }

    @Test
    public void populateFundingForm() throws Exception {

        EuActionTypeResource euActionTypeResource = newEuActionTypeResource()
                .withId(1L)
                .withName("Action Type")
                .withDescription("Description")
                .withPriority(1)
                .build();

        EuFundingResource euFundingResource = newEuFundingResource()
                .withActionType(euActionTypeResource)
                .withFundingContribution(BigDecimal.valueOf(100000L))
                .withGrantAgreementNumber("123456")
                .withProjectCoordinator(true)
                .withProjectStartDate(LocalDate.now())
                .withProjectEndDate(LocalDate.now().plusYears(1L))
                .withProjectName("Project Name")
                .withParticipantId("123456789")
                .build();

        EuFundingForm fundingForm = new EuFundingForm();

        EuGrantResource euGrantResource = newEuGrantResource()
                .withFunding(euFundingResource)
                .build();

        when(euGrantCookieService.get()).thenReturn(euGrantResource);

        EuFundingForm euFundingForm = service.populate(fundingForm);

        assertEquals(euFundingResource.getGrantAgreementNumber(), euFundingForm.getGrantAgreementNumber());
        assertEquals(euFundingResource.getActionType().getId(), euFundingForm.getActionType());
        assertEquals(euFundingResource.getFundingContribution(), euFundingForm.getFundingContribution());
        assertEquals(euFundingResource.isProjectCoordinator(), euFundingForm.getProjectCoordinator());
        assertEquals(euFundingResource.getProjectStartDate().getMonth().getValue(), euFundingForm.getStartDateMonth().intValue());
        assertEquals(euFundingResource.getProjectStartDate().getYear(), euFundingForm.getStartDateYear().intValue());
        assertEquals(euFundingResource.getProjectEndDate().getMonth().getValue(), euFundingForm.getEndDateMonth().intValue());
        assertEquals(euFundingResource.getProjectEndDate().getYear(), euFundingForm.getEndDateYear().intValue());
        assertEquals(euFundingResource.getProjectName(), euFundingForm.getProjectName());
        assertEquals(euFundingResource.getParticipantId(), euFundingForm.getParticipantId());
    }
}