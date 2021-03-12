package org.innovateuk.ifs.application.forms.sections.yourfunding.viewmodel;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;

public class ManagementYourFundingViewModel extends YourFundingViewModel {

    public ManagementYourFundingViewModel(long applicationId,
                                          String competitionName,
                                          long sectionId,
                                          long organisationId,
                                          long competitionId,
                                          String applicationName,
                                          String url,
                                          FundingType fundingType,
                                          OrganisationTypeEnum organisationType) {
        super(
                applicationId,
                competitionName,
                sectionId,
                organisationId,
                competitionId,
                true,
                false,
                false,
                true,
                applicationName,
                false,
                false,
                false,
                false,
                0L,
                0L,
                0L,
                0,
                url,
                false,
                fundingType,
                organisationType
        );
    }
}