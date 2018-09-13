package org.innovateuk.ifs.eugrant.overview.populator;

import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.overview.service.EuGrantCookieService;
import org.innovateuk.ifs.eugrant.overview.viewmodel.EuGrantOverviewViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EuGrantOverviewViewModelPopulator {

    @Autowired
    private EuGrantCookieService euGrantCookieService;

    public EuGrantOverviewViewModel populate() {
        EuGrantResource euGrant = euGrantCookieService.get();
        return new EuGrantOverviewViewModel(euGrant.isOrganisationComplete(), euGrant.isContactComplete(), euGrant.isFundingComplete());
    }
}
