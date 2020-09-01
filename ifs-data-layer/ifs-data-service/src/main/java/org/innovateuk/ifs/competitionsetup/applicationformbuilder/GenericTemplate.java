package org.innovateuk.ifs.competitionsetup.applicationformbuilder;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.*;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.GuidanceRowBuilder.aGuidanceRow;
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
                                                defaultAssessedQuestionFormInputs(Function.identity(),
                                                        assessorInputBuilder ->
                                                                assessorInputBuilder.withGuidanceRows(newArrayList(
                                                                        aGuidanceRow()
                                                                                .withSubject("9,10"),
                                                                        aGuidanceRow()
                                                                                .withSubject("7,8"),
                                                                        aGuidanceRow()
                                                                                .withSubject("5,6"),
                                                                        aGuidanceRow()
                                                                                .withSubject("3,4"),
                                                                        aGuidanceRow()
                                                                                .withSubject("1,2")
                                                                ))
                                                        )
                                        )
                        )),
                finances(),
                termsAndConditions()
        );

    }
}
