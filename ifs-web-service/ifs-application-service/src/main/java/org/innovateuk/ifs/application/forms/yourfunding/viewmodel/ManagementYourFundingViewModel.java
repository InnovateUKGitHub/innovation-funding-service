package org.innovateuk.ifs.application.forms.yourfunding.viewmodel;

public class ManagementYourFundingViewModel extends YourFundingViewModel {

    public ManagementYourFundingViewModel(long applicationId, long sectionId, long competitionId, String applicationName, String url) {
        super(
                applicationId,
                sectionId,
                competitionId,
                true,
                false,
                false,
                true,
                applicationName,
                false,
        false,
        false,
        0L,
        0L,
                0,
                url);
    }
}
