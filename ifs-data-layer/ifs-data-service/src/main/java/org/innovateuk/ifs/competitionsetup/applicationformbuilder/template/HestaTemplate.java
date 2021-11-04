package org.innovateuk.ifs.competitionsetup.applicationformbuilder.template;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.GrantTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.competition.resource.CompetitionCompletionStage.PROJECT_SETUP;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.*;
import static org.innovateuk.ifs.form.resource.FormInputScope.ASSESSMENT;

@Component
public class HestaTemplate implements CompetitionTemplate {

    @Autowired
    private GrantTermsAndConditionsRepository grantTermsAndConditionsRepository;

    @Override
    public CompetitionTypeEnum type() {
        return CompetitionTypeEnum.HESTA;
    }

    @Override
    public Competition copyTemplatePropertiesToCompetition(Competition competition) {
        if (FundingRules.SUBSIDY_CONTROL == competition.getFundingRules()) {
            competition.setTermsAndConditions(grantTermsAndConditionsRepository.findFirstByNameOrderByVersionDesc("Innovate UK - Subsidy control"));
            competition.setOtherFundingRulesTermsAndConditions(grantTermsAndConditionsRepository.findFirstByNameOrderByVersionDesc("Innovate UK"));
        } else {
            competition.setTermsAndConditions(grantTermsAndConditionsRepository.findFirstByNameOrderByVersionDesc("Innovate UK"));
        }
        competition.setAcademicGrantPercentage(100);
        competition.setMinProjectDuration(1);
        competition.setMaxProjectDuration(84);
        return competition;
    }

    @Override
    public List<SectionBuilder> sections() {
        return newArrayList(
                projectDetails()
                        .withQuestions(newArrayList(
                                applicationTeam(),
                                applicationDetails()
                        )),
                applicationQuestions()
                        .withQuestions(newArrayList(
                                hestaDefaultQuestions()
                        )),
                finances(),
                termsAndConditions()
        );
    }

    public static QuestionBuilder hestaDefaultQuestions() {
        QuestionBuilder hestaQuestion = genericQuestion();
        hestaQuestion.getFormInputs().stream()
                .filter(fi -> fi.getScope().equals(ASSESSMENT))
                .forEach(fi -> fi.withActive(false));
        return hestaQuestion;
    }
}