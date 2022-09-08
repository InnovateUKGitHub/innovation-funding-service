package org.innovateuk.ifs.competitionsetup.applicationformbuilder.template;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.GrantTermsAndConditions;
import org.innovateuk.ifs.competition.repository.GrantTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.*;

@Component
public class ApcTemplate implements CompetitionTemplate {

    @Autowired
    private GrantTermsAndConditionsRepository grantTermsAndConditionsRepository;

    @Override
    public CompetitionTypeEnum type() {
        return CompetitionTypeEnum.ADVANCED_PROPULSION_CENTRE;
    }

    @Override
    public Competition copyTemplatePropertiesToCompetition(Competition competition) {

        GrantTermsAndConditions apcTerms = grantTermsAndConditionsRepository.findFirstByNameOrderByVersionDesc("Advanced Propulsion Centre (APC)");

        if (FundingRules.SUBSIDY_CONTROL == competition.getFundingRules()) {
            GrantTermsAndConditions subsidyControlTermsAndConditions = grantTermsAndConditionsRepository.findFirstByNameOrderByVersionDesc("Advanced Propulsion Centre (APC) - Subsidy control");
            if (subsidyControlTermsAndConditions != null) {
                competition.setTermsAndConditions(subsidyControlTermsAndConditions);
            } else {
                competition.setTermsAndConditions(apcTerms);
            }
            competition.setOtherFundingRulesTermsAndConditions(apcTerms);
        } else {
            competition.setTermsAndConditions(apcTerms);
        }
        competition.setAcademicGrantPercentage(100);
        competition.setMinProjectDuration(1);
        competition.setMaxProjectDuration(36);
        return competition;
    }

    @Override
    public List<SectionBuilder> sections() {
        return newArrayList(
                projectDetails()
                        .withQuestions(newArrayList(
                                applicationTeam(),
                                applicationDetails(),
                                researchCategory(),
                                equalityDiversityAndInclusion(),
                                projectSummary(),
                                publicDescription(),
                                scope()
                        )),
                applicationQuestions()
                        .withQuestions(apcDefaultQuestions()),
                finances(),
                termsAndConditions()
        );
    }

    public static List<QuestionBuilder> apcDefaultQuestions() {
        List<QuestionBuilder> programmeQuestions = ProgrammeTemplate.programmeDefaultQuestions();
        programmeQuestions.stream()
                .filter(question -> question.getName().equals("Funding"))
                .findFirst()
                .flatMap(question -> question.getFormInputs().stream()
                        .filter(formInput -> formInput.getScope() == FormInputScope.ASSESSMENT && formInput.getType() == FormInputType.TEXTAREA)
                        .findFirst()
                )
                .ifPresent(formInput -> formInput.withGuidanceAnswer("Guidance for assessing financial commitment"));
        return programmeQuestions;
    }
}
