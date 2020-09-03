package org.innovateuk.ifs.competitionsetup.applicationformbuilder;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.GrantTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.*;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.GuidanceRowBuilder.aGuidanceRow;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.QuestionBuilder.aDefaultAssessedQuestion;

@Component
public class GenericTemplate implements CompetitionTemplate {

    @Autowired
    private GrantTermsAndConditionsRepository grantTermsAndConditionsRepository;

    @Autowired
    private CommonBuilders commonBuilders;

    @Override
    public CompetitionTypeEnum type() {
        return CompetitionTypeEnum.GENERIC;
    }

    @Override
    public Competition copyTemplatePropertiesToCompetition(Competition competition) {
        competition.setGrantClaimMaximums(commonBuilders.getDefaultGrantClaimMaximums());
        competition.setTermsAndConditions(grantTermsAndConditionsRepository.findFirstByNameOrderByVersionDesc("Innovate UK"));
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
