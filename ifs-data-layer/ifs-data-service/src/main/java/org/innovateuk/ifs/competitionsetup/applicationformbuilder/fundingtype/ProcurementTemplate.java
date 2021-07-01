package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingtype;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.resource.SectionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder.aQuestionWithMultipleStatuses;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder.aSubSection;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.*;
import static org.innovateuk.ifs.project.internal.ProjectSetupStage.*;
import static org.innovateuk.ifs.project.internal.ProjectSetupStage.GRANT_OFFER_LETTER;

@Component
public class ProcurementTemplate implements FundingTypeTemplate {

    @Value("${ifs.procurement.milestones.enabled}")
    private boolean procurementMilestones;

    @Autowired
    private CommonBuilders commonBuilders;

    @Override
    public FundingType type() {
        return FundingType.PROCUREMENT;
    }

    @Override
    public List<SectionBuilder> sections(List<SectionBuilder> competitionTypeSections) {
        if (!procurementMilestones) {
            return competitionTypeSections;
        }

        competitionTypeSections.stream()
                .filter(section -> section.getType() == SectionType.FINANCES)
                .flatMap(section -> section.getChildSections().stream())
                .filter(section -> section.getType() == SectionType.FINANCE)
                .findAny()
                .ifPresent(this::addProcurementMilestoneSection);

        return competitionTypeSections;
    }

    private void addProcurementMilestoneSection(SectionBuilder financeSection) {
        financeSection.addChildSection(
                aSubSection()
                        .withName("Your payment milestones")
                        .withType(SectionType.PAYMENT_MILESTONES)
                        .withQuestions(newArrayList(
                                aQuestionWithMultipleStatuses()
                        ))
        );
    }


    @Override
    public Competition initialiseProjectSetupColumns(Competition competition) {
        commonBuilders.addProjectSetupStage(competition, PROJECT_DETAILS);
        commonBuilders.addProjectSetupStage(competition, PROJECT_TEAM);
        commonBuilders.addProjectSetupStage(competition, DOCUMENTS);
        commonBuilders.addProjectSetupStage(competition, MONITORING_OFFICER);
        commonBuilders.addProjectSetupStage(competition, BANK_DETAILS);
        commonBuilders.addProjectSetupStage(competition, FINANCE_CHECKS);
        commonBuilders.addProjectSetupStage(competition, GRANT_OFFER_LETTER);
        return competition;
    }

    @Override
    public Competition initialiseFinanceTypes(Competition competition) {
        List<FinanceRowType> types = newArrayList(LABOUR, PROCUREMENT_OVERHEADS, MATERIALS, CAPITAL_USAGE, SUBCONTRACTING_COSTS, TRAVEL, OTHER_COSTS, FINANCE, OTHER_FUNDING, VAT);
        return commonBuilders.saveFinanceRows(competition, types);
    }

    @Override
    public Competition setGolTemplate(Competition competition) {
        return commonBuilders.getGolTemplate(competition);
    }

    @Override
    public Competition overrideTermsAndConditions(Competition competition) {
        if (FundingRules.SUBSIDY_CONTROL == competition.getFundingRules()) {
            return competition;
        }
        return commonBuilders.overrideTermsAndConditions(competition);
    }
}
