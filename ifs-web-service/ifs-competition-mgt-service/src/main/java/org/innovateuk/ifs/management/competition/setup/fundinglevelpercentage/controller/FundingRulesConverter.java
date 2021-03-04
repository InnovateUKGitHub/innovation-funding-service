package org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.controller;

import org.innovateuk.ifs.competition.resource.FundingRules;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class FundingRulesConverter implements Converter<String, FundingRules> {

    @Override
    public FundingRules convert(String source) {
        return FundingRules.fromUrl(source);
    }
}