package org.innovateuk.ifs.application.forms.questions.granttransferdetails.populator;

import org.innovateuk.ifs.application.forms.questions.granttransferdetails.form.GrantTransferDetailsForm;
import org.innovateuk.ifs.granttransfer.resource.EuGrantTransferResource;
import org.innovateuk.ifs.granttransfer.service.EuGrantTransferRestServiceImpl;
import org.springframework.stereotype.Component;

@Component
public class GrantTransferDetailsFormPopulator {

    private final EuGrantTransferRestServiceImpl euGrantTransferRestService;

    public GrantTransferDetailsFormPopulator(EuGrantTransferRestServiceImpl euGrantTransferRestService) {
        this.euGrantTransferRestService = euGrantTransferRestService;
    }

    public GrantTransferDetailsForm populate(GrantTransferDetailsForm fundingForm, long applicationId) {

        EuGrantTransferResource euGrantResource = euGrantTransferRestService.findDetailsByApplicationId(applicationId).getSuccess();

        fundingForm.setFundingContribution(euGrantResource.getFundingContribution());
        fundingForm.setGrantAgreementNumber(euGrantResource.getGrantAgreementNumber());
        fundingForm.setParticipantId(euGrantResource.getParticipantId());
        fundingForm.setProjectCoordinator(euGrantResource.isProjectCoordinator());
        fundingForm.setProjectName(euGrantResource.getProjectName());
        fundingForm.setStartDateMonth(euGrantResource.getProjectStartDate().getMonthValue());
        fundingForm.setStartDateYear(euGrantResource.getProjectStartDate().getYear());
        fundingForm.setEndDateMonth(euGrantResource.getProjectEndDate().getMonthValue());
        fundingForm.setEndDateYear(euGrantResource.getProjectEndDate().getYear());
        fundingForm.setActionType(euGrantResource.getActionType().getId());

        return fundingForm;

    }
}
