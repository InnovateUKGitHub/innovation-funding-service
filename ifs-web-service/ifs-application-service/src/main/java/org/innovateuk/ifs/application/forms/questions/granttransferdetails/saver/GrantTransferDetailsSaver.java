package org.innovateuk.ifs.application.forms.questions.granttransferdetails.saver;

import org.innovateuk.ifs.application.forms.questions.granttransferdetails.form.GrantTransferDetailsForm;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.granttransfer.resource.EuActionTypeResource;
import org.innovateuk.ifs.granttransfer.resource.EuGrantTransferResource;
import org.innovateuk.ifs.granttransfer.service.EuGrantTransferRestService;
import org.springframework.stereotype.Component;

@Component
public class GrantTransferDetailsSaver {

    private final EuGrantTransferRestService euGrantTransferRestService;

    public GrantTransferDetailsSaver(EuGrantTransferRestService euGrantTransferRestService) {
        this.euGrantTransferRestService = euGrantTransferRestService;
    }

    public RestResult<Void> save(GrantTransferDetailsForm grantTransferDetailsForm, long applicationId) {

        EuGrantTransferResource euGrantResource = euGrantTransferRestService.findDetailsByApplicationId(applicationId).getOptionalSuccessObject().orElse(new EuGrantTransferResource());

        euGrantResource.setGrantAgreementNumber(grantTransferDetailsForm.getGrantAgreementNumber());
        euGrantResource.setParticipantId(grantTransferDetailsForm.getParticipantId());
        euGrantResource.setProjectName(grantTransferDetailsForm.getProjectName());
        euGrantResource.setProjectStartDate(grantTransferDetailsForm.getStartDate());
        euGrantResource.setProjectEndDate(grantTransferDetailsForm.getEndDate());
        euGrantResource.setFundingContribution(grantTransferDetailsForm.getFundingContribution());
        euGrantResource.setProjectCoordinator(grantTransferDetailsForm.getProjectCoordinator());

        EuActionTypeResource euActionTypeResource = new EuActionTypeResource();
        euActionTypeResource.setId(grantTransferDetailsForm.getActionType());
        euGrantResource.setActionType(euActionTypeResource);

        return euGrantTransferRestService.updateGrantTransferDetails(euGrantResource, applicationId);
    }

}
