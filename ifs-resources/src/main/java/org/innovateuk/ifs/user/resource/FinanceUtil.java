package org.innovateuk.ifs.user.resource;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.springframework.stereotype.Component;

@Component
public class FinanceUtil {

    public boolean isUsingJesFinances(CompetitionResource competition, long organisationType) {
        return competition.showJesFinances(organisationType);
    }
}
