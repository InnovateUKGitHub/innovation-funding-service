package org.innovateuk.ifs.application.forms.yourprojectcosts.viewmodel;

public class ManagementYourProjectCostsViewModel extends YourProjectCostsViewModel{

    public ManagementYourProjectCostsViewModel(long applicationId, long sectionId, long competitionId, String applicationName, String organisationName, String url) {
        super(
                applicationId,
                sectionId,
                competitionId,
                true,
                false,
                false,
                applicationName,
                organisationName,
                url);
    }
}
