package org.innovateuk.ifs.eugrant.funding.populator;

import org.innovateuk.ifs.eugrant.EuFundingResource;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.funding.form.EuFundingForm;
import org.innovateuk.ifs.eugrant.overview.service.EuGrantCookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EuFundingFormPopulator {

    private EuGrantCookieService euGrantCookieService;

    public EuFundingFormPopulator() {
    }

    @Autowired
    public EuFundingFormPopulator(EuGrantCookieService euGrantCookieService) {
        this.euGrantCookieService = euGrantCookieService;
    }

    public EuFundingForm populate(EuFundingForm fundingForm) {

        EuGrantResource euGrantResource = euGrantCookieService.get();
        EuFundingResource euFundingResource = euGrantResource.getFunding();

        if (euFundingResource != null) {
            fundingForm.setFundingContribution(euFundingResource.getFundingContribution());
            fundingForm.setGrantAgreementNumber(euFundingResource.getGrantAgreementNumber());
            fundingForm.setParticipantId(euFundingResource.getParticipantId());
            fundingForm.setProjectCoordinator(euFundingResource.isProjectCoordinator());
            fundingForm.setProjectName(euFundingResource.getProjectName());
            fundingForm.setProjectStartDate(euFundingResource.getProjectStartDate());
            fundingForm.setProjectEndDate(euFundingResource.getProjectEndDate());
            fundingForm.setActionType(euFundingResource.getActionType().getId());
        }

        return fundingForm;

    }
}
