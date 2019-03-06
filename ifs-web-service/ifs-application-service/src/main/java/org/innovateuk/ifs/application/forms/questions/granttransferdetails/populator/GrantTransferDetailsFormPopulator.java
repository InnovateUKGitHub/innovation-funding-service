package org.innovateuk.ifs.application.forms.questions.granttransferdetails.populator;

import org.innovateuk.ifs.application.forms.questions.granttransferdetails.form.GrantTransferDetailsForm;
import org.innovateuk.ifs.granttransfer.resource.EuGrantTransferResource;
import org.innovateuk.ifs.granttransfer.service.EuGrantTransferRestService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GrantTransferDetailsFormPopulator {

    private final EuGrantTransferRestService euGrantTransferRestService;

    public GrantTransferDetailsFormPopulator(EuGrantTransferRestService euGrantTransferRestService) {
        this.euGrantTransferRestService = euGrantTransferRestService;
    }

    public GrantTransferDetailsForm populate(GrantTransferDetailsForm fundingForm, long applicationId) {

        Optional<EuGrantTransferResource> maybeEuGrantResource = euGrantTransferRestService.findDetailsByApplicationId(applicationId).getOptionalSuccessObject();

        if (maybeEuGrantResource.isPresent()) {
            EuGrantTransferResource euGrantResource = maybeEuGrantResource.get();
            fundingForm.setFundingContribution(euGrantResource.getFundingContribution());
            fundingForm.setGrantAgreementNumber(euGrantResource.getGrantAgreementNumber());
            fundingForm.setParticipantId(euGrantResource.getParticipantId());
            fundingForm.setProjectCoordinator(euGrantResource.getProjectCoordinator());
            fundingForm.setProjectName(euGrantResource.getProjectName());
            if (euGrantResource.getProjectStartDate() != null) {
                fundingForm.setStartDateMonth(euGrantResource.getProjectStartDate().getMonthValue());
                fundingForm.setStartDateYear(euGrantResource.getProjectStartDate().getYear());
            }
            if (euGrantResource.getProjectEndDate() != null) {
                fundingForm.setEndDateMonth(euGrantResource.getProjectEndDate().getMonthValue());
                fundingForm.setEndDateYear(euGrantResource.getProjectEndDate().getYear());
            }
            if (euGrantResource.getActionType() != null) {
                fundingForm.setActionType(euGrantResource.getActionType().getId());
            }
        }

        return fundingForm;

    }
}
