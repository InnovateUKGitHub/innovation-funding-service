package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingtype;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.resource.SectionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.*;

@Component
public class InvestorPartnershipTemplate implements FundingTypeTemplate {

    private static final String TERMS_AND_CONDITIONS_INVESTOR_PARTNERSHIPS = "Investor Partnerships terms and conditions";

    @Autowired
    private CommonBuilders commonBuilders;

    @Override
    public FundingType type() {
        return FundingType.INVESTOR_PARTNERSHIPS;
    }

    @Override
    public List<SectionBuilder> sections(List<SectionBuilder> competitionTypeSections) {
        return overrideTermsAndConditionsTerminology(competitionTypeSections);
    }

    @Override
    public Competition setGolTemplate(Competition competition) {
        return commonBuilders.getGolTemplate(competition);
    }

    @Override
    public Competition initialiseFinanceTypes(Competition competition) {
        List<FinanceRowType> types = newArrayList(LABOUR, OVERHEADS, MATERIALS, CAPITAL_USAGE, SUBCONTRACTING_COSTS, TRAVEL, OTHER_COSTS, FINANCE, OTHER_FUNDING, YOUR_FINANCE);
        return commonBuilders.saveFinanceRows(competition, types);
    }

    private List<SectionBuilder> overrideTermsAndConditionsTerminology(List<SectionBuilder> sections) {
        Optional<SectionBuilder> termsSection = sections.stream()
                .filter(sectionBuilder -> sectionBuilder.getType() == SectionType.TERMS_AND_CONDITIONS)
                .findFirst();

        termsSection.ifPresent(sectionBuilder -> sectionBuilder.getQuestions().forEach(questionBuilder -> {
            questionBuilder.withDescription(TERMS_AND_CONDITIONS_INVESTOR_PARTNERSHIPS)
                    .withName(TERMS_AND_CONDITIONS_INVESTOR_PARTNERSHIPS)
                    .withShortName(TERMS_AND_CONDITIONS_INVESTOR_PARTNERSHIPS);
        }));

        return sections;
    }

    @Override
    public Competition overrideTermsAndConditions(Competition competition) {
        return commonBuilders.overrideTermsAndConditions(competition);
    }

}
