package org.innovateuk.ifs.competitionsetup.applicationformbuilder;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.*;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.QuestionBuilder.aQuestion;

@Component
public class Horizon2020Template implements CompetitionTemplate {

    @Override
    public Competition copyTemplatePropertiesToCompetition(Competition competition) {
        //todo remove dependency on template comp.
//        competition.setGrantClaimMaximums(new ArrayList<>(template.getGrantClaimMaximums()));
//        competition.setTermsAndConditions(template.getTermsAndConditions());
//        competition.setAcademicGrantPercentage(template.getAcademicGrantPercentage());
//        competition.setMinProjectDuration(template.getMinProjectDuration());
//        competition.setMaxProjectDuration(template.getMaxProjectDuration());
//        competition.setApplicationFinanceType(template.getApplicationFinanceType());
        return competition;
    }


    @Override
    public CompetitionTypeEnum type() {
        return CompetitionTypeEnum.HORIZON_2020;
    }

    @Override
    public List<SectionBuilder> sections() {
        return newArrayList(
                projectDetails()
                        .withQuestions(newArrayList(
                                applicationDetails()
                                        .withQuestionSetupType(QuestionSetupType.GRANT_TRANSFER_DETAILS),
                                applicationTeam(),
                                aQuestion()
                                    .withShortName("Horizon 2020 grant agreement")
                                    .withName("Horizon 2020 grant agreement")
                                    .withType(QuestionType.LEAD_ONLY)
                                    .withQuestionSetupType(QuestionSetupType.GRANT_AGREEMENT),
                                publicDescription(),
                                equalityDiversityAndInclusion()
                        )),
                finances(),
                termsAndConditions()
        );
    }

}
