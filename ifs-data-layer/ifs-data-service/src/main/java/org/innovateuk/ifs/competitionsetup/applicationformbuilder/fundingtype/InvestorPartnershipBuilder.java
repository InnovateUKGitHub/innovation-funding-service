package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingtype;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.form.resource.SectionType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class InvestorPartnershipBuilder implements FundingTypeTemplate {

    private static final String TERMS_AND_CONDITIONS_INVESTOR_PARTNERSHIPS = "Investor Partnerships terms and conditions";

    @Override
    public FundingType type() {
        return FundingType.INVESTOR_PARTNERSHIPS;
    }

    @Override
    public List<SectionBuilder> sections(List<SectionBuilder> competitionTypeSections) {
        return overrideTermsAndConditionsTerminology(competitionTypeSections);
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

        return termsSection.map(Arrays::asList).orElse(Collections.emptyList());
    }
}
