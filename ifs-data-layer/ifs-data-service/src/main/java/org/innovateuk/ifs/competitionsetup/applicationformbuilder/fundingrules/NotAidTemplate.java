package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingrules;

import org.innovateuk.ifs.competition.resource.FundingRules;
import org.springframework.stereotype.Component;

@Component
public class NotAidTemplate implements FundingRulesTemplate {
    @Override
    public FundingRules type() {
        return FundingRules.NOT_AID;
    }
}
