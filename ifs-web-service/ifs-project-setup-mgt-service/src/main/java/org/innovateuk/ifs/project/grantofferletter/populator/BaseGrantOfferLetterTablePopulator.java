package org.innovateuk.ifs.project.grantofferletter.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.FinanceUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base class for grant offer letter finance table populators
 **/

public class BaseGrantOfferLetterTablePopulator {

    @Autowired
    private FinanceUtil financeUtil;

    protected boolean isAcademic(OrganisationResource organisation, CompetitionResource competition) {
        return financeUtil.isUsingJesFinances(competition, organisation.getOrganisationType());
    }
}
