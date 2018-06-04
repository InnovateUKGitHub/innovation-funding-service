package org.innovateuk.ifs.user.resource;

import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.springframework.stereotype.Component;

@Component
public class FinanceUtil {

    public boolean isUsingJesFinances(Long organisationType) {
        return OrganisationTypeEnum.RESEARCH.getId().equals(organisationType);
    }
}
