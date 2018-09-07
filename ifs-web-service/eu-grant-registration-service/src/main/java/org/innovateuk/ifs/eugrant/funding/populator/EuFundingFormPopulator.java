package org.innovateuk.ifs.eugrant.funding.populator;

import org.innovateuk.ifs.eugrant.EuFundingResource;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.funding.form.EuFundingForm;
import org.innovateuk.ifs.eugrant.overview.service.EuGrantCookieService;
import org.springframework.stereotype.Component;

@Component
public class EuFundingFormPopulator {

    private EuGrantCookieService euGrantCookieService;

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
            fundingForm.setStartDateMonth(euFundingResource.getProjectStartDate().getMonthValue());
            fundingForm.setStartDateYear(euFundingResource.getProjectStartDate().getYear());
            fundingForm.setEndDateMonth(euFundingResource.getProjectEndDate().getMonthValue());
            fundingForm.setEndDateYear(euFundingResource.getProjectEndDate().getYear());
            fundingForm.setActionType(euFundingResource.getActionType().getId());
        }

        return fundingForm;

    }
}
