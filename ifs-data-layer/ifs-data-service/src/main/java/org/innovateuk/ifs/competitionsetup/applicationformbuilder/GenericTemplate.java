package org.innovateuk.ifs.competitionsetup.applicationformbuilder;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.*;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.QuestionBuilder.aDefaultAssessedQuestion;

@Component
public class GenericTemplate implements CompetitionTemplate {
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
        return CompetitionTypeEnum.GENERIC;
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
                        .withQuestions(newArrayList(
                            aDefaultAssessedQuestion()
                                .withFormInputs(
                                    defaultAssessedQuestionFormInputs()
                                )
                        )),
                finances(),
                termsAndConditions()
        );

    }
}
