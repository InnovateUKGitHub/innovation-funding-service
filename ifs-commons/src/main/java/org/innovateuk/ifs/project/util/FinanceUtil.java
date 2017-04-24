package org.innovateuk.ifs.project.util;

import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.springframework.stereotype.Component;

@Component
public class FinanceUtil {

    public boolean isUsingJesFinances(Long organisationType) {
        return OrganisationTypeEnum.RESEARCH.getId().equals(organisationType);
    }
}
