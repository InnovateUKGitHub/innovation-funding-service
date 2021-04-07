package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingrules;

import org.innovateuk.ifs.competition.resource.FundingRules;
import org.springframework.stereotype.Component;

@Component
public class StateAidTemplate implements FundingRulesTemplate {
    @Override
    public FundingRules type() {
        return FundingRules.STATE_AID;
    }
}
