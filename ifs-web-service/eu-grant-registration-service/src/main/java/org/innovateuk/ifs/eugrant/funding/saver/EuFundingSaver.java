package org.innovateuk.ifs.eugrant.funding.saver;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.eugrant.EuActionTypeResource;
import org.innovateuk.ifs.eugrant.EuFundingResource;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.funding.form.EuFundingForm;
import org.innovateuk.ifs.eugrant.overview.service.EuGrantCookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EuFundingSaver {

    @Autowired
    private EuGrantCookieService euGrantCookieService;

    public RestResult<Void> save(EuFundingForm fundingForm) {

        EuFundingResource euFundingResource = getEuFundingResource(fundingForm);

        EuGrantResource euGrantResource = euGrantCookieService.get();
        euGrantResource.setFunding(euFundingResource);

        euGrantCookieService.save(euGrantResource);
        return RestResult.restSuccess();
    }

    private EuFundingResource getEuFundingResource(EuFundingForm fundingForm) {

        EuActionTypeResource euActionTypeResource = new EuActionTypeResource();
        euActionTypeResource.setId(fundingForm.getActionType());

        return new EuFundingResource(
                fundingForm.getGrantAgreementNumber(),
                fundingForm.getParticipantId(),
                fundingForm.getProjectName(),
                fundingForm.getStartDate(),
                fundingForm.getEndDate(),
                fundingForm.getFundingContribution(),
                fundingForm.getProjectCoordinator(),
                euActionTypeResource
        );
    }
}
