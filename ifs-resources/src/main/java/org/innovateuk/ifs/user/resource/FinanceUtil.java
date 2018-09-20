package org.innovateuk.ifs.user.resource;

import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.springframework.stereotype.Component;

@Component
public class FinanceUtil {

    public boolean isUsingJesFinances(long organisationType) {
        return OrganisationTypeEnum.RESEARCH.getId() == organisationType;
    }
}
