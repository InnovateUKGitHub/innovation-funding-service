package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingrules;

import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.northernIrelandDeclaration;

@Component
public class SubsidyControlTemplate implements FundingRulesTemplate {

    @Value("${ifs.subsidy.control.northern.ireland.enabled}")
    private boolean northernIrelandSubsidyControlToggle;

    @Override
    public FundingRules type() {
        return FundingRules.SUBSIDY_CONTROL;
    }

    @Override
    public List<SectionBuilder> sections(List<SectionBuilder> sectionBuilders) {
        if (northernIrelandSubsidyControlToggle) {
            insertNorthernIrelandDeclaration(sectionBuilders);
        }
        return sectionBuilders;
    }

    private void insertNorthernIrelandDeclaration(List<SectionBuilder> sectionBuilders) {
        sectionBuilders.stream()
                .filter(section -> "Project details".equals(section.getName()))
                .findAny()
                .ifPresent(section -> section.getQuestions().add(0, northernIrelandDeclaration()));
    }
}
