package org.innovateuk.ifs.eugrant.funding.saver;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.eugrant.EuActionTypeResource;
import org.innovateuk.ifs.eugrant.EuActionTypeRestService;
import org.innovateuk.ifs.eugrant.EuFundingResource;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.funding.form.EuFundingForm;
import org.innovateuk.ifs.eugrant.overview.service.EuGrantCookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class EuFundingSaver {

    @Autowired
    private EuGrantCookieService euGrantCookieService;

    @Autowired
    private EuActionTypeRestService euActionTypeRestService;

    public RestResult<Void> save(EuFundingForm fundingForm) {

        EuFundingResource euFundingResource = getEuFundingResource(fundingForm);

        EuGrantResource euGrantResource = euGrantCookieService.get();
        euGrantResource.setFunding(euFundingResource);

        euGrantCookieService.save(euGrantResource);
        return RestResult.restSuccess();
    }

    private EuFundingResource getEuFundingResource(EuFundingForm fundingForm) {

        EuActionTypeResource euActionTypeResource = euActionTypeRestService.getById(fundingForm.getActionType()).getSuccess();

        return new EuFundingResource(
                fundingForm.getGrantAgreementNumber(),
                fundingForm.getParticipantId(),
                fundingForm.getProjectName(),
                getLocalDate(fundingForm.getStartDateMonth(), fundingForm.getStartDateYear()),
                getLocalDate(fundingForm.getEndDateMonth(), fundingForm.getEndDateYear()),
                fundingForm.getFundingContribution(),
                fundingForm.isProjectCoordinator(),
                euActionTypeResource
        );
    }

    private LocalDate getLocalDate(int month, int year) {

        String date = String.valueOf(year) + "-" + String.valueOf(month) + "-01";

        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

}
